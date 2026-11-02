package io.github.vedantjoshi23.kairos.core.config

import java.net.URI

/** The bootstrap file's contents: source endpoints only, never styles (ADR-0003, ADR-0009). */
data class BootstrapConfig(
    val apiBaseUrl: String,
    val tileUrl: String,
    val glyphUrl: String?,
    val demUrl: String,
)

/**
 * STD-SECURITY rule 4: HTTPS only; the API host must be on an allowlist of exact
 * hostnames, never a wildcard over a public DDNS zone. A rejected or unreachable value
 * falls back to last-known-good, then to the built-in default.
 */
class ConfigResolver(
    private val builtIn: BootstrapConfig,
    private val allowedApiHosts: Set<String>,
) {
    init {
        require(allowedApiHosts.none { it.contains('*') }) { "wildcards are not allowed" }
        require(isAcceptable(builtIn)) { "built-in config must itself be acceptable" }
    }

    fun isAcceptable(config: BootstrapConfig): Boolean {
        val api = parse(config.apiBaseUrl) ?: return false
        if (api.host?.lowercase() !in allowedApiHosts) return false
        val endpoints = listOfNotNull(config.tileUrl, config.glyphUrl, config.demUrl)
        return endpoints.all { isHttpsTemplate(it) }
    }

    fun resolve(fetched: BootstrapConfig?, lastKnownGood: BootstrapConfig?): Resolution = when {
        fetched != null && isAcceptable(fetched) -> Resolution(fetched, Source.FETCHED)
        lastKnownGood != null && isAcceptable(lastKnownGood) -> Resolution(lastKnownGood, Source.LAST_KNOWN_GOOD)
        else -> Resolution(builtIn, Source.BUILT_IN)
    }

    enum class Source { FETCHED, LAST_KNOWN_GOOD, BUILT_IN }

    data class Resolution(val config: BootstrapConfig, val source: Source)

    private fun parse(url: String): URI? = runCatching { URI(url) }.getOrNull()?.takeIf { it.scheme == "https" && it.host != null }

    // Templates such as https://host/{z}/{x}/{y}.pbf are not valid URIs until placeholders are filled.
    // Both braces are escaped: Android's ICU regex rejects a bare "}" that desktop Java accepts.
    private fun isHttpsTemplate(url: String): Boolean =
        parse(url.replace(PLACEHOLDER, "0")) != null

    internal companion object {
        val PLACEHOLDER = Regex("\\{[a-z]+\\}")
    }
}
