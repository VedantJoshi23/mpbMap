package io.github.vedantjoshi23.kairos.core.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfigResolverTest {
    private val builtIn = BootstrapConfig(
        apiBaseUrl = "https://kairos-api.duckdns.org/",
        tileUrl = "https://tiles.openfreemap.org/planet/{z}/{x}/{y}.pbf",
        glyphUrl = null,
        demUrl = "https://elevation-tiles-prod.s3.amazonaws.com/terrarium/{z}/{x}/{y}.png",
    )
    private val resolver = ConfigResolver(builtIn, setOf("kairos-api.duckdns.org", "kairos-api2.duckdns.org"))

    @Test fun acceptsAllowlistedHttpsConfig() {
        val fetched = builtIn.copy(apiBaseUrl = "https://kairos-api2.duckdns.org/")
        assertEquals(ConfigResolver.Source.FETCHED, resolver.resolve(fetched, null).source)
    }

    @Test fun rejectsOtherNameInTheSameDdnsZone() {
        val evil = builtIn.copy(apiBaseUrl = "https://attacker.duckdns.org/")
        assertEquals(ConfigResolver.Source.BUILT_IN, resolver.resolve(evil, null).source)
    }

    @Test fun rejectsPlainHttp() {
        val http = builtIn.copy(apiBaseUrl = "http://kairos-api.duckdns.org/")
        assertEquals(ConfigResolver.Source.BUILT_IN, resolver.resolve(http, null).source)
        val httpTiles = builtIn.copy(tileUrl = "http://tiles.example/{z}/{x}/{y}.pbf")
        assertEquals(ConfigResolver.Source.BUILT_IN, resolver.resolve(httpTiles, null).source)
    }

    @Test fun fallsBackToLastKnownGoodThenBuiltIn() {
        val lkg = builtIn.copy(apiBaseUrl = "https://kairos-api2.duckdns.org/")
        val bad = builtIn.copy(apiBaseUrl = "https://evil.example/")
        assertEquals(ConfigResolver.Resolution(lkg, ConfigResolver.Source.LAST_KNOWN_GOOD), resolver.resolve(bad, lkg))
        assertEquals(ConfigResolver.Source.BUILT_IN, resolver.resolve(null, bad).source)
        assertEquals(builtIn, resolver.resolve(null, null).config)
    }

    /** Regression: Android (ICU) rejects an unescaped "}" in the pattern; keep both braces escaped. */
    @Test fun placeholderPatternEscapesBothBraces() {
        assertEquals("\\{[a-z]+\\}", ConfigResolver.PLACEHOLDER.pattern)
        assertEquals("https://t/0/0/0.pbf", "https://t/{z}/{x}/{y}.pbf".replace(ConfigResolver.PLACEHOLDER, "0"))
    }

    @Test fun refusesWildcardAllowlists() {
        assertFailsWith<IllegalArgumentException> { ConfigResolver(builtIn, setOf("*.duckdns.org")) }
    }
}
