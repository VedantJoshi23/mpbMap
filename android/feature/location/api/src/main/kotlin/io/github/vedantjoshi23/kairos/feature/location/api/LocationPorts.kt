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

/**
 * Debug-build-only movement simulator (owner request, EVD-055): feeds synthetic fixes
 * moving from [from] to [to] into the real fix stream so the real trip engine can be
 * tested without travelling. Inert in release builds. It is not the J12 demo and is
 * never used as Play declaration evidence (STD-COMPLIANCE rule 1).
 */
interface DevMovementSimulator {
    val available: Boolean
    val active: kotlinx.coroutines.flow.StateFlow<Boolean>
    fun start(from: io.github.vedantjoshi23.kairos.core.kernel.LatLng, to: io.github.vedantjoshi23.kairos.core.kernel.LatLng, speedMps: Double)
    fun stop()
}
