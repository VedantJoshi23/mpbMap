package io.github.vedantjoshi23.kairos.feature.trip.api

import io.github.vedantjoshi23.kairos.core.kernel.LatLng
import io.github.vedantjoshi23.kairos.core.kernel.TripId

/** Public contract of BC1 for other contexts and app/ (DOM-TRIP §4). Depends only on core. */
enum class TripMode { WALK, CYCLE, ROAD, RAIL }

data class TripRequest(val destination: LatLng, val label: String, val mode: TripMode, val radiiM: List<Double>)

/** Lifecycle events other contexts consume (DOM-LOCATION opens and closes trip scope on these). */
sealed interface TripLifecycle {
    val tripId: TripId
    data class Started(override val tripId: TripId) : TripLifecycle
    data class Ended(override val tripId: TripId) : TripLifecycle
    data class Resumed(override val tripId: TripId) : TripLifecycle
}
