package io.github.vedantjoshi23.kairos.feature.location.api

import kotlinx.coroutines.flow.Flow

/**
 * Fix stream from BC2. Fixes flow only while collected, and only when
 * [io.github.vedantjoshi23.kairos.feature.location.api.LocationAccess] allows it (Law 3).
 * [tripActive] marks collection by the trip foreground service; any other collector is
 * the in-use UI.
 */
interface LocationSource {
    fun fixes(tripActive: Boolean): Flow<LocationFixDto>
}

interface LocationAccessProvider {
    fun current(): LocationAccess
}

/** ConsentRecord is owned by BC2 for every kind (ARCH-SYSTEM v1.1.0). */
interface ConsentApi {
    suspend fun record(kind: ConsentKind, granted: Boolean)
    suspend fun withdraw(kind: ConsentKind)
    suspend fun isInForce(kind: ConsentKind): Boolean
}
