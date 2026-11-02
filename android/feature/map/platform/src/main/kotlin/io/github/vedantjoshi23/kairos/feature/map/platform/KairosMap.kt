package io.github.vedantjoshi23.kairos.feature.map.platform

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.vedantjoshi23.kairos.feature.map.domain.SkinMeta
import io.github.vedantjoshi23.kairos.feature.map.domain.SkinMetaParser
import io.github.vedantjoshi23.kairos.feature.map.domain.StyleFlattener
import io.github.vedantjoshi23.kairos.feature.map.domain.StyleGuard
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow

/**
 * A marker drawn over the map, passed in by app/. Kinds differ by shape and label, never
 * by colour alone (STD-ACCESSIBILITY rule 3).
 */
data class Overlay(val id: String, val kind: Kind, val lat: Double, val lon: Double, val label: String) {
    enum class Kind { ME, DESTINATION }
}

object SkinAssets {
    /** Every bundled skin, read from its meta.json (STD-SKINS rule 3). */
    fun catalog(context: Context): List<SkinMeta> =
        (context.assets.list("") ?: emptyArray()).mapNotNull { dir ->
            runCatching { context.assets.open("$dir/meta.json").bufferedReader().use { SkinMetaParser.parse(it.readText()) } }.getOrNull()
        }.sortedBy { it.id }

    /** Bundled style through the runtime boundary guard (Law 12); 3D skins flattened on request (C1). */
    fun load(context: Context, skin: SkinMeta, tileUrl: String, flatten: Boolean): String {
        val raw = context.assets.open("${skin.id}/style.json").bufferedReader().use { it.readText() }
        val guarded = StyleGuard.prepare(raw, tileUrl, "asset://glyphs/{fontstack}/{range}.pbf").styleJson
        return if (skin.is3D && flatten) StyleFlattener.flatten(guarded) else guarded
    }
}

@Composable
fun KairosMap(
    styleJson: String,
    overlays: List<Overlay>,
    showGrid: Boolean,
    centerOn: Pair<Double, Double>?,
    onLongPress: (lat: Double, lon: Double) -> Unit,
    onError: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // Error boundary: native map start-up failures surface as an error, not a crash.
    val created = remember {
        runCatching {
            MapLibre.getInstance(context)
            MapView(context).also { it.onCreate(Bundle()) }
        }
    }
    val mapView = created.getOrNull()
    if (mapView == null) {
        LaunchedEffect(Unit) { onError(created.exceptionOrNull() ?: IllegalStateException("map unavailable")) }
        return
    }
    val currentOverlays = rememberUpdatedState(overlays)
    val currentGrid = rememberUpdatedState(showGrid)
    val currentLongPress = rememberUpdatedState(onLongPress)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    AndroidView(factory = {
        mapView.getMapAsync { map ->
            // Attribution stays visible on every map view (R11).
            map.uiSettings.isAttributionEnabled = true
            map.uiSettings.isLogoEnabled = false
            map.cameraPosition = CameraPosition.Builder().target(LatLng(22.0, 79.0)).zoom(4.0).build()
            map.addOnMapLongClickListener { p -> currentLongPress.value(p.latitude, p.longitude); true }
            map.addOnCameraIdleListener { map.style?.let { s -> drawGrid(map, s, currentGrid.value) } }
        }
        mapView
    }, modifier = modifier)

    // Skin switch: the camera is untouched, overlays and grid are redrawn (C1, J2).
    LaunchedEffect(styleJson) {
        mapView.getMapAsync { map ->
            runCatching {
                map.setStyle(Style.Builder().fromJson(styleJson)) { style ->
                    runCatching {
                        drawOverlays(style, currentOverlays.value)
                        drawGrid(map, style, currentGrid.value)
                    }.onFailure(onError)
                }
            }.onFailure(onError)
        }
    }
    LaunchedEffect(overlays) {
        mapView.getMapAsync { map -> map.style?.let { s -> runCatching { drawOverlays(s, overlays) }.onFailure(onError) } }
    }
    LaunchedEffect(showGrid) {
        mapView.getMapAsync { map -> map.style?.let { s -> drawGrid(map, s, showGrid) } }
    }
    LaunchedEffect(centerOn) {
        val c = centerOn ?: return@LaunchedEffect
        mapView.getMapAsync { it.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(c.first, c.second), 13.0)) }
    }
}

private const val OVERLAY_SOURCE = "kairos-overlays"

private fun drawOverlays(style: Style, overlays: List<Overlay>) {
    val features = overlays.map { o ->
        Feature.fromGeometry(Point.fromLngLat(o.lon, o.lat)).also {
            it.addStringProperty("kind", o.kind.name)
            it.addStringProperty("label", o.label)
        }
    }
    val collection = FeatureCollection.fromFeatures(features)
    val existing = style.getSourceAs<GeoJsonSource>(OVERLAY_SOURCE)
    if (existing != null) {
        existing.setGeoJson(collection)
        return
    }
    style.addSource(GeoJsonSource(OVERLAY_SOURCE, collection))
    // "You": a filled dot. Destination: a hollow ring. Both are labelled.
    style.addLayer(
        CircleLayer("kairos-me", OVERLAY_SOURCE).withProperties(
            PropertyFactory.circleRadius(7f),
            PropertyFactory.circleColor("#0E6B6A"),
            PropertyFactory.circleStrokeColor("#FFFFFF"),
            PropertyFactory.circleStrokeWidth(2.5f),
        ).withFilter(org.maplibre.android.style.expressions.Expression.eq(
            org.maplibre.android.style.expressions.Expression.get("kind"), "ME")),
    )
    style.addLayer(
        CircleLayer("kairos-destination", OVERLAY_SOURCE).withProperties(
            PropertyFactory.circleRadius(12f),
            PropertyFactory.circleOpacity(0f),
            PropertyFactory.circleStrokeColor("#B4530A"),
            PropertyFactory.circleStrokeWidth(4f),
        ).withFilter(org.maplibre.android.style.expressions.Expression.eq(
            org.maplibre.android.style.expressions.Expression.get("kind"), "DESTINATION")),
    )
    style.addLayer(
        SymbolLayer("kairos-overlay-labels", OVERLAY_SOURCE).withProperties(
            PropertyFactory.textField(org.maplibre.android.style.expressions.Expression.get("label")),
            PropertyFactory.textFont(arrayOf("NotoSans-Regular")),
            PropertyFactory.textSize(13f),
            PropertyFactory.textOffset(arrayOf(0f, 1.6f)),
            PropertyFactory.textColor("#13212B"),
            PropertyFactory.textHaloColor("#FFFFFF"),
            PropertyFactory.textHaloWidth(1.8f),
            PropertyFactory.textAllowOverlap(true),
        ),
    )
}

private const val GRID_SOURCE = "kairos-grid"

/** The tactical skin's live grid: about eight lines across the view, at a round spacing. */
private fun drawGrid(map: MapLibreMap, style: Style, show: Boolean) {
    val existing = style.getSourceAs<GeoJsonSource>(GRID_SOURCE)
    if (!show) {
        existing?.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
        return
    }
    val bounds = map.projection.visibleRegion.latLngBounds
    val span = max(bounds.latitudeSpan, bounds.longitudeSpan).coerceAtLeast(1e-6)
    val step = 2.0.pow(floor(ln(span / 8) / ln(2.0)))
    val lines = mutableListOf<Feature>()
    var lon = floor(bounds.longitudeWest / step) * step
    while (lon <= bounds.longitudeEast && lines.size < 60) {
        lines += Feature.fromGeometry(LineString.fromLngLats(listOf(Point.fromLngLat(lon, bounds.latitudeSouth), Point.fromLngLat(lon, bounds.latitudeNorth))))
        lon += step
    }
    var lat = floor(bounds.latitudeSouth / step) * step
    while (lat <= bounds.latitudeNorth && lines.size < 120) {
        lines += Feature.fromGeometry(LineString.fromLngLats(listOf(Point.fromLngLat(bounds.longitudeWest, lat), Point.fromLngLat(bounds.longitudeEast, lat))))
        lat += step
    }
    val collection = FeatureCollection.fromFeatures(lines)
    if (existing != null) {
        existing.setGeoJson(collection)
        return
    }
    style.addSource(GeoJsonSource(GRID_SOURCE, collection))
    val below = if (style.getLayer("place-label") != null) "place-label" else null
    val layer = LineLayer("kairos-grid", GRID_SOURCE).withProperties(
        PropertyFactory.lineColor("#2A2A22"),
        PropertyFactory.lineOpacity(0.35f),
        PropertyFactory.lineWidth(1f),
    )
    if (below != null) style.addLayerBelow(layer, below) else style.addLayer(layer)
}

/** Rough device class for the 2D fallback until spike S1 measures real thresholds. */
object DeviceClass {
    fun prefers2D(context: Context): Boolean {
        val am = context.getSystemService(android.app.ActivityManager::class.java)
        return am?.isLowRamDevice == true || (am?.memoryClass ?: 256) < 192
    }
}
