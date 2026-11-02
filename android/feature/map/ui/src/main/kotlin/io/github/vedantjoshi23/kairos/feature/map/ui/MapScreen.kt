package io.github.vedantjoshi23.kairos.feature.map.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.vedantjoshi23.kairos.feature.map.domain.SkinMeta
import io.github.vedantjoshi23.kairos.feature.map.platform.KairosMap
import io.github.vedantjoshi23.kairos.feature.map.platform.Overlay

/** Working names until the owner chooses public skin names (ADR-0001 rule 4). */
fun skinWorkingName(id: String): String = id.split('-').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

@Composable
fun MapScreen(
    onMapError: (Throwable) -> Unit,
    styleJson: String,
    overlays: List<Overlay>,
    centerOn: Pair<Double, Double>?,
    onLongPress: (Double, Double) -> Unit,
    onLocateMe: () -> Unit,
    skins: List<SkinMeta>,
    selectedSkin: String,
    onSelectSkin: (String) -> Unit,
    flat: Boolean,
    onFlatChange: (Boolean) -> Unit,
    showGrid: Boolean,
    hint: String?,
    bottomPanel: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        KairosMap(styleJson, overlays, showGrid, centerOn, onLongPress, onMapError, Modifier.fillMaxSize())

        Surface(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            tonalElevation = 3.dp,
        ) {
            Column(Modifier.padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Kairos", style = MaterialTheme.typography.titleMedium)
                    val selected = skins.firstOrNull { it.id == selectedSkin }
                    if (selected?.is3D == true) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("2D", style = MaterialTheme.typography.labelMedium)
                            Switch(
                                checked = flat,
                                onCheckedChange = onFlatChange,
                                modifier = Modifier.semantics { contentDescription = "Use the flat 2D version of this skin" },
                            )
                        }
                    }
                }
                LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(skins, key = { it.id }) { skin ->
                        FilterChip(
                            selected = skin.id == selectedSkin,
                            onClick = { onSelectSkin(skin.id) },
                            label = { Text(skinWorkingName(skin.id) + if (skin.is3D) " · 3D" else "") },
                        )
                    }
                }
                val selected = skins.firstOrNull { it.id == selectedSkin }
                if (selected?.is3D == true && !flat) {
                    Text(
                        "Tilt with two fingers to see the 3D city.",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 14.dp),
                    )
                }
            }
        }

        Column(
            Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (hint != null) {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)) {
                    Text(hint, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }
            bottomPanel()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                ExtendedFloatingActionButton(onClick = onLocateMe) { Text("Locate me") }
            }
        }
    }
}
