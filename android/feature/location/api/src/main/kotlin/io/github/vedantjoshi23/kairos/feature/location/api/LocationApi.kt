package io.github.vedantjoshi23.kairos.feature.location.api

import io.github.vedantjoshi23.kairos.core.kernel.LatLng

/** Public contract of BC2, the only gateway to device location (Law 13). */
data class LocationFixDto(val position: LatLng, val accuracyM: Double, val elapsedMs: Long, val fromNetwork: Boolean)

data class LocationAccess(
    val disclosureRecorded: Boolean,
    val permissionGranted: Boolean,
    val precise: Boolean,
    val systemLocationOn: Boolean,
    val fixSourceAvailable: Boolean,
)

enum class ConsentKind { LOCATION_DISCLOSURE, REVEAL_DURING_TRIPS, SIGN_IN_SYNC }

data class ConsentRecordDto(val kind: ConsentKind, val shownAtMs: Long, val granted: Boolean, val withdrawnAtMs: Long?, val appVersion: String)
