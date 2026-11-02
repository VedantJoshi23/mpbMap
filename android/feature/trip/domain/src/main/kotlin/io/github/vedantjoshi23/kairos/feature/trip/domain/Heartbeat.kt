package io.github.vedantjoshi23.kairos.feature.trip.domain

/** Written by the trip foreground service on its own timer (DISC-DATA-MODEL v1.3.0). */
data class Heartbeat(val atWallMs: Long, val elapsedMs: Long, val bootCount: Int?)

/**
 * The only trip data in device-protected storage: no coordinates, no history.
 * Fields map to TripBootRecord in DISC-DATA-MODEL; persisted with fixed proto numbers.
 */
data class TripBootRecord(
    val tripId: String,
    val armed: Boolean,
    val destinationLabel: String,
    val alarmStates: List<String>,
    val heartbeat: Heartbeat?,
)

enum class BootVerdict {
    /** No armed trip: nothing to do. */
    NOT_ARMED,

    /** The trip service is alive. */
    FRESH,

    /** Raise R25 "Trip alarm interrupted". */
    STALE,

    /** Device restarted since the heartbeat: the boot path raises R25. */
    DIFFERENT_BOOT,
}

/** The fail-safe reading of the heartbeat (DISC-DATA-MODEL v1.2.0+): when in doubt, alert. */
object HeartbeatPolicy {
    fun evaluate(
        record: TripBootRecord?,
        nowWallMs: Long,
        nowElapsedMs: Long,
        currentBootCount: Int?,
        staleThresholdMs: Long,
        futureToleranceMs: Long,
    ): BootVerdict {
        if (record == null || !record.armed) return BootVerdict.NOT_ARMED
        val hb = record.heartbeat ?: return BootVerdict.STALE
        if (currentBootCount == null || hb.bootCount == null || hb.bootCount != currentBootCount) {
            return BootVerdict.DIFFERENT_BOOT
        }
        if (hb.atWallMs > nowWallMs + futureToleranceMs) return BootVerdict.STALE
        val age = nowElapsedMs - hb.elapsedMs
        if (age < 0 || age > staleThresholdMs) return BootVerdict.STALE
        return BootVerdict.FRESH
    }

    fun shouldRaiseInterrupted(verdict: BootVerdict): Boolean =
        verdict == BootVerdict.STALE || verdict == BootVerdict.DIFFERENT_BOOT
}
