package io.github.vedantjoshi23.kairos.feature.trip.domain

import io.github.vedantjoshi23.kairos.core.kernel.AlarmId
import io.github.vedantjoshi23.kairos.core.kernel.Geo
import io.github.vedantjoshi23.kairos.core.kernel.TripId

/**
 * One trip session holding every armed alarm (R2). Pure state machine: callers feed
 * fixes, ticks and user actions; it returns the effects to perform. Times are
 * monotonic elapsed milliseconds.
 */
class TripSession private constructor(
    val tripId: TripId,
    val destination: Destination,
    val mode: TravelMode,
    alarms: List<Alarm>,
    startedElapsedMs: Long,
) {
    var alarms: List<Alarm> = alarms
        private set
    var ended: Boolean = false
        private set
    var lastFix: Fix? = null
        private set
    var lastKnownDistanceM: Double? = null
        private set
    var lastProgressElapsedMs: Long = startedElapsedMs
        private set

    private val startedElapsedMs = startedElapsedMs
    private var lastProgressDistanceM: Double? = null
    private var arrivalReported = false
    private var locationLostAlerted = false
    private var staleAlerted = false

    val arrivalDistanceM: Double get() = ArrivalPolicy.arrivalDistanceM(mode, alarms)
    val hasActiveAlarm: Boolean get() = alarms.any { it.state.isActive }

    companion object {
        const val SNOOZE_MS = 2 * 60_000L // DOM-TRIP Invariant 12: fixed, not user-extendable in the MVP
        const val LOCATION_LOSS_GRACE_MS = 90_000L
        const val STALE_AFTER_MS = 3 * 60 * 60_000L // R24
        const val PROGRESS_EPSILON_M = 100.0

        fun start(
            tripId: TripId,
            destination: Destination,
            mode: TravelMode,
            alarms: List<Pair<AlarmId, Double>>,
            nowElapsedMs: Long,
        ): Pair<TripSession, List<TripEffect>> {
            require(alarms.isNotEmpty()) { "a trip needs at least one alarm" }
            require(alarms.all { RadiusPolicy.isValid(it.second) }) { "radius out of range" }
            require(alarms.map { it.first }.toSet().size == alarms.size) { "duplicate alarm id" }
            val session = TripSession(tripId, destination, mode, alarms.map { Alarm(it.first, it.second) }, nowElapsedMs)
            return session to listOf(TripEffect.Publish(TripEvent.TripStarted(tripId)))
        }

        /**
         * Resumes a persisted trip after an interruption (R25). Only a foreground user
         * action may call this; there is no automatic restart (R5). Snoozes are reset to
         * fired, because elapsed time from an earlier boot is meaningless.
         */
        fun restore(
            tripId: TripId,
            destination: Destination,
            mode: TravelMode,
            alarms: List<Alarm>,
            nowElapsedMs: Long,
        ): Pair<TripSession, List<TripEffect>> {
            require(alarms.any { it.state.isActive }) { "nothing to resume" }
            val normalised = alarms.map {
                if (it.state == AlarmState.SNOOZED) it.copy(state = AlarmState.FIRED, snoozeUntilElapsedMs = null) else it
            }
            val session = TripSession(tripId, destination, mode, normalised, nowElapsedMs)
            val reRing = normalised.filter { it.state == AlarmState.FIRED }
                .map { TripEffect.Raise(Alert(AlertKind.ARRIVAL, tripId, it.id)) }
            return session to (listOf<TripEffect>(TripEffect.Publish(TripEvent.TripResumed(tripId))) + reRing)
        }
    }

    /** R1: straight-line distance to the destination, never the route. */
    fun onFix(fix: Fix): List<TripEffect> {
        if (ended) return emptyList()
        val effects = mutableListOf<TripEffect>()
        val distance = Geo.distanceMeters(fix.position, destination.position)
        lastFix = fix
        lastKnownDistanceM = distance
        if (locationLostAlerted) {
            // A real fix ends the loss episode; the user still dismisses the alert itself.
            locationLostAlerted = false
        }
        val previousProgress = lastProgressDistanceM
        if (previousProgress == null || previousProgress - distance >= PROGRESS_EPSILON_M) {
            lastProgressDistanceM = distance
            lastProgressElapsedMs = fix.elapsedMs
            staleAlerted = false
        }
        alarms = alarms.map { alarm ->
            if (alarm.state == AlarmState.ARMED && distance <= alarm.radiusM) {
                effects += TripEffect.Publish(TripEvent.AlarmFired(tripId, alarm.id))
                effects += TripEffect.Raise(Alert(AlertKind.ARRIVAL, tripId, alarm.id))
                alarm.copy(state = AlarmState.FIRED, firedAtElapsedMs = fix.elapsedMs)
            } else {
                alarm
            }
        }
        if (!arrivalReported && distance <= arrivalDistanceM) {
            // Arrival never ends a session with an active alarm (R2, R17).
            arrivalReported = true
            effects += TripEffect.Publish(TripEvent.ArrivalReached(tripId))
        }
        return effects
    }

    /** Called periodically by the trip service: snooze expiry, location loss (R3), stale trip (R24). */
    fun onTick(nowElapsedMs: Long): List<TripEffect> {
        if (ended) return emptyList()
        val effects = mutableListOf<TripEffect>()

        alarms = alarms.map { alarm ->
            val until = alarm.snoozeUntilElapsedMs
            if (alarm.state == AlarmState.SNOOZED && until != null && nowElapsedMs >= until) {
                // Re-ring the same alert regardless of distance (Invariant 12).
                effects += TripEffect.Publish(TripEvent.AlarmFired(tripId, alarm.id))
                effects += TripEffect.Raise(Alert(AlertKind.ARRIVAL, tripId, alarm.id))
                alarm.copy(state = AlarmState.FIRED, snoozeUntilElapsedMs = null)
            } else {
                alarm
            }
        }

        if (!locationLostAlerted && alarms.any { it.state == AlarmState.ARMED }) {
            val fix = lastFix
            val sinceFix = nowElapsedMs - (fix?.elapsedMs ?: startedElapsedMs)
            if (sinceFix >= LOCATION_LOSS_GRACE_MS && locationLostNearDestination(nowElapsedMs)) {
                locationLostAlerted = true
                effects += TripEffect.Publish(TripEvent.LocationLostNearDestination(tripId))
                effects += TripEffect.Raise(Alert(AlertKind.LOCATION_LOST, tripId))
            }
        }

        if (!staleAlerted && hasActiveAlarm && nowElapsedMs - lastProgressElapsedMs >= STALE_AFTER_MS) {
            staleAlerted = true
            effects += TripEffect.Publish(TripEvent.StaleTripDetected(tripId))
            effects += TripEffect.Raise(Alert(AlertKind.STALE_TRIP_PROMPT, tripId))
        }
        return effects
    }

    /**
     * R3 dead reckoning: the last known distance minus the mode's maximum speed times the
     * time since that fix. With no fix at all since the start, the position is unknown,
     * and an uncertain alert beats silence.
     */
    private fun locationLostNearDestination(nowElapsedMs: Long): Boolean {
        val fix = lastFix ?: return true
        val last = lastKnownDistanceM ?: return true
        val outermostArmed = alarms.filter { it.state == AlarmState.ARMED }.maxOfOrNull { it.radiusM } ?: return false
        val estimate = last - mode.maxSpeedMps * (nowElapsedMs - fix.elapsedMs) / 1000.0
        return estimate <= outermostArmed
    }

    fun snooze(alarmId: AlarmId, nowElapsedMs: Long): List<TripEffect> {
        val alarm = alarms.firstOrNull { it.id == alarmId } ?: return emptyList()
        if (ended || alarm.state != AlarmState.FIRED) return emptyList()
        val until = nowElapsedMs + SNOOZE_MS
        alarms = alarms.map { if (it.id == alarmId) it.copy(state = AlarmState.SNOOZED, snoozeUntilElapsedMs = until) else it }
        return listOf(
            TripEffect.Clear(Alert(AlertKind.ARRIVAL, tripId, alarmId)),
            TripEffect.Publish(TripEvent.AlarmSnoozed(tripId, alarmId, until)),
        )
    }

    /** Dismissing one alarm disarms only that alarm; the session ends with the last one (R2). */
    fun dismiss(alarmId: AlarmId): List<TripEffect> {
        val alarm = alarms.firstOrNull { it.id == alarmId } ?: return emptyList()
        if (ended || !alarm.state.isActive) return emptyList()
        alarms = alarms.map { if (it.id == alarmId) it.copy(state = AlarmState.DISMISSED, snoozeUntilElapsedMs = null) else it }
        val effects = mutableListOf<TripEffect>(
            TripEffect.Clear(Alert(AlertKind.ARRIVAL, tripId, alarmId)),
            TripEffect.Publish(TripEvent.AlarmDismissed(tripId, alarmId)),
        )
        if (!hasActiveAlarm) effects += endEffects()
        return effects
    }

    /** Adds an alarm to a running trip (J3 step 7). The caller re-runs pre-flight. */
    fun addAlarm(alarmId: AlarmId, radiusM: Double): List<TripEffect> {
        require(RadiusPolicy.isValid(radiusM)) { "radius out of range" }
        require(alarms.none { it.id == alarmId }) { "duplicate alarm id" }
        if (ended) return emptyList()
        alarms = alarms + Alarm(alarmId, radiusM)
        return emptyList()
    }

    fun acknowledge(kind: AlertKind): List<TripEffect> =
        if (kind == AlertKind.ARRIVAL) emptyList() else listOf(TripEffect.Clear(Alert(kind, tripId)))

    fun end(): List<TripEffect> = if (ended) emptyList() else endEffects()

    private fun endEffects(): List<TripEffect> {
        ended = true
        val clears = alarms.filter { it.state == AlarmState.FIRED }
            .map { TripEffect.Clear(Alert(AlertKind.ARRIVAL, tripId, it.id)) }
        return clears + TripEffect.Publish(TripEvent.TripEnded(tripId))
    }

    /** The minimal record kept in device-protected storage for the before-unlock alert (R25). */
    fun bootRecord(heartbeat: Heartbeat?): TripBootRecord = TripBootRecord(
        tripId = tripId.value,
        armed = !ended && hasActiveAlarm,
        destinationLabel = destination.label,
        alarmStates = alarms.map { it.state.name },
        heartbeat = heartbeat,
    )
}
