package io.github.vedantjoshi23.kairos.feature.map.api

/** Public contract of BC3 (DOM-MAP §4). */
data class SkinInfo(val id: String, val displayName: String, val is3D: Boolean, val fallback2DId: String?)

data class SkinChanged(val skinId: String)
