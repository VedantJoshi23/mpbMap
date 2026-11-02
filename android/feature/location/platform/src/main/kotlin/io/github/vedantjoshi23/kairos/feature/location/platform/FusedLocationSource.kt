package io.github.vedantjoshi23.kairos.feature.location.platform

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.vedantjoshi23.kairos.core.kernel.LatLng
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentApi
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentKind
import io.github.vedantjoshi23.kairos.feature.location.api.DevMovementSimulator
import io.github.vedantjoshi23.kairos.feature.location.api.LocationAccess
import io.github.vedantjoshi23.kairos.feature.location.api.LocationAccessProvider
import io.github.vedantjoshi23.kairos.feature.location.api.LocationFixDto
import io.github.vedantjoshi23.kairos.feature.location.api.LocationSource
import io.github.vedantjoshi23.kairos.feature.location.data.ConsentStore
import io.github.vedantjoshi23.kairos.feature.location.domain.CollectionPolicy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton

/** The only code that touches platform location APIs (Law 13; ARCH-SYSTEM §4 rule 3). */
@Singleton
class AndroidLocationAccess @Inject constructor(@ApplicationContext private val context: Context) : LocationAccessProvider {
    @Volatile var disclosureRecorded: Boolean = false

    override fun current(): LocationAccess {
        val fine = granted(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = granted(Manifest.permission.ACCESS_COARSE_LOCATION)
        val lm = context.getSystemService(LocationManager::class.java)
        val gms = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        return LocationAccess(
            disclosureRecorded = disclosureRecorded,
            permissionGranted = fine || coarse,
            precise = fine,
            systemLocationOn = lm != null && LocationManagerCompat.isLocationEnabled(lm),
            // Slice 1 has no LocationManager fallback yet, so a missing Play services blocks (R26).
            fixSourceAvailable = gms,
        )
    }

    private fun granted(p: String) = ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED
}

/** Debug-only synthetic movement (see [DevMovementSimulator]). */
@Singleton
class DevSimulator @Inject constructor() : DevMovementSimulator {
    private data class Route(val from: LatLng, val to: LatLng, val speedMps: Double, val startElapsedMs: Long)

    private val route = MutableStateFlow<Route?>(null)
    private val _active = MutableStateFlow(false)
    override val active: StateFlow<Boolean> = _active
    override val available: Boolean get() = BuildConfig.DEBUG

    override fun start(from: LatLng, to: LatLng, speedMps: Double) {
        if (!available) return
        route.value = Route(from, to, speedMps, SystemClock.elapsedRealtime())
        _active.value = true
    }

    override fun stop() {
        route.value = null
        _active.value = false
    }

    /** Emits a fix every second along the straight line, then holds at the destination. */
    @OptIn(ExperimentalCoroutinesApi::class)
    internal fun fixes(): Flow<LocationFixDto> = route.flatMapLatest { r ->
        flow {
            if (r == null) return@flow
            val total = io.github.vedantjoshi23.kairos.core.kernel.Geo.distanceMeters(r.from, r.to)
            while (true) {
                val now = SystemClock.elapsedRealtime()
                val travelled = r.speedMps * (now - r.startElapsedMs) / 1000.0
                val f = if (total <= 0.0) 1.0 else (travelled / total).coerceIn(0.0, 1.0)
                val p = LatLng(r.from.lat + (r.to.lat - r.from.lat) * f, r.from.lon + (r.to.lon - r.from.lon) * f)
                emit(LocationFixDto(p, accuracyM = 5.0, elapsedMs = now, fromNetwork = false))
                delay(1_000)
            }
        }
    }
}

@Singleton
class FusedLocationSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val access: AndroidLocationAccess,
    private val consent: ConsentApi,
    private val simulator: DevSimulator,
) : LocationSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fixes(tripActive: Boolean): Flow<LocationFixDto> =
        // Debug builds only: while the simulator runs, its synthetic fixes replace real ones.
        // It reads no real location, so no permission is involved.
        simulator.active.flatMapLatest { simulating ->
            if (BuildConfig.DEBUG && simulating) simulator.fixes() else realFixes(tripActive)
        }

    @SuppressLint("MissingPermission") // checked through CollectionPolicy before requesting
    private fun realFixes(tripActive: Boolean): Flow<LocationFixDto> = callbackFlow {
        access.disclosureRecorded = consent.isInForce(ConsentKind.LOCATION_DISCLOSURE)
        val a = access.current()
        // A collector is either the trip service (tripActive) or the in-use UI (Law 3, R5).
        if (!CollectionPolicy.mayCollect(a.disclosureRecorded, a.permissionGranted, appInUse = !tripActive, tripActive = tripActive)) {
            close()
            return@callbackFlow
        }
        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, if (tripActive) 15_000L else 5_000L)
            .setMinUpdateIntervalMillis(5_000L)
            .build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { loc ->
                    trySend(
                        LocationFixDto(
                            position = LatLng(loc.latitude, loc.longitude),
                            accuracyM = loc.accuracy.toDouble(),
                            elapsedMs = loc.elapsedRealtimeNanos / 1_000_000,
                            fromNetwork = loc.provider == LocationManager.NETWORK_PROVIDER,
                        ),
                    )
                }
            }
        }
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {
    @Binds abstract fun bindSource(impl: FusedLocationSource): LocationSource
    @Binds abstract fun bindAccess(impl: AndroidLocationAccess): LocationAccessProvider
    @Binds abstract fun bindConsent(impl: ConsentStore): ConsentApi
    @Binds abstract fun bindSimulator(impl: DevSimulator): DevMovementSimulator
}
