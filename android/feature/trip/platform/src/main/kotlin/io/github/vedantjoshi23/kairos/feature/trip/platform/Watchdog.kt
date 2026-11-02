package io.github.vedantjoshi23.kairos.feature.trip.platform

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.provider.Settings
import io.github.vedantjoshi23.kairos.feature.trip.data.BootRecordStore
import io.github.vedantjoshi23.kairos.feature.trip.domain.BootVerdict
import io.github.vedantjoshi23.kairos.feature.trip.domain.HeartbeatPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Thresholds are provisional until spike S3 measures them (ADR-0005). */
object SafetyTiming {
    const val HEARTBEAT_INTERVAL_MS = 60_000L
    const val STALE_THRESHOLD_MS = 5 * 60_000L // at least 2x the heartbeat interval
    const val FUTURE_TOLERANCE_MS = 2 * 60_000L
    const val WATCHDOG_PERIOD_MS = 9 * 60_000L // allow-while-idle alarms fire at most about every 9 min in Doze
}

object BootCount {
    fun read(context: Context): Int? =
        runCatching { Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT) }.getOrNull()
}

/** Self-rescheduling inexact watchdog (R25): no exact-alarm permission. */
object Watchdog {
    private const val REQUEST = 0x5744

    fun schedule(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        am.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + SafetyTiming.WATCHDOG_PERIOD_MS,
            pending(context),
        )
    }

    fun cancel(context: Context) = context.getSystemService(AlarmManager::class.java).cancel(pending(context))

    private fun pending(context: Context): PendingIntent = PendingIntent.getBroadcast(
        context, REQUEST, Intent(context, WatchdogReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
}

private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

/** Reads only the DP TripBootRecord, never CE storage, the bus or the network (direct-boot constraint). */
private fun evaluateAndAlert(context: Context, afterBoot: Boolean, done: () -> Unit) {
    val appContext = context.applicationContext ?: context
    receiverScope.launch {
        try {
            val record = BootRecordStore.read(appContext)
            val verdict = if (afterBoot && record?.armed == true) {
                BootVerdict.DIFFERENT_BOOT
            } else {
                HeartbeatPolicy.evaluate(
                    record, System.currentTimeMillis(), SystemClock.elapsedRealtime(), BootCount.read(appContext),
                    SafetyTiming.STALE_THRESHOLD_MS, SafetyTiming.FUTURE_TOLERANCE_MS,
                )
            }
            if (HeartbeatPolicy.shouldRaiseInterrupted(verdict) && record != null) {
                InterruptedNotifier.raise(appContext, record.destinationLabel)
            }
            if (verdict != BootVerdict.NOT_ARMED) Watchdog.schedule(appContext)
        } finally {
            done()
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        evaluateAndAlert(context, afterBoot = true) { pending.finish() }
    }
}

class WatchdogReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        evaluateAndAlert(context, afterBoot = false) { pending.finish() }
    }
}
