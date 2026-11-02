package io.github.vedantjoshi23.kairos.feature.trip.platform

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.vedantjoshi23.kairos.core.designsystem.KairosTheme
import io.github.vedantjoshi23.kairos.feature.trip.domain.AlertKind

/**
 * The full-screen alarm, shown over the lock screen (Law 14: large targets, TalkBack
 * labels). Direct-boot aware: before unlock it only shows the interrupted alert, and
 * acting on it opens the app after unlock.
 */
class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val kind = AlertKind.valueOf(intent.getStringExtra(EXTRA_KIND) ?: AlertKind.ARRIVAL.name)
        val alarmId = intent.getStringExtra(EXTRA_ALARM)
        setContent {
            KairosTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(AlertText.title(kind), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
                        Text(AlertText.body(kind), style = MaterialTheme.typography.bodyLarge)
                        when (kind) {
                            AlertKind.ARRIVAL -> {
                                Button(onClick = { send(TripService.ACTION_DISMISS, alarmId) }, modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)) { Text("Dismiss alarm") }
                                OutlinedButton(onClick = { send(TripService.ACTION_SNOOZE, alarmId) }, modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)) { Text("Snooze 2 minutes") }
                            }
                            AlertKind.TRIP_INTERRUPTED -> {
                                Button(onClick = { InterruptedNotifier.clear(this@AlarmActivity); openApp() }, modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)) { Text("Open Kairos to resume") }
                            }
                            else -> {
                                Button(onClick = { ack(kind) }, modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)) { Text("I'm awake") }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun send(action: String, alarmId: String?) {
        startService(serviceIntent(action).putExtra(TripService.EXTRA_ALARM, alarmId))
        finish()
    }

    private fun ack(kind: AlertKind) {
        startService(serviceIntent(TripService.ACTION_ACK).putExtra(TripService.EXTRA_KIND, kind.name))
        finish()
    }

    private fun openApp() {
        packageManager.getLaunchIntentForPackage(packageName)?.let { startActivity(it.putExtra(EXTRA_RESUME, true)) }
        finish()
    }

    companion object {
        const val EXTRA_KIND = "kind"
        const val EXTRA_ALARM = "alarm"
        const val EXTRA_RESUME = "kairos.resume"

        fun intent(context: Context, kind: AlertKind, alarmId: String?): Intent =
            Intent(context, AlarmActivity::class.java)
                .putExtra(EXTRA_KIND, kind.name)
                .putExtra(EXTRA_ALARM, alarmId)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
    }
}
