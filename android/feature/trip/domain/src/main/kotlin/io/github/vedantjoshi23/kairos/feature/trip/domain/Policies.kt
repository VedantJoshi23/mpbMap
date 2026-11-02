package io.github.vedantjoshi23.kairos.feature.trip.domain

import kotlin.math.max
import kotlin.math.min

/** Radius rules (FEAT-ARRIVAL-ALARM §3; KC-079: the user sets the distance). */
object RadiusPolicy {
    const val MIN_M = 200.0
    const val MAX_M = 50_000.0

    fun isValid(radiusM: Double): Boolean = radiusM in MIN_M..MAX_M

    /**
     * An editable suggestion scaled to trip length. It never exceeds half the trip,
     * so a fresh suggestion does not land the user inside the radius (R4).
     */
    fun suggest(tripDistanceM: Double, mode: TravelMode): Double {
        val (lo, hi) = when (mode) {
            TravelMode.WALK -> 200.0 to 1_000.0
            TravelMode.CYCLE -> 500.0 to 3_000.0
            TravelMode.ROAD, TravelMode.RAIL -> 2_000.0 to 10_000.0
        }
        val scaled = (tripDistanceM * 0.1).coerceIn(lo, hi)
        return max(MIN_M, min(scaled, tripDistanceM * 0.5))
    }
}

/** R17: arrival = min(mode default, smallest active radius). */
object ArrivalPolicy {
    fun arrivalDistanceM(mode: TravelMode, alarms: List<Alarm>): Double {
        val smallest = alarms.filter { it.state.isActive }.minOfOrNull { it.radiusM }
        return if (smallest == null) mode.arrivalDefaultM else min(mode.arrivalDefaultM, smallest)
    }

    /** R4: radii the user is already inside when arming; the UI must ask before arming these. */
    fun radiiAlreadyInside(currentDistanceM: Double, radiiM: List<Double>): List<Double> =
        radiiM.filter { currentDistanceM <= it }
}

enum class PreflightItem {
    DISCLOSURE, LOCATION_PERMISSION, SYSTEM_LOCATION, FIX_SOURCE, PRECISION,
    NOTIFICATIONS, ALARM_VOLUME, FULL_SCREEN_INTENT,
}

enum class Severity { BLOCK, WARN }

data class PreflightFinding(val item: PreflightItem, val severity: Severity)

data class PreflightInput(
    val disclosureRecorded: Boolean,
    val locationPermissionGranted: Boolean,
    val systemLocationOn: Boolean,
    val fixSourceAvailable: Boolean,
    val preciseGranted: Boolean,
    val reportedAccuracyM: Double?,
    val notificationsGranted: Boolean,
    val alarmVolumeAboveZero: Boolean,
    val fullScreenIntentAvailable: Boolean,
    val smallestRadiusM: Double,
)

/**
 * R26: re-checked on every Start trip. Each failing item either blocks the start or
 * degrades with a warning. A warning is allowed only where a guaranteed alert channel
 * remains (Laws 1 and 14).
 */
object Preflight {
    /** Approximate location on Android 12+ is about 1–3 km; assume the worst when unreported. */
    const val APPROXIMATE_ACCURACY_FALLBACK_M = 3_000.0

    fun check(input: PreflightInput): List<PreflightFinding> = buildList {
        if (!input.disclosureRecorded) add(PreflightFinding(PreflightItem.DISCLOSURE, Severity.BLOCK))
        if (!input.locationPermissionGranted) add(PreflightFinding(PreflightItem.LOCATION_PERMISSION, Severity.BLOCK))
        if (!input.systemLocationOn) add(PreflightFinding(PreflightItem.SYSTEM_LOCATION, Severity.BLOCK))
        if (!input.fixSourceAvailable) add(PreflightFinding(PreflightItem.FIX_SOURCE, Severity.BLOCK))
        if (!input.preciseGranted) {
            val accuracy = input.reportedAccuracyM ?: APPROXIMATE_ACCURACY_FALLBACK_M
            val severity = if (input.smallestRadiusM < accuracy) Severity.BLOCK else Severity.WARN
            add(PreflightFinding(PreflightItem.PRECISION, severity))
        }
        if (!input.notificationsGranted) add(PreflightFinding(PreflightItem.NOTIFICATIONS, Severity.BLOCK))
        if (!input.alarmVolumeAboveZero) add(PreflightFinding(PreflightItem.ALARM_VOLUME, Severity.WARN))
        if (!input.fullScreenIntentAvailable) add(PreflightFinding(PreflightItem.FULL_SCREEN_INTENT, Severity.WARN))
    }

    fun canStart(findings: List<PreflightFinding>): Boolean = findings.none { it.severity == Severity.BLOCK }
}
