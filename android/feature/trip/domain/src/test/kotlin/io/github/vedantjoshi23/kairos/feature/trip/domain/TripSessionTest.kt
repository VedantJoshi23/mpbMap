package io.github.vedantjoshi23.kairos.feature.trip.domain

import io.github.vedantjoshi23.kairos.core.kernel.AlarmId
import io.github.vedantjoshi23.kairos.core.kernel.Geo
import io.github.vedantjoshi23.kairos.core.kernel.LatLng
import io.github.vedantjoshi23.kairos.core.kernel.TripId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TripSessionTest {
    private val dest = Destination(LatLng(19.0760, 72.8777), "Mumbai Central")
    private val trip = TripId("t1")
    private val wake = AlarmId("wake")
    private val ready = AlarmId("ready")

    /** A point due north of the destination at roughly [km] kilometres. */
    private fun northOf(km: Double) = LatLng(dest.position.lat + km / 111.195, dest.position.lon)
    private fun fix(km: Double, t: Long) = Fix(northOf(km), 10.0, t, FixSource.GPS)

    private fun startTwoAlarms(mode: TravelMode = TravelMode.RAIL) =
        TripSession.start(trip, dest, mode, listOf(wake to 10_000.0, ready to 2_000.0), nowElapsedMs = 0).first

    private fun List<TripEffect>.events() = filterIsInstance<TripEffect.Publish>().map { it.event }
    private fun List<TripEffect>.raised() = filterIsInstance<TripEffect.Raise>().map { it.alert }

    @Test
    fun startPublishesTripStarted() {
        val (_, effects) = TripSession.start(trip, dest, TravelMode.RAIL, listOf(wake to 10_000.0), 0)
        assertEquals(listOf<TripEvent>(TripEvent.TripStarted(trip)), effects.events())
    }

    @Test
    fun startRejectsInvalidInput() {
        assertFailsWith<IllegalArgumentException> { TripSession.start(trip, dest, TravelMode.RAIL, emptyList(), 0) }
        assertFailsWith<IllegalArgumentException> { TripSession.start(trip, dest, TravelMode.RAIL, listOf(wake to 50.0), 0) }
        assertFailsWith<IllegalArgumentException> { TripSession.start(trip, dest, TravelMode.RAIL, listOf(wake to 60_000.0), 0) }
        assertFailsWith<IllegalArgumentException> {
            TripSession.start(trip, dest, TravelMode.RAIL, listOf(wake to 1_000.0, wake to 2_000.0), 0)
        }
    }

    @Test
    fun r1_firesOnStraightLineDistanceOnly() {
        val s = startTwoAlarms()
        assertTrue(s.onFix(fix(12.0, 1_000)).raised().isEmpty())
        val effects = s.onFix(fix(9.5, 2_000))
        assertEquals(listOf(Alert(AlertKind.ARRIVAL, trip, wake)), effects.raised())
        assertTrue(TripEvent.AlarmFired(trip, wake) in effects.events())
    }

    @Test
    fun r1_firesWhicheverRouteIsTaken() {
        // Approach from the east instead of the north: same radius, same alarm (C-vs-D, KC-080).
        val s = startTwoAlarms()
        val east = LatLng(dest.position.lat, dest.position.lon + 9.0 / (111.195 * Math.cos(Math.toRadians(dest.position.lat))))
        assertTrue(Geo.distanceMeters(east, dest.position) < 10_000.0)
        assertEquals(1, s.onFix(Fix(east, 10.0, 1_000, FixSource.GPS)).raised().size)
    }

    @Test
    fun r2_dismissingOneAlarmKeepsTheOthersArmed() {
        val s = startTwoAlarms()
        s.onFix(fix(9.0, 1_000))
        val effects = s.dismiss(wake)
        assertFalse(s.ended)
        assertTrue(TripEvent.AlarmDismissed(trip, wake) in effects.events())
        assertFalse(effects.events().any { it is TripEvent.TripEnded })
        assertEquals(AlarmState.ARMED, s.alarms.first { it.id == ready }.state)
        assertEquals(1, s.onFix(fix(1.5, 2_000)).raised().size)
    }

    @Test
    fun r2_sessionEndsOnlyWithTheLastAlarm() {
        val s = startTwoAlarms()
        s.onFix(fix(1.0, 1_000))
        s.dismiss(wake)
        val effects = s.dismiss(ready)
        assertTrue(s.ended)
        assertTrue(TripEvent.TripEnded(trip) in effects.events())
        assertTrue(s.onFix(fix(0.5, 2_000)).isEmpty())
    }

    @Test
    fun r17_arrivalNeverEndsASessionWithActiveAlarms() {
        val s = startTwoAlarms()
        val effects = s.onFix(fix(0.2, 1_000))
        assertTrue(TripEvent.ArrivalReached(trip) in effects.events())
        assertFalse(s.ended)
        assertEquals(2, effects.raised().size)
    }

    @Test
    fun r17_arrivalDistanceIsTheSmallerOfModeDefaultAndSmallestRadius() {
        val rail = TripSession.start(trip, dest, TravelMode.RAIL, listOf(wake to 300.0), 0).first
        assertEquals(300.0, rail.arrivalDistanceM)
        val walk = TripSession.start(trip, dest, TravelMode.WALK, listOf(wake to 300.0), 0).first
        assertEquals(100.0, walk.arrivalDistanceM)
        assertEquals(500.0, startTwoAlarms().arrivalDistanceM)
    }

    @Test
    fun arrivalIsReportedOnce() {
        val s = startTwoAlarms()
        s.onFix(fix(0.3, 1_000))
        assertFalse(s.onFix(fix(0.2, 2_000)).events().any { it is TripEvent.ArrivalReached })
    }

    @Test
    fun snoozeReRingsAfterTwoMinutesRegardlessOfDistance() {
        val s = startTwoAlarms()
        s.onFix(fix(9.0, 1_000))
        val snoozed = s.snooze(wake, nowElapsedMs = 1_000)
        assertTrue(snoozed.events().any { it is TripEvent.AlarmSnoozed })
        assertEquals(AlarmState.SNOOZED, s.alarms.first { it.id == wake }.state)
        s.onFix(fix(15.0, 30_000)) // moved away; snooze still re-rings
        assertTrue(s.onTick(1_000 + TripSession.SNOOZE_MS - 1).raised().none { it.kind == AlertKind.ARRIVAL })
        val reRing = s.onTick(1_000 + TripSession.SNOOZE_MS)
        assertEquals(listOf(Alert(AlertKind.ARRIVAL, trip, wake)), reRing.raised())
        assertEquals(AlarmState.FIRED, s.alarms.first { it.id == wake }.state)
    }

    @Test
    fun snoozedAlarmStaysActiveAndInTheBootRecord() {
        val s = TripSession.start(trip, dest, TravelMode.RAIL, listOf(wake to 10_000.0), 0).first
        s.onFix(fix(9.0, 1_000))
        s.snooze(wake, 1_000)
        assertTrue(s.hasActiveAlarm)
        val record = s.bootRecord(Heartbeat(5, 5, 1))
        assertTrue(record.armed)
        assertEquals(listOf("SNOOZED"), record.alarmStates)
    }

    @Test
    fun snoozeIgnoredUnlessFired() {
        val s = startTwoAlarms()
        assertTrue(s.snooze(wake, 0).isEmpty())
        assertTrue(s.snooze(AlarmId("nope"), 0).isEmpty())
    }

    @Test
    fun r3_locationLostNearDestinationRaisesDistinctAlertNotArrival() {
        val s = startTwoAlarms(TravelMode.RAIL)
        s.onFix(fix(14.0, 0))
        // 50 m/s for 120 s = 6 km, so the estimate is 8 km, inside the 10 km radius.
        val effects = s.onTick(120_000)
        assertEquals(listOf(Alert(AlertKind.LOCATION_LOST, trip)), effects.raised())
        assertTrue(TripEvent.LocationLostNearDestination(trip) in effects.events())
        assertTrue(effects.raised().none { it.kind == AlertKind.ARRIVAL })
    }

    @Test
    fun r3_noAlertWhileEstimateIsStillOutside() {
        val s = startTwoAlarms(TravelMode.WALK)
        s.onFix(fix(14.0, 0))
        assertTrue(s.onTick(120_000).raised().isEmpty()) // 2.5 m/s * 120 s = 300 m
    }

    @Test
    fun r3_noAlertWithinTheGracePeriod() {
        val s = startTwoAlarms(TravelMode.RAIL)
        s.onFix(fix(10.5, 0))
        assertTrue(s.onTick(TripSession.LOCATION_LOSS_GRACE_MS - 1).raised().isEmpty())
    }

    @Test
    fun r3_alertsOncePerEpisodeAndAgainAfterARealFix() {
        val s = startTwoAlarms(TravelMode.RAIL)
        s.onFix(fix(14.0, 0))
        assertEquals(1, s.onTick(120_000).raised().size)
        assertTrue(s.onTick(130_000).raised().isEmpty())
        s.onFix(fix(13.0, 140_000))
        assertEquals(1, s.onTick(140_000 + 200_000).raised().count { it.kind == AlertKind.LOCATION_LOST })
    }

    @Test
    fun r3_noFixSinceStartIsTreatedAsUncertainAndAlerts() {
        val s = startTwoAlarms()
        assertEquals(listOf(Alert(AlertKind.LOCATION_LOST, trip)), s.onTick(TripSession.LOCATION_LOSS_GRACE_MS).raised())
    }

    @Test
    fun r24_staleTripPromptsOnceAndNeverEndsTheTrip() {
        val s = startTwoAlarms(TravelMode.WALK)
        s.onFix(fix(30.0, 0))
        var t = 0L
        while (t < TripSession.STALE_AFTER_MS) { t += 60_000; s.onFix(fix(30.0, t)) } // not moving closer
        val effects = s.onTick(t)
        assertTrue(Alert(AlertKind.STALE_TRIP_PROMPT, trip) in effects.raised())
        assertFalse(s.ended)
        assertTrue(s.onTick(t + 1).raised().none { it.kind == AlertKind.STALE_TRIP_PROMPT })
    }

    @Test
    fun r24_progressResetsStaleness() {
        val s = startTwoAlarms(TravelMode.WALK)
        s.onFix(fix(30.0, 0))
        s.onFix(fix(29.0, TripSession.STALE_AFTER_MS - 1))
        assertTrue(s.onTick(TripSession.STALE_AFTER_MS).raised().none { it.kind == AlertKind.STALE_TRIP_PROMPT })
    }

    @Test
    fun endTripClearsRingingAlertsAndPublishesEnded() {
        val s = startTwoAlarms()
        s.onFix(fix(9.0, 1_000))
        val effects = s.end()
        assertTrue(TripEffect.Clear(Alert(AlertKind.ARRIVAL, trip, wake)) in effects)
        assertTrue(TripEvent.TripEnded(trip) in effects.events())
        assertTrue(s.end().isEmpty())
        assertFalse(s.bootRecord(null).armed)
    }

    @Test
    fun addAlarmToARunningTrip() {
        val s = startTwoAlarms()
        s.addAlarm(AlarmId("extra"), 5_000.0)
        assertEquals(3, s.alarms.size)
        assertFailsWith<IllegalArgumentException> { s.addAlarm(AlarmId("bad"), 10.0) }
    }

    @Test
    fun acknowledgeClearsNonArrivalAlerts() {
        val s = startTwoAlarms()
        assertEquals(listOf<TripEffect>(TripEffect.Clear(Alert(AlertKind.LOCATION_LOST, trip))), s.acknowledge(AlertKind.LOCATION_LOST))
        assertTrue(s.acknowledge(AlertKind.ARRIVAL).isEmpty())
    }

    @Test
    fun restorePublishesResumedAndReRingsFiredOrSnoozedAlarms() {
        val alarms = listOf(
            Alarm(wake, 10_000.0, AlarmState.SNOOZED, snoozeUntilElapsedMs = 999),
            Alarm(ready, 2_000.0, AlarmState.ARMED),
        )
        val (s, effects) = TripSession.restore(trip, dest, TravelMode.RAIL, alarms, 0)
        assertEquals(TripEvent.TripResumed(trip), effects.events().first())
        assertEquals(listOf(Alert(AlertKind.ARRIVAL, trip, wake)), effects.raised())
        assertEquals(AlarmState.FIRED, s.alarms.first { it.id == wake }.state)
        assertFailsWith<IllegalArgumentException> {
            TripSession.restore(trip, dest, TravelMode.RAIL, listOf(Alarm(wake, 1_000.0, AlarmState.DISMISSED)), 0)
        }
    }
}
