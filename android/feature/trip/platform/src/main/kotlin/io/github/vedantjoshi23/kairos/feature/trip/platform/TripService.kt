package io.github.vedantjoshi23.kairos.feature.trip.platform

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.vedantjoshi23.kairos.core.eventbus.EventBus
import io.github.vedantjoshi23.kairos.core.kernel.AlarmId
import io.github.vedantjoshi23.kairos.core.kernel.LatLng
import io.github.vedantjoshi23.kairos.core.kernel.TripId
import io.github.vedantjoshi23.kairos.feature.location.api.LocationSource
import io.github.vedantjoshi23.kairos.feature.trip.api.TripLifecycle
import io.github.vedantjoshi23.kairos.feature.trip.data.AlarmEntity
import io.github.vedantjoshi23.kairos.feature.trip.data.BootRecordStore
import io.github.vedantjoshi23.kairos.feature.trip.data.TripDatabase
import io.github.vedantjoshi23.kairos.feature.trip.data.TripEntity
import io.github.vedantjoshi23.kairos.feature.trip.domain.Alarm
import io.github.vedantjoshi23.kairos.feature.trip.domain.AlarmState
import io.github.vedantjoshi23.kairos.feature.trip.domain.AlertKind
import io.github.vedantjoshi23.kairos.feature.trip.domain.Destination
import io.github.vedantjoshi23.kairos.feature.trip.domain.Fix
import io.github.vedantjoshi23.kairos.feature.trip.domain.FixSource
import io.github.vedantjoshi23.kairos.feature.trip.domain.Heartbeat
import io.github.vedantjoshi23.kairos.feature.trip.domain.TravelMode
import io.github.vedantjoshi23.kairos.feature.trip.domain.TripEffect
import io.github.vedantjoshi23.kairos.feature.trip.domain.TripEvent
import io.github.vedantjoshi23.kairos.feature.trip.domain.TripSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * The single trip session (R2), as a user-started foreground service of type location.
 * It is started only from a foreground user action; there is no automatic restart (R5).
 */
@AndroidEntryPoint
class TripService : LifecycleService() {
    @Inject lateinit var locationSource: LocationSource
    @Inject lateinit var bus: EventBus

    private lateinit var alerts: PhoneAlertAdapter
    private var session: TripSession? = null
    private var jobs = mutableListOf<Job>()

    override fun onCreate() {
        super.onCreate()
        Channels.ensure(this)
        alerts = PhoneAlertAdapter(this, AlarmSound(this))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> start(intent)
            ACTION_RESUME -> resume()
            ACTION_DISMISS -> intent.getStringExtra(EXTRA_ALARM)?.let { applyEffects(session?.dismiss(AlarmId(it)).orEmpty()) }
            ACTION_SNOOZE -> intent.getStringExtra(EXTRA_ALARM)?.let { applyEffects(session?.snooze(AlarmId(it), SystemClock.elapsedRealtime()).orEmpty()) }
            ACTION_ACK -> intent.getStringExtra(EXTRA_KIND)?.let { applyEffects(session?.acknowledge(AlertKind.valueOf(it)).orEmpty()) }
            ACTION_END -> applyEffects(session?.end().orEmpty())
        }
        if (session == null) stopSelf()
        // Never restarted by the system: a dead session is reported loudly instead (R25).
        return START_NOT_STICKY
    }

    private fun start(intent: Intent) {
        if (session != null) return
        val dest = Destination(LatLng(intent.getDoubleExtra(EXTRA_LAT, 0.0), intent.getDoubleExtra(EXTRA_LON, 0.0)), intent.getStringExtra(EXTRA_LABEL) ?: "destination")
        val mode = TravelMode.valueOf(intent.getStringExtra(EXTRA_MODE) ?: TravelMode.RAIL.name)
        val radii = intent.getDoubleArrayExtra(EXTRA_RADII)?.toList().orEmpty()
        if (radii.isEmpty()) return
        val alarms = radii.map { AlarmId(UUID.randomUUID().toString()) to it }
        val (s, effects) = TripSession.start(TripId(UUID.randomUUID().toString()), dest, mode, alarms, SystemClock.elapsedRealtime())
        begin(s, effects)
    }

    private fun resume() {
        if (session != null) return
        lifecycleScope.launch {
            val dao = TripDatabase.get(this@TripService).dao()
            val trip = dao.activeTrip() ?: return@launch stopSelf()
            val alarms = dao.alarmsFor(trip.id).map { Alarm(AlarmId(it.id), it.radiusM, AlarmState.valueOf(it.state)) }
            if (alarms.none { it.state.isActive }) return@launch stopSelf()
            InterruptedNotifier.clear(this@TripService)
            val (s, effects) = TripSession.restore(
                TripId(trip.id), Destination(LatLng(trip.lat, trip.lon), trip.label), TravelMode.valueOf(trip.mode), alarms, SystemClock.elapsedRealtime(),
            )
            begin(s, effects)
        }
    }

    private fun begin(s: TripSession, effects: List<TripEffect>) {
        session = s
        _active.value = true
        ServiceCompat.startForeground(this, FOREGROUND_ID, ongoing(s), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        applyEffects(effects)
        jobs += lifecycleScope.launch {
            locationSource.fixes(tripActive = true).collect { dto ->
                val fix = Fix(dto.position, dto.accuracyM, dto.elapsedMs, if (dto.fromNetwork) FixSource.NETWORK else FixSource.GPS)
                applyEffects(session?.onFix(fix).orEmpty())
            }
        }
        jobs += lifecycleScope.launch {
            while (isActive) {
                applyEffects(session?.onTick(SystemClock.elapsedRealtime()).orEmpty())
                delay(15_000)
            }
        }
        jobs += lifecycleScope.launch {
            while (isActive) {
                writeHeartbeat()
                delay(SafetyTiming.HEARTBEAT_INTERVAL_MS)
            }
        }
        Watchdog.schedule(this)
    }

    private fun applyEffects(effects: List<TripEffect>) {
        val s = session ?: return
        for (effect in effects) {
            when (effect) {
                is TripEffect.Raise -> alerts.raise(effect.alert)
                is TripEffect.Clear -> alerts.clear(effect.alert)
                is TripEffect.Publish -> publish(effect.event)
            }
        }
        persist(s)
        if (s.ended) finish()
    }

    private fun publish(event: TripEvent) {
        when (event) {
            is TripEvent.TripStarted -> bus.publish(TripLifecycle.Started(event.tripId))
            is TripEvent.TripEnded -> bus.publish(TripLifecycle.Ended(event.tripId))
            is TripEvent.TripResumed -> bus.publish(TripLifecycle.Resumed(event.tripId))
            else -> bus.publish(event)
        }
    }

    private fun persist(s: TripSession) {
        lifecycleScope.launch {
            val dao = TripDatabase.get(this@TripService).dao()
            dao.upsertTrip(TripEntity(s.tripId.value, s.destination.position.lat, s.destination.position.lon, s.destination.label, s.mode.name, active = !s.ended))
            dao.upsertAlarms(s.alarms.map { AlarmEntity(it.id.value, s.tripId.value, it.radiusM, it.state.name) })
        }
        writeHeartbeat()
    }

    private fun writeHeartbeat() {
        val s = session ?: return
        val hb = Heartbeat(System.currentTimeMillis(), SystemClock.elapsedRealtime(), BootCount.read(this))
        lifecycleScope.launch { BootRecordStore.write(this@TripService, s.bootRecord(hb)) }
    }

    private fun finish() {
        Watchdog.cancel(this)
        alerts.clearAll()
        jobs.forEach { it.cancel() }
        jobs.clear()
        lifecycleScope.launch { BootRecordStore.clear(this@TripService) }
        session = null
        _active.value = false
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun ongoing(s: TripSession): Notification {
        val end = PendingIntent.getService(this, 1, serviceIntent(ACTION_END), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, Channels.TRIP)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Trip to ${s.destination.label}")
            .setContentText("Arrival alarm armed. Kairos is watching your distance.")
            .setOngoing(true)
            .addAction(0, "End trip", end)
            .build()
    }

    companion object {
        const val ACTION_START = "kairos.trip.START"
        const val ACTION_RESUME = "kairos.trip.RESUME"
        const val ACTION_DISMISS = "kairos.trip.DISMISS"
        const val ACTION_SNOOZE = "kairos.trip.SNOOZE"
        const val ACTION_ACK = "kairos.trip.ACK"
        const val ACTION_END = "kairos.trip.END"
        const val EXTRA_LAT = "lat"
        const val EXTRA_LON = "lon"
        const val EXTRA_LABEL = "label"
        const val EXTRA_MODE = "mode"
        const val EXTRA_RADII = "radii"
        const val EXTRA_ALARM = "alarm"
        const val EXTRA_KIND = "kind"
        private const val FOREGROUND_ID = 0x4B52

        private val _active = MutableStateFlow(false)
        val active: StateFlow<Boolean> = _active

        fun startIntent(context: Context, lat: Double, lon: Double, label: String, mode: String, radiiM: DoubleArray): Intent =
            context.serviceIntent(ACTION_START)
                .putExtra(EXTRA_LAT, lat).putExtra(EXTRA_LON, lon).putExtra(EXTRA_LABEL, label)
                .putExtra(EXTRA_MODE, mode).putExtra(EXTRA_RADII, radiiM)
    }
}
