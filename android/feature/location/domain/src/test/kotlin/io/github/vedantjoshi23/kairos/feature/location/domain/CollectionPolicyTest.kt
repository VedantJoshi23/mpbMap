package io.github.vedantjoshi23.kairos.feature.location.domain

import io.github.vedantjoshi23.kairos.feature.location.api.ConsentKind
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentRecordDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CollectionPolicyTest {
    @Test fun noCollectionBeforeDisclosureOrPermission() {
        assertFalse(CollectionPolicy.mayCollect(disclosureRecorded = false, permissionGranted = true, appInUse = true, tripActive = true))
        assertFalse(CollectionPolicy.mayCollect(disclosureRecorded = true, permissionGranted = false, appInUse = true, tripActive = true))
    }

    @Test fun collectionOnlyInUseOrDuringTrip() {
        assertTrue(CollectionPolicy.mayCollect(true, true, appInUse = true, tripActive = false))
        assertTrue(CollectionPolicy.mayCollect(true, true, appInUse = false, tripActive = true))
        assertFalse(CollectionPolicy.mayCollect(true, true, appInUse = false, tripActive = false))
    }

    @Test fun revealGateNeedsAllThree() {
        assertTrue(RevealGate.tripFixMayRevealCells(true, true, true))
        assertFalse(RevealGate.tripFixMayRevealCells(true, true, false))
        assertFalse(RevealGate.tripFixMayRevealCells(true, false, true))
        assertFalse(RevealGate.tripFixMayRevealCells(false, true, true))
    }

    @Test fun withdrawalEffectsPerKind() {
        assertEquals(WithdrawalEffect.STOP_ACQUISITION, ConsentPolicy.effectOfWithdrawal(ConsentKind.LOCATION_DISCLOSURE))
        assertEquals(WithdrawalEffect.CLOSE_TRIP_REVEAL_GATE, ConsentPolicy.effectOfWithdrawal(ConsentKind.REVEAL_DURING_TRIPS))
        assertEquals(WithdrawalEffect.STOP_SYNC_AND_SIGN_OUT, ConsentPolicy.effectOfWithdrawal(ConsentKind.SIGN_IN_SYNC))
    }

    @Test fun latestConsentRecordWins() {
        val k = ConsentKind.LOCATION_DISCLOSURE
        val granted = ConsentRecordDto(k, 10, true, null, "1")
        val withdrawn = ConsentRecordDto(k, 10, true, 20, "1")
        assertTrue(ConsentPolicy.isInForce(listOf(granted), k))
        assertFalse(ConsentPolicy.isInForce(listOf(withdrawn), k))
        assertFalse(ConsentPolicy.isInForce(emptyList(), k))
        assertTrue(ConsentPolicy.isInForce(listOf(withdrawn.copy(shownAtMs = 5), granted.copy(shownAtMs = 30)), k))
    }

    @Test fun retentionCutoffIsThirtyDays() = assertEquals(0L, FixRetention.cutoff(FixRetention.WINDOW_MS))
}
