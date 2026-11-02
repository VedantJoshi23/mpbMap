package io.github.vedantjoshi23.kairos.feature.location.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Prominent disclosure, shown before the system permission prompt (R6; STD-COMPLIANCE
 * rule 2). It names every location-using feature and says "location".
 */
@Composable
fun DisclosureScreen(onAccept: () -> Unit, onDecline: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Kairos uses your location", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
            Text(
                "Kairos collects location data to show where you are on the map (locate-me), " +
                    "and to run arrival alarms that wake you near your destination. " +
                    "During a trip you start, location is collected while the app is closed or not in use, " +
                    "until the trip ends. Kairos never collects location in the background otherwise.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "Location history stays on this phone. You can withdraw this consent at any time in Settings.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onAccept) { Text("Continue") }
                OutlinedButton(onClick = onDecline) { Text("Not now") }
            }
        }
    }
}
