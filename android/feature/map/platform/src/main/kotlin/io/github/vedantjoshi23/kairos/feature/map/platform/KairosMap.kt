package io.github.vedantjoshi23.kairos.feature.map.platform

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.vedantjoshi23.kairos.feature.map.domain.StyleGuard
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

/** A point to draw over the map, passed in by app/ (quest marker, my location). */
data class Overlay(val id: String, val lat: Double, val lon: Double, val colorHex: String, val radiusPx: Float)

object SkinAssets {
    /** Bundled style, passed through the runtime boundary guard (Law 12, ADR-0003). */
    fun load(context: Context, skinId: String, tileUrl: String): String {
        val raw = context.assets.open("$skinId/style.json").bufferedReader().use { it.readText() }
        return StyleGuard.prepare(raw, tileUrl, "asset://glyphs/{fontstack}/{range}.pbf").styleJson
    }
}

@Composable
fun KairosMap(
    styleJson: String,
    overlays: List<Overlay>,
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
    val holder = remember { arrayOfNulls<MapLibreMap>(1) }
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
            holder[0] = map
            // Attribution stays visible on every map view (R11).
            map.uiSettings.isAttributionEnabled = true
            map.uiSettings.isLogoEnabled = false
            map.cameraPosition = CameraPosition.Builder().target(LatLng(22.0, 79.0)).zoom(4.0).build()
            runCatching { map.setStyle(Style.Builder().fromJson(styleJson)) }.onFailure(onError)
            map.addOnMapLongClickListener { p -> onLongPress(p.latitude, p.longitude); true }
        }
        mapView
    }, modifier = modifier)

    LaunchedEffect(overlays) {
        mapView.getMapAsync { map ->
            map.getStyle { style -> runCatching { overlays.forEach { draw(style, it) } }.onFailure(onError) }
        }
    }
    LaunchedEffect(centerOn) {
        val c = centerOn ?: return@LaunchedEffect
        mapView.getMapAsync { it.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(c.first, c.second), 13.0)) }
    }
}

private fun draw(style: Style, o: Overlay) {
    val feature = Feature.fromGeometry(Point.fromLngLat(o.lon, o.lat))
    val sourceId = "overlay-${o.id}"
    val existing = style.getSourceAs<GeoJsonSource>(sourceId)
    if (existing != null) {
        existing.setGeoJson(FeatureCollection.fromFeature(feature))
        return
    }
    style.addSource(GeoJsonSource(sourceId, FeatureCollection.fromFeature(feature)))
    style.addLayer(
        CircleLayer("layer-${o.id}", sourceId).withProperties(
            PropertyFactory.circleColor(o.colorHex),
            PropertyFactory.circleRadius(o.radiusPx),
            PropertyFactory.circleStrokeColor("#FFFFFF"),
            PropertyFactory.circleStrokeWidth(2f),
        ),
    )
}
