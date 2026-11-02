package io.github.vedantjoshi23.kairos.feature.trip.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PoliciesTest {
    private val allGood = PreflightInput(
        disclosureRecorded = true, locationPermissionGranted = true, systemLocationOn = true,
        fixSourceAvailable = true, preciseGranted = true, reportedAccuracyM = 8.0,
        notificationsGranted = true, alarmVolumeAboveZero = true, fullScreenIntentAvailable = true,
        smallestRadiusM = 2_000.0,
    )

    @Test
    fun radiusRangeIncludesTheOwnersTenKmExample() {
        assertTrue(RadiusPolicy.isValid(10_000.0))
        assertTrue(RadiusPolicy.isValid(RadiusPolicy.MIN_M))
        assertTrue(RadiusPolicy.isValid(RadiusPolicy.MAX_M))
        assertFalse(RadiusPolicy.isValid(199.0))
        assertFalse(RadiusPolicy.isValid(50_001.0))
    }

    @Test
    fun suggestionScalesWithModeAndStaysOutsideTheStart() {
        assertEquals(10_000.0, RadiusPolicy.suggest(400_000.0, TravelMode.RAIL))
        assertEquals(5_000.0, RadiusPolicy.suggest(50_000.0, TravelMode.ROAD))
        assertEquals(1_000.0, RadiusPolicy.suggest(20_000.0, TravelMode.WALK))
        // Short trip: capped at half the distance so the start is outside the radius (R4).
        assertEquals(1_500.0, RadiusPolicy.suggest(3_000.0, TravelMode.ROAD))
        // Very short trip: never below the minimum radius.
        assertEquals(RadiusPolicy.MIN_M, RadiusPolicy.suggest(100.0, TravelMode.WALK))
    }

    @Test
    fun r4_detectsRadiiTheUserIsAlreadyInside() {
        assertEquals(listOf(10_000.0), ArrivalPolicy.radiiAlreadyInside(8_000.0, listOf(10_000.0, 2_000.0)))
        assertTrue(ArrivalPolicy.radiiAlreadyInside(20_000.0, listOf(10_000.0)).isEmpty())
    }

    @Test
    fun r26_allGoodHasNoFindings() {
        assertTrue(Preflight.check(allGood).isEmpty())
        assertTrue(Preflight.canStart(Preflight.check(allGood)))
    }

    @Test
    fun r26_blockingItems() {
        val blocking = listOf(
            allGood.copy(disclosureRecorded = false) to PreflightItem.DISCLOSURE,
            allGood.copy(locationPermissionGranted = false) to PreflightItem.LOCATION_PERMISSION,
            allGood.copy(systemLocationOn = false) to PreflightItem.SYSTEM_LOCATION,
            allGood.copy(fixSourceAvailable = false) to PreflightItem.FIX_SOURCE,
            allGood.copy(notificationsGranted = false) to PreflightItem.NOTIFICATIONS,
        )
        for ((input, item) in blocking) {
            val findings = Preflight.check(input)
            assertEquals(listOf(PreflightFinding(item, Severity.BLOCK)), findings)
            assertFalse(Preflight.canStart(findings))
        }
    }

    @Test
    fun r26_degradeWithWarningOnlyWhereAChannelRemains() {
        val volume = Preflight.check(allGood.copy(alarmVolumeAboveZero = false))
        assertEquals(listOf(PreflightFinding(PreflightItem.ALARM_VOLUME, Severity.WARN)), volume)
        assertTrue(Preflight.canStart(volume))
        val fsi = Preflight.check(allGood.copy(fullScreenIntentAvailable = false))
        assertEquals(listOf(PreflightFinding(PreflightItem.FULL_SCREEN_INTENT, Severity.WARN)), fsi)
        assertTrue(Preflight.canStart(fsi))
    }

    @Test
    fun r26_approximateLocationBlocksRadiiBelowAccuracy() {
        val small = Preflight.check(allGood.copy(preciseGranted = false, reportedAccuracyM = 2_500.0, smallestRadiusM = 2_000.0))
        assertEquals(listOf(PreflightFinding(PreflightItem.PRECISION, Severity.BLOCK)), small)
        val big = Preflight.check(allGood.copy(preciseGranted = false, reportedAccuracyM = 2_500.0, smallestRadiusM = 10_000.0))
        assertEquals(listOf(PreflightFinding(PreflightItem.PRECISION, Severity.WARN)), big)
        val unknown = Preflight.check(allGood.copy(preciseGranted = false, reportedAccuracyM = null, smallestRadiusM = 2_000.0))
        assertEquals(Severity.BLOCK, unknown.single().severity)
    }
}
