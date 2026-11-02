package io.github.vedantjoshi23.kairos

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.vedantjoshi23.kairos.core.config.BootstrapConfig
import io.github.vedantjoshi23.kairos.core.config.ConfigResolver
import io.github.vedantjoshi23.kairos.core.eventbus.EventBus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun eventBus(): EventBus = EventBus()

    /**
     * Built-in endpoints. The API host allowlist holds exact hostnames only
     * (STD-SECURITY rule 4). The API host is a placeholder until the backend ships (slice 3).
     */
    @Provides @Singleton fun configResolver(): ConfigResolver = ConfigResolver(
        builtIn = BootstrapConfig(
            apiBaseUrl = "https://kairos-api.duckdns.org/",
            tileUrl = "https://tiles.openfreemap.org/planet/20261025_080012_pt/{z}/{x}/{y}.pbf",
            glyphUrl = null,
            demUrl = "https://elevation-tiles-prod.s3.amazonaws.com/terrarium/{z}/{x}/{y}.png",
        ),
        allowedApiHosts = setOf("kairos-api.duckdns.org"),
    )
}
