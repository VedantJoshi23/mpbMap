package io.github.vedantjoshi23.kairos

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.vedantjoshi23.kairos.core.config.ConfigResolver
import io.github.vedantjoshi23.kairos.core.designsystem.KairosTheme
import io.github.vedantjoshi23.kairos.core.kernel.Geo
import io.github.vedantjoshi23.kairos.core.kernel.LatLng
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentApi
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentKind
import io.github.vedantjoshi23.kairos.feature.location.api.DevMovementSimulator
import io.github.vedantjoshi23.kairos.feature.location.api.LocationAccessProvider
import io.github.vedantjoshi23.kairos.feature.location.api.LocationFixDto
import io.github.vedantjoshi23.kairos.feature.location.api.LocationSource
import io.github.vedantjoshi23.kairos.feature.location.ui.DisclosureScreen
import io.github.vedantjoshi23.kairos.feature.map.domain.SkinMeta
import io.github.vedantjoshi23.kairos.feature.map.platform.DeviceClass
import io.github.vedantjoshi23.kairos.feature.map.platform.Overlay
import io.github.vedantjoshi23.kairos.feature.map.platform.SkinAssets
import io.github.vedantjoshi23.kairos.feature.map.ui.MapScreen
import io.github.vedantjoshi23.kairos.feature.trip.domain.PreflightInput
import io.github.vedantjoshi23.kairos.feature.trip.domain.PreflightItem
import io.github.vedantjoshi23.kairos.feature.trip.platform.AlarmActivity
import io.github.vedantjoshi23.kairos.feature.trip.platform.TripService
import io.github.vedantjoshi23.kairos.feature.trip.ui.ArmContext
import io.github.vedantjoshi23.kairos.feature.trip.ui.ArmTripSheet
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

/** The composition root's screen: map, skins, long-press destinations and the trip flow (J1, J2, J3). */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var location: LocationSource
    @Inject lateinit var access: LocationAccessProvider
    @Inject lateinit var consent: ConsentApi
    @Inject lateinit var simulator: DevMovementSimulator

    // Lazy: built inside the style error boundary, never during injection.
    @Inject lateinit var config: dagger.Lazy<ConfigResolver>

    private val settings by lazy { MapSettings(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra(AlarmActivity.EXTRA_RESUME, false)) {
            // Resume after an interruption is always a foreground user action (R25, R5).
            startTripService(Intent(this, TripService::class.java).setAction(TripService.ACTION_RESUME))
        }
        val crash = CrashGuard.lastCrash(this)
        val catalog = runCatching { SkinAssets.catalog(this) }.onFailure { CrashGuard.record(this, "skinCatalog", it) }.getOrDefault(emptyList())
        val defaultFlat = DeviceClass.prefers2D(this)
        setContent {
            KairosTheme {
                Surface(Modifier.fillMaxSize()) {
                    var report by remember { mutableStateOf(crash) }
                    val skinId by settings.skin(DEFAULT_SKIN).collectAsState(initial = DEFAULT_SKIN)
                    val flat by settings.flat(defaultFlat).collectAsState(initial = defaultFlat)
                    var styleAttempt by remember { mutableStateOf(0) }
                    val skin = catalog.firstOrNull { it.id == skinId } ?: catalog.firstOrNull { it.id == DEFAULT_SKIN }
                    val style = remember(skin, flat, styleAttempt) { loadStyle(skin, flat) }
                    when {
                        report != null -> CrashReportScreen(report!!) { CrashGuard.clear(this@MainActivity); report = null }
                        skin == null || style.isFailure -> ErrorCard(
                            "The map couldn't load",
                            style.exceptionOrNull()?.let { "${it::class.java.simpleName}: ${it.message}" } ?: "No skins were found in the app.",
                        ) { styleAttempt++ }
                        else -> AppRoot(style.getOrThrow(), catalog, skin, flat)
                    }
                }
            }
        }
    }

    /** Error boundary: a bad style or asset shows an error card, never a crash. */
    private fun loadStyle(skin: SkinMeta?, flat: Boolean): Result<String> = runCatching {
        requireNotNull(skin) { "no skin" }
        SkinAssets.load(this, skin, config.get().resolve(null, null).config.tileUrl, flatten = flat)
    }.onFailure { CrashGuard.record(this, "loadStyle", it) }

    /** Error boundary for starting the trip service (e.g. a background-start restriction). */
    private fun startTripService(intent: Intent): Boolean = runCatching {
        ContextCompat.startForegroundService(this, intent)
    }.onFailure {
        CrashGuard.record(this, "startTripService", it)
        Toast.makeText(this, "Couldn't start the trip: ${it::class.java.simpleName}", Toast.LENGTH_LONG).show()
    }.isSuccess

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppRoot(style: String, catalog: List<SkinMeta>, skin: SkinMeta, flat: Boolean) {
        val scope = rememberCoroutineScope()
        val ctx = LocalContext.current
        var showDisclosure by remember { mutableStateOf(false) }
        var disclosed by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { disclosed = consent.isInForce(ConsentKind.LOCATION_DISCLOSURE) }
        var pendingAfterConsent by remember { mutableStateOf<(() -> Unit)?>(null) }
        var me by remember { mutableStateOf<LocationFixDto?>(null) }
        var center by remember { mutableStateOf<Pair<Double, Double>?>(null) }
        var destination by remember { mutableStateOf<LatLng?>(null) }
        var tripDestination by remember { mutableStateOf<LatLng?>(null) }
        var mapError by remember { mutableStateOf<Throwable?>(null) }
        val tripActive by TripService.active.collectAsState()
        val simulating by simulator.active.collectAsState()

        val permissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            pendingAfterConsent?.invoke(); pendingAfterConsent = null
        }

        fun withLocation(action: () -> Unit) {
            scope.launch {
                pendingAfterConsent = action
                if (consent.isInForce(ConsentKind.LOCATION_DISCLOSURE)) permissions.launch(requiredPermissions()) else showDisclosure = true
            }
        }

        fun locateOnce() = scope.launch {
            val fix = runCatching { withTimeoutOrNull(15_000) { location.fixes(tripActive = false).firstOrNull() } }
                .onFailure { CrashGuard.record(ctx, "locateOnce", it) }
                .getOrNull()
            if (fix != null) { me = fix; center = fix.position.lat to fix.position.lon }
        }

        // While a trip or a simulation runs, follow the fixes so the "You" marker moves.
        LaunchedEffect(tripActive, simulating) {
            if (!tripActive && !simulating) return@LaunchedEffect
            runCatching { location.fixes(tripActive = false).collect { me = it } }
                .onFailure { CrashGuard.record(ctx, "followFixes", it) }
        }

        if (showDisclosure) {
            DisclosureScreen(
                onAccept = {
                    scope.launch {
                        consent.record(ConsentKind.LOCATION_DISCLOSURE, granted = true)
                        disclosed = true
                        showDisclosure = false
                        permissions.launch(requiredPermissions())
                    }
                },
                onDecline = {
                    scope.launch { consent.record(ConsentKind.LOCATION_DISCLOSURE, granted = false) }
                    showDisclosure = false; pendingAfterConsent = null
                },
            )
            return
        }

        mapError?.let { e ->
            ErrorCard("The map couldn't start", "${e::class.java.simpleName}: ${e.message}") { mapError = null }
            return
        }

        val overlays = buildList {
            me?.let { add(Overlay("me", Overlay.Kind.ME, it.position.lat, it.position.lon, "You")) }
            (tripDestination.takeIf { tripActive } ?: destination)?.let {
                add(Overlay("destination", Overlay.Kind.DESTINATION, it.lat, it.lon, "Destination"))
            }
        }
        MapScreen(
            onMapError = { e -> CrashGuard.record(ctx, "map", e); mapError = e },
            styleJson = style,
            overlays = overlays,
            centerOn = center,
            onLongPress = { lat, lon -> if (!tripActive) destination = LatLng(lat, lon) },
            onLocateMe = { withLocation { locateOnce() } },
            skins = catalog,
            selectedSkin = skin.id,
            onSelectSkin = { id -> scope.launch { settings.setSkin(id) } },
            flat = flat,
            onFlatChange = { f -> scope.launch { settings.setFlat(f) } },
            showGrid = skin.grid,
            hint = if (!tripActive && destination == null) "Long-press anywhere on the map to set an arrival alarm." else null,
            bottomPanel = {
                if (tripActive) {
                    TripBanner(
                        simulatorAvailable = simulator.available,
                        simulating = simulating,
                        onEnd = { startTripService(Intent(ctx, TripService::class.java).setAction(TripService.ACTION_END)); simulator.stop() },
                        onSimulate = { kmh ->
                            val to = tripDestination ?: return@TripBanner
                            val from = me?.position ?: offsetNorth(to, 15_000.0)
                            simulator.start(from, to, kmh / 3.6)
                        },
                        onStopSimulation = { simulator.stop() },
                    )
                }
            },
        )

        val dest = destination
        if (dest != null && !tripActive) {
            ModalBottomSheet(onDismissRequest = { destination = null }) {
                ArmTripSheet(
                    destinationLabel = "the pinned place",
                    context = ArmContext(
                        tripDistanceM = me?.let { Geo.distanceMeters(it.position, dest) },
                        preflight = { smallest -> preflightInput(ctx, smallest, disclosed) },
                    ),
                    onStart = { mode, radii ->
                        withLocation {
                            if (startTripService(TripService.startIntent(ctx, dest.lat, dest.lon, "the pinned place", mode.name, radii.toDoubleArray()))) {
                                tripDestination = dest
                                destination = null
                            }
                        }
                    },
                    onFix = { item -> fix(item, ctx) { withLocation {} } },
                    onCancel = { destination = null },
                )
            }
        }
    }

    @Composable
    private fun TripBanner(
        simulatorAvailable: Boolean,
        simulating: Boolean,
        onEnd: () -> Unit,
        onSimulate: (kmh: Double) -> Unit,
        onStopSimulation: () -> Unit,
    ) {
        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primary) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Trip active: your arrival alarm is armed.", color = MaterialTheme.colorScheme.onPrimary)
                Button(onClick = onEnd) { Text("End trip") }
                if (simulatorAvailable) {
                    Text(
                        if (simulating) "DEV: simulating movement" else "DEV: simulate movement toward the destination",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(30.0, 100.0, 300.0).forEach { kmh ->
                            OutlinedButton(onClick = { onSimulate(kmh) }) { Text("${kmh.toInt()} km/h", color = MaterialTheme.colorScheme.onPrimary) }
                        }
                        if (simulating) OutlinedButton(onClick = onStopSimulation) { Text("Stop", color = MaterialTheme.colorScheme.onPrimary) }
                    }
                }
            }
        }
    }

    /** With no known position, a simulation starts 15 km north of the destination. */
    private fun offsetNorth(p: LatLng, meters: Double): LatLng =
        LatLng((p.lat + meters / 111_195.0).coerceAtMost(89.0), p.lon)

    private fun requiredPermissions(): Array<String> = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
    }.toTypedArray()

    /** R26 inputs, read fresh on every start. A running simulation stands in for a fix source. */
    private fun preflightInput(ctx: Context, smallestRadiusM: Double, disclosed: Boolean): PreflightInput {
        val a = access.current()
        val audio = ctx.getSystemService(AudioManager::class.java)
        val nm = ctx.getSystemService(NotificationManager::class.java)
        return PreflightInput(
            disclosureRecorded = disclosed,
            locationPermissionGranted = a.permissionGranted,
            systemLocationOn = a.systemLocationOn,
            fixSourceAvailable = a.fixSourceAvailable,
            preciseGranted = a.precise,
            reportedAccuracyM = null,
            notificationsGranted = NotificationManagerCompat.from(ctx).areNotificationsEnabled(),
            alarmVolumeAboveZero = audio.getStreamVolume(AudioManager.STREAM_ALARM) > 0,
            fullScreenIntentAvailable = if (Build.VERSION.SDK_INT >= 34) nm.canUseFullScreenIntent() else true,
            smallestRadiusM = smallestRadiusM,
        )
    }

    private fun fix(item: PreflightItem, ctx: Context, requestLocation: () -> Unit) {
        when (item) {
            PreflightItem.SYSTEM_LOCATION -> ctx.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            PreflightItem.FULL_SCREEN_INTENT -> if (Build.VERSION.SDK_INT >= 34) {
                ctx.startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).setData(android.net.Uri.parse("package:" + ctx.packageName)))
            }
            PreflightItem.NOTIFICATIONS -> ctx.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName),
            )
            else -> requestLocation()
        }
    }

    companion object {
        const val DEFAULT_SKIN = "terrain-sepia"
    }
}
