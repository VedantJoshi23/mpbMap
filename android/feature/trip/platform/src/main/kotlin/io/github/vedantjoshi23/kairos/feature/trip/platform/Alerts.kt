package io.github.vedantjoshi23.kairos.feature.trip.platform

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.vedantjoshi23.kairos.feature.trip.domain.Alert
import io.github.vedantjoshi23.kairos.feature.trip.domain.AlertKind
import io.github.vedantjoshi23.kairos.feature.trip.domain.AlertPort

object Channels {
    const val ALARM = "kairos.alarm"
    const val INTERRUPTED = "kairos.interrupted"
    const val PROMPT = "kairos.prompt"
    const val TRIP = "kairos.trip"

    private val alarmAudio: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    /** Safe to call in direct boot: notification channels need no CE storage. */
    fun ensure(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        nm.createNotificationChannel(
            NotificationChannel(ALARM, "Arrival alarms", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(null, null) // the service plays a looping sound itself
                enableVibration(false)
                setBypassDnd(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
        )
        nm.createNotificationChannel(
            NotificationChannel(INTERRUPTED, "Interrupted trip alarms", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(sound, alarmAudio)
                enableVibration(true)
                vibrationPattern = Vibration.PATTERN
                setBypassDnd(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
        )
        nm.createNotificationChannel(NotificationChannel(PROMPT, "Trip prompts", NotificationManager.IMPORTANCE_HIGH))
        nm.createNotificationChannel(NotificationChannel(TRIP, "Active trip", NotificationManager.IMPORTANCE_LOW))
    }
}

object Vibration {
    /** Repeating pattern: the alarm is perceivable without sound (Law 14). */
    val PATTERN = longArrayOf(0, 800, 400, 800, 400, 1600, 800)

    fun start(context: Context) {
        vibrator(context).vibrate(VibrationEffect.createWaveform(PATTERN, 1))
    }

    fun stop(context: Context) = vibrator(context).cancel()

    private fun vibrator(context: Context): Vibrator =
        if (Build.VERSION.SDK_INT >= 31) {
            context.getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
}

/** Looping sound on the alarm stream, until dismissed (ADR-0005). */
class AlarmSound(private val context: Context) {
    private var player: MediaPlayer? = null

    fun start() {
        if (player != null) return
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        player = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            setDataSource(context, uri)
            isLooping = true
            prepare()
            start()
        }
        Vibration.start(context)
    }

    fun stop() {
        player?.run { runCatching { stop() }; release() }
        player = null
        Vibration.stop(context)
    }
}

/**
 * The phone's AlertPort adapter while the trip service runs: looping alarm-stream audio,
 * repeating vibration, and a full-screen or heads-up visual (ADR-0005; Law 14).
 */
class PhoneAlertAdapter(private val context: Context, private val sound: AlarmSound) : AlertPort {
    private val ringing = mutableSetOf<Alert>()

    override fun raise(alert: Alert) {
        when (alert.kind) {
            AlertKind.ARRIVAL, AlertKind.LOCATION_LOST, AlertKind.TRIP_INTERRUPTED -> {
                ringing += alert
                sound.start()
                post(alert, Channels.ALARM, fullScreen = true)
            }
            AlertKind.STALE_TRIP_PROMPT -> post(alert, Channels.PROMPT, fullScreen = false)
        }
    }

    override fun clear(alert: Alert) {
        ringing -= alert
        NotificationManagerCompat.from(context).cancel(notificationId(alert))
        if (ringing.isEmpty()) sound.stop()
    }

    fun clearAll() {
        ringing.toList().forEach { clear(it) }
        sound.stop()
    }

    private fun post(alert: Alert, channel: String, fullScreen: Boolean) {
        val open = AlarmActivity.intent(context, alert.kind, alert.alarmId?.value)
        val pi = PendingIntent.getActivity(context, notificationId(alert), open, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, channel)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(AlertText.title(alert.kind))
            .setContentText(AlertText.body(alert.kind))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(alert.kind != AlertKind.STALE_TRIP_PROMPT)
            .setContentIntent(pi)
        if (fullScreen) builder.setFullScreenIntent(pi, true)
        runCatching { NotificationManagerCompat.from(context).notify(notificationId(alert), builder.build()) }
    }

    companion object {
        fun notificationId(alert: Alert): Int = (alert.kind.name + (alert.alarmId?.value ?: "")).hashCode()
    }
}

object AlertText {
    fun title(kind: AlertKind): String = when (kind) {
        AlertKind.ARRIVAL -> "You're nearly there"
        AlertKind.LOCATION_LOST -> "Location lost near your destination"
        AlertKind.TRIP_INTERRUPTED -> "Trip alarm interrupted"
        AlertKind.STALE_TRIP_PROMPT -> "Still travelling?"
    }

    fun body(kind: AlertKind): String = when (kind) {
        AlertKind.ARRIVAL -> "Your arrival alarm is ringing. Dismiss or snooze for 2 minutes."
        AlertKind.LOCATION_LOST -> "Kairos can't see your position and you may be close. Check where you are."
        AlertKind.TRIP_INTERRUPTED -> "Your phone restarted or Kairos was stopped. Open Kairos to resume the trip."
        AlertKind.STALE_TRIP_PROMPT -> "No progress toward your destination for 3 hours. End the trip?"
    }
}

/**
 * The no-service alert (R25): a FLAG_INSISTENT notification loops the channel sound with
 * no running service, before unlock too. Uses only direct-boot-safe APIs.
 */
object InterruptedNotifier {
    const val ID = 0x4B41

    fun raise(context: Context, destinationLabel: String) {
        Channels.ensure(context)
        val open = AlarmActivity.intent(context, AlertKind.TRIP_INTERRUPTED, null)
        val pi = PendingIntent.getActivity(context, ID, open, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val n = NotificationCompat.Builder(context, Channels.INTERRUPTED)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(AlertText.title(AlertKind.TRIP_INTERRUPTED))
            .setContentText("Trip to $destinationLabel. " + AlertText.body(AlertKind.TRIP_INTERRUPTED))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pi, true)
            .setContentIntent(pi)
            .build()
        n.flags = n.flags or Notification.FLAG_INSISTENT
        runCatching { NotificationManagerCompat.from(context).notify(ID, n) }
    }

    fun clear(context: Context) = NotificationManagerCompat.from(context).cancel(ID)
}

internal fun Context.serviceIntent(action: String): Intent = Intent(this, TripService::class.java).setAction(action)
