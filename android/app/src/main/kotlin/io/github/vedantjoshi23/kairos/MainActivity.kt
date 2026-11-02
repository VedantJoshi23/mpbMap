package io.github.vedantjoshi23.kairos

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.vedantjoshi23.kairos.core.config.ConfigResolver
import io.github.vedantjoshi23.kairos.core.designsystem.KairosTheme
import io.github.vedantjoshi23.kairos.core.kernel.Geo
import io.github.vedantjoshi23.kairos.core.kernel.LatLng
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentApi
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentKind
import io.github.vedantjoshi23.kairos.feature.location.api.LocationAccessProvider
import io.github.vedantjoshi23.kairos.feature.location.api.LocationFixDto
import io.github.vedantjoshi23.kairos.feature.location.api.LocationSource
import io.github.vedantjoshi23.kairos.feature.location.ui.DisclosureScreen
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

/** The composition root's only screen in slice 1 (map long-press destinations, J1/J3). */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var location: LocationSource
    @Inject lateinit var access: LocationAccessProvider
    @Inject lateinit var consent: ConsentApi
    @Inject lateinit var config: ConfigResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra(AlarmActivity.EXTRA_RESUME, false)) {
            // Resume after an interruption is always a foreground user action (R25, R5).
            startTripService(Intent(this, TripService::class.java).setAction(TripService.ACTION_RESUME))
        }
        val crash = CrashGuard.lastCrash(this)
        setContent {
            KairosTheme {
                Surface(Modifier.fillMaxSize()) {
                    var report by remember { mutableStateOf(crash) }
                    var styleAttempt by remember { mutableStateOf(0) }
                    val style = remember(styleAttempt) { loadStyle() }
                    when {
                        report != null -> CrashReportScreen(report!!) { CrashGuard.clear(this@MainActivity); report = null }
                        style.isFailure -> ErrorCard(
                            "The map couldn't load",
                            style.exceptionOrNull()?.let { "${it::class.java.simpleName}: ${it.message}" } ?: "Unknown error",
                        ) { styleAttempt++ }
                        else -> AppRoot(style.getOrThrow())
                    }
                }
            }
        }
    }

    /** Error boundary: a bad style or asset shows an error card, never a crash. */
    private fun loadStyle(): Result<String> = runCatching {
        SkinAssets.load(this, "terrain-sepia", config.resolve(null, null).config.tileUrl)
    }.onFailure { CrashGuard.record(this, "loadStyle", it) }

    /** Error boundary for starting the trip service (e.g. a background-start restriction). */
    private fun startTripService(intent: Intent): Boolean = runCatching {
        ContextCompat.startForegroundService(this, intent)
    }.onFailure {
        CrashGuard.record(this, "startTripService", it)
        android.widget.Toast.makeText(this, "Couldn't start the trip: ${it::class.java.simpleName}", android.widget.Toast.LENGTH_LONG).show()
    }.isSuccess

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppRoot(style: String) {
        val scope = rememberCoroutineScope()
        var showDisclosure by remember { mutableStateOf(false) }
        var disclosed by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { disclosed = consent.isInForce(ConsentKind.LOCATION_DISCLOSURE) }
        var pendingAfterConsent by remember { mutableStateOf<(() -> Unit)?>(null) }
        var me by remember { mutableStateOf<LocationFixDto?>(null) }
        var center by remember { mutableStateOf<Pair<Double, Double>?>(null) }
        var destination by remember { mutableStateOf<LatLng?>(null) }
        val tripActive by TripService.active.collectAsState()
        val ctx = LocalContext.current

        val permissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            pendingAfterConsent?.invoke(); pendingAfterConsent = null
        }

        fun withLocation(action: () -> Unit) {
            scope.launch {
                if (consent.isInForce(ConsentKind.LOCATION_DISCLOSURE)) {
                    pendingAfterConsent = action
                    permissions.launch(requiredPermissions())
                } else {
                    pendingAfterConsent = action
                    showDisclosure = true
                }
            }
        }

        fun locateOnce() = scope.launch {
            val fix = runCatching { withTimeoutOrNull(15_000) { location.fixes(tripActive = false).firstOrNull() } }
                .onFailure { CrashGuard.record(ctx, "locateOnce", it) }
                .getOrNull()
            if (fix != null) { me = fix; center = fix.position.lat to fix.position.lon }
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

        val overlays = buildList {
            me?.let { add(Overlay("me", it.position.lat, it.position.lon, "#0E6B6A", 7f)) }
            destination?.let { add(Overlay("quest", it.lat, it.lon, "#B4530A", 10f)) }
        }
        var mapError by remember { mutableStateOf<Throwable?>(null) }
        mapError?.let { e ->
            ErrorCard("The map couldn't start", "${e::class.java.simpleName}: ${e.message}") { mapError = null }
            return
        }
        MapScreen(
            onMapError = { e -> CrashGuard.record(ctx, "map", e); mapError = e },
            styleJson = style,
            overlays = overlays,
            centerOn = center,
            onLongPress = { lat, lon -> if (!tripActive) destination = LatLng(lat, lon) },
            onLocateMe = { withLocation { locateOnce() } },
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

    private fun requiredPermissions(): Array<String> = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
    }.toTypedArray()

    /** R26 inputs, read fresh on every start. */
    private fun preflightInput(ctx: Context, smallestRadiusM: Double, disclosed: Boolean): PreflightInput {
        val a = access.current()
        val audio = ctx.getSystemService(AudioManager::class.java)
        val nm = ctx.getSystemService(NotificationManager::class.java)
        return PreflightInput(
            // Also re-checked when the service starts collecting fixes (Law 3).
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
}
