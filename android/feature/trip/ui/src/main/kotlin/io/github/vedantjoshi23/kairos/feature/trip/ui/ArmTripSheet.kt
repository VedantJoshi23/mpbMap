package io.github.vedantjoshi23.kairos.feature.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.vedantjoshi23.kairos.feature.trip.domain.ArrivalPolicy
import io.github.vedantjoshi23.kairos.feature.trip.domain.Preflight
import io.github.vedantjoshi23.kairos.feature.trip.domain.PreflightFinding
import io.github.vedantjoshi23.kairos.feature.trip.domain.PreflightInput
import io.github.vedantjoshi23.kairos.feature.trip.domain.PreflightItem
import io.github.vedantjoshi23.kairos.feature.trip.domain.RadiusPolicy
import io.github.vedantjoshi23.kairos.feature.trip.domain.Severity
import io.github.vedantjoshi23.kairos.feature.trip.domain.TravelMode

/** What the sheet needs from outside BC1, supplied by app/ (composition root). */
data class ArmContext(val tripDistanceM: Double?, val preflight: (smallestRadiusM: Double) -> PreflightInput)

/**
 * J3 steps 1-6: the radius is required (0.2-50 km) with a trip-scaled suggestion; the
 * travel mode is chosen (R22); pre-flight runs on every start (R26); arming inside a
 * radius asks first (R4).
 */
@Composable
fun ArmTripSheet(
    destinationLabel: String,
    context: ArmContext,
    onStart: (mode: TravelMode, radiiM: List<Double>) -> Unit,
    onFix: (PreflightItem) -> Unit,
    onCancel: () -> Unit,
) {
    var mode by remember { mutableStateOf(TravelMode.RAIL) }
    val suggestion = context.tripDistanceM?.let { RadiusPolicy.suggest(it, mode) } ?: 10_000.0
    val radiiKm = remember { mutableStateListOf(formatKm(suggestion)) }
    var findings by remember { mutableStateOf<List<PreflightFinding>>(emptyList()) }
    var insideAsk by remember { mutableStateOf<List<Double>?>(null) }

    val parsed = radiiKm.map { it.toDoubleOrNull()?.times(1000) }
    val valid = parsed.isNotEmpty() && parsed.all { it != null && RadiusPolicy.isValid(it) }

    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Wake me near $destinationLabel", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TravelMode.entries.forEach { m ->
                FilterChip(selected = m == mode, onClick = { mode = m }, label = { Text(m.name.lowercase().replaceFirstChar { it.uppercase() }) })
            }
        }
        radiiKm.forEachIndexed { i, value ->
            OutlinedTextField(
                value = value,
                onValueChange = { radiiKm[i] = it },
                label = { Text("Alarm ${i + 1} radius (km, 0.2 to 50)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = parsed.getOrNull(i)?.let { !RadiusPolicy.isValid(it) } ?: true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        TextButton(onClick = { radiiKm.add(formatKm(2_000.0)) }) { Text("Add another alarm") }
        findings.forEach { f ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text((if (f.severity == Severity.BLOCK) "Needed: " else "Warning: ") + PreflightText.of(f.item), modifier = Modifier.weight(1f))
                TextButton(onClick = { onFix(f.item) }) { Text("Fix") }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(enabled = valid, onClick = {
                val radii = parsed.filterNotNull()
                val result = Preflight.check(context.preflight(radii.min()))
                findings = result
                if (!Preflight.canStart(result)) return@Button
                val inside = context.tripDistanceM?.let { ArrivalPolicy.radiiAlreadyInside(it, radii) }.orEmpty()
                if (inside.isNotEmpty()) insideAsk = radii else onStart(mode, radii)
            }) { Text("Start trip") }
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    }

    insideAsk?.let { radii ->
        AlertDialog(
            onDismissRequest = { insideAsk = null },
            title = { Text("You're already inside a radius") },
            text = { Text("Shrink the radius, or arm anyway: it will then ring at about ${formatKm(ArrivalPolicyHelper.arrival(mode, radii))} km from the destination.") },
            confirmButton = { TextButton(onClick = { insideAsk = null; onStart(mode, radii) }) { Text("Arm anyway") } },
            dismissButton = { TextButton(onClick = { insideAsk = null }) { Text("Shrink radius") } },
        )
    }
}

private object ArrivalPolicyHelper {
    fun arrival(mode: TravelMode, radii: List<Double>): Double = minOf(mode.arrivalDefaultM, radii.min())
}

object PreflightText {
    fun of(item: PreflightItem): String = when (item) {
        PreflightItem.DISCLOSURE -> "review how Kairos uses location"
        PreflightItem.LOCATION_PERMISSION -> "allow location access"
        PreflightItem.SYSTEM_LOCATION -> "turn on location"
        PreflightItem.FIX_SOURCE -> "Google Play services is required for location"
        PreflightItem.PRECISION -> "allow precise location for small radii"
        PreflightItem.NOTIFICATIONS -> "allow notifications so the alarm can ring"
        PreflightItem.ALARM_VOLUME -> "alarm volume is 0; vibration and the screen will still wake you"
        PreflightItem.FULL_SCREEN_INTENT -> "full-screen alarms are off; a loud heads-up alarm will be used"
    }
}

private fun formatKm(m: Double): String = "%.1f".format(m / 1000)
