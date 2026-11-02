package io.github.vedantjoshi23.kairos.feature.trip.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HeartbeatPolicyTest {
    private val threshold = 10 * 60_000L
    private val tolerance = 60_000L
    private fun rec(hb: Heartbeat?, armed: Boolean = true) = TripBootRecord("t1", armed, "Stop", listOf("ARMED"), hb)
    private fun eval(r: TripBootRecord?, wall: Long = 1_000_000, elapsed: Long = 500_000, boot: Int? = 7) =
        HeartbeatPolicy.evaluate(r, wall, elapsed, boot, threshold, tolerance)

    @Test fun notArmedOrMissingRecordDoesNothing() {
        assertEquals(BootVerdict.NOT_ARMED, eval(null))
        assertEquals(BootVerdict.NOT_ARMED, eval(rec(Heartbeat(1, 1, 7), armed = false)))
    }

    @Test fun freshHeartbeat() = assertEquals(BootVerdict.FRESH, eval(rec(Heartbeat(999_000, 499_000, 7))))

    @Test fun missingHeartbeatIsStale() = assertEquals(BootVerdict.STALE, eval(rec(null)))

    @Test fun oldHeartbeatIsStale() =
        assertEquals(BootVerdict.STALE, eval(rec(Heartbeat(1, 500_000 - threshold - 1, 7))))

    @Test fun negativeAgeIsStale() = assertEquals(BootVerdict.STALE, eval(rec(Heartbeat(1, 600_000, 7))))

    @Test fun futureWallClockIsStale() =
        assertEquals(BootVerdict.STALE, eval(rec(Heartbeat(1_000_000 + tolerance + 1, 499_000, 7))))

    @Test fun differentOrUnreadableBootCountRoutesToBootPath() {
        assertEquals(BootVerdict.DIFFERENT_BOOT, eval(rec(Heartbeat(999_000, 499_000, 6))))
        assertEquals(BootVerdict.DIFFERENT_BOOT, eval(rec(Heartbeat(999_000, 499_000, null))))
        assertEquals(BootVerdict.DIFFERENT_BOOT, eval(rec(Heartbeat(999_000, 499_000, 7)), boot = null))
    }

    @Test fun staleAndDifferentBootBothRaiseInterrupted() {
        assertTrue(HeartbeatPolicy.shouldRaiseInterrupted(BootVerdict.STALE))
        assertTrue(HeartbeatPolicy.shouldRaiseInterrupted(BootVerdict.DIFFERENT_BOOT))
        assertFalse(HeartbeatPolicy.shouldRaiseInterrupted(BootVerdict.FRESH))
        assertFalse(HeartbeatPolicy.shouldRaiseInterrupted(BootVerdict.NOT_ARMED))
    }
}
