package io.github.vedantjoshi23.kairos.core.kernel

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/** A WGS84 position. Shared kernel primitive (ARCH-SYSTEM §1). */
data class LatLng(val lat: Double, val lon: Double) {
    init {
        require(lat in -90.0..90.0) { "latitude out of range" }
        require(lon in -180.0..180.0) { "longitude out of range" }
    }

    // Never interpolate coordinates into messages or logs (STD-CODE rule 5).
    override fun toString(): String = "LatLng(redacted)"
}

object Geo {
    /** Mean Earth radius in metres (IUGG). */
    const val EARTH_RADIUS_M = 6_371_008.8

    /**
     * Great-circle (straight-line) distance in metres, by the haversine formula.
     * This is the single distance implementation BC1 uses for R1 (DOM-KERNEL).
     */
    fun distanceMeters(a: LatLng, b: LatLng): Double {
        val lat1 = Math.toRadians(a.lat)
        val lat2 = Math.toRadians(b.lat)
        val dLat = lat2 - lat1
        val dLon = Math.toRadians(b.lon - a.lon)
        val h = sin(dLat / 2).let { it * it } + cos(lat1) * cos(lat2) * sin(dLon / 2).let { it * it }
        return 2 * EARTH_RADIUS_M * asin(min(1.0, sqrt(h)))
    }
}
