package io.github.vedantjoshi23.kairos.feature.trip.domain

import io.github.vedantjoshi23.kairos.core.kernel.AlarmId
import io.github.vedantjoshi23.kairos.core.kernel.LatLng
import io.github.vedantjoshi23.kairos.core.kernel.TripId

/** R22: chosen when arming; drives the R3 speed cap and the R17 arrival distance. */
enum class TravelMode(val arrivalDefaultM: Double, val maxSpeedMps: Double) {
    WALK(arrivalDefaultM = 100.0, maxSpeedMps = 2.5),
    CYCLE(arrivalDefaultM = 100.0, maxSpeedMps = 9.0),
    ROAD(arrivalDefaultM = 500.0, maxSpeedMps = 36.0),
    RAIL(arrivalDefaultM = 500.0, maxSpeedMps = 50.0),
}

data class Destination(val position: LatLng, val label: String)

/** A snoozed or fired-but-undismissed alarm is still active (DOM-TRIP Invariant 12). */
enum class AlarmState { ARMED, SNOOZED, FIRED, DISMISSED;

    val isActive: Boolean get() = this != DISMISSED
}

data class Alarm(
    val id: AlarmId,
    val radiusM: Double,
    val state: AlarmState = AlarmState.ARMED,
    val firedAtElapsedMs: Long? = null,
    val snoozeUntilElapsedMs: Long? = null,
)

enum class FixSource { GPS, NETWORK }

/** A location fix as the trip engine sees it. Never logged with coordinates. */
data class Fix(
    val position: LatLng,
    val accuracyM: Double,
    val elapsedMs: Long,
    val source: FixSource,
)

/** What rings, and why. Alerts differ by kind so the user can tell them apart (R3, R25). */
enum class AlertKind { ARRIVAL, LOCATION_LOST, TRIP_INTERRUPTED, STALE_TRIP_PROMPT }

data class Alert(val kind: AlertKind, val tripId: TripId, val alarmId: AlarmId? = null)

/**
 * BC1's alerting contract (DOM-TRIP §4). Adapters decide how an alert is rendered,
 * but every adapter must loop audio until cleared and provide a repeating non-audio
 * channel (ADR-0005, Law 14).
 */
interface AlertPort {
    fun raise(alert: Alert)
    fun clear(alert: Alert)
}

/** Domain events published by BC1 (DISC-DOMAIN-DISCOVERY event catalogue). */
sealed interface TripEvent {
    val tripId: TripId

    data class TripStarted(override val tripId: TripId) : TripEvent
    data class TripEnded(override val tripId: TripId) : TripEvent
    data class TripResumed(override val tripId: TripId) : TripEvent
    data class AlarmFired(override val tripId: TripId, val alarmId: AlarmId) : TripEvent
    data class AlarmDismissed(override val tripId: TripId, val alarmId: AlarmId) : TripEvent
    data class AlarmSnoozed(override val tripId: TripId, val alarmId: AlarmId, val untilElapsedMs: Long) : TripEvent
    data class ArrivalReached(override val tripId: TripId) : TripEvent
    data class LocationLostNearDestination(override val tripId: TripId) : TripEvent
    data class StaleTripDetected(override val tripId: TripId) : TripEvent
    data class TripInterrupted(override val tripId: TripId) : TripEvent
}

/** Side effects the engine asks its host to perform, in order. */
sealed interface TripEffect {
    data class Raise(val alert: Alert) : TripEffect
    data class Clear(val alert: Alert) : TripEffect
    data class Publish(val event: TripEvent) : TripEffect
}
