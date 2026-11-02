package io.github.vedantjoshi23.kairos.core.kernel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class GeoTest {
    @Test
    fun zeroDistanceForSamePoint() {
        val p = LatLng(23.2156, 72.6369)
        assertEquals(0.0, Geo.distanceMeters(p, p), 1e-6)
    }

    @Test
    fun oneDegreeOfLatitudeIsAbout111Km() {
        val d = Geo.distanceMeters(LatLng(0.0, 0.0), LatLng(1.0, 0.0))
        assertEquals(111_195.0, d, 50.0)
    }

    @Test
    fun knownCityPairWithinOnePercent() {
        // Ahmedabad to Mumbai, about 441 km great-circle.
        val d = Geo.distanceMeters(LatLng(23.0225, 72.5714), LatLng(19.0760, 72.8777))
        assertEquals(439_500.0, d, 4_500.0)
    }

    @Test
    fun antipodesDoNotProduceNaN() {
        val d = Geo.distanceMeters(LatLng(0.0, 0.0), LatLng(0.0, 180.0))
        assertFalse(d.isNaN())
    }

    @Test
    fun rejectsInvalidLatitude() {
        assertFailsWith<IllegalArgumentException> { LatLng(91.0, 0.0) }
    }

    @Test
    fun toStringNeverRevealsCoordinates() {
        assertEquals("LatLng(redacted)", LatLng(12.34, 56.78).toString())
    }
}
