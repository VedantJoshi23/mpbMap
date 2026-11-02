package io.github.vedantjoshi23.kairos.feature.location.domain

import io.github.vedantjoshi23.kairos.feature.location.api.ConsentKind
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentRecordDto

/** Law 3 and R5: fixes only after the disclosure, and only in use or during a trip. */
object CollectionPolicy {
    fun mayCollect(disclosureRecorded: Boolean, permissionGranted: Boolean, appInUse: Boolean, tripActive: Boolean): Boolean =
        disclosureRecorded && permissionGranted && (appInUse || tripActive)
}

/** R18: trip-session fixes may reveal cells only with the double opt-in. Never the journal. */
object RevealGate {
    fun tripFixMayRevealCells(revealOn: Boolean, disclosureListsExploration: Boolean, revealDuringTripsOn: Boolean): Boolean =
        revealOn && disclosureListsExploration && revealDuringTripsOn
}

/** What withdrawing each consent kind does (FEAT-LOCATION-CONSENT §3). */
enum class WithdrawalEffect { STOP_ACQUISITION, CLOSE_TRIP_REVEAL_GATE, STOP_SYNC_AND_SIGN_OUT }

object ConsentPolicy {
    fun effectOfWithdrawal(kind: ConsentKind): WithdrawalEffect = when (kind) {
        ConsentKind.LOCATION_DISCLOSURE -> WithdrawalEffect.STOP_ACQUISITION
        ConsentKind.REVEAL_DURING_TRIPS -> WithdrawalEffect.CLOSE_TRIP_REVEAL_GATE
        ConsentKind.SIGN_IN_SYNC -> WithdrawalEffect.STOP_SYNC_AND_SIGN_OUT
    }

    /** A consent is in force if its latest record is granted and not withdrawn. */
    fun isInForce(records: List<ConsentRecordDto>, kind: ConsentKind): Boolean =
        records.filter { it.kind == kind }.maxByOrNull { it.shownAtMs }?.let { it.granted && it.withdrawnAtMs == null } ?: false
}

/** STD-DATABASE rule 4: LocationFix is pruned to 30 days. */
object FixRetention {
    const val WINDOW_MS = 30L * 24 * 60 * 60 * 1000
    fun cutoff(nowWallMs: Long): Long = nowWallMs - WINDOW_MS
}
