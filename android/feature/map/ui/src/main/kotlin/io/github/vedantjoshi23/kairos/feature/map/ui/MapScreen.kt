package io.github.vedantjoshi23.kairos.feature.map.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vedantjoshi23.kairos.feature.map.platform.KairosMap
import io.github.vedantjoshi23.kairos.feature.map.platform.Overlay

@Composable
fun MapScreen(
    styleJson: String,
    overlays: List<Overlay>,
    centerOn: Pair<Double, Double>?,
    onLongPress: (Double, Double) -> Unit,
    onLocateMe: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        KairosMap(styleJson, overlays, centerOn, onLongPress, Modifier.fillMaxSize())
        ExtendedFloatingActionButton(
            onClick = onLocateMe,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) { Text("Locate me") }
    }
}
