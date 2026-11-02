package io.github.vedantjoshi23.kairos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Direct-boot rule (ADR-0002): onCreate and every eagerly created singleton must never
 * touch credential-encrypted storage. Nothing is initialised here.
 */
@HiltAndroidApp
class KairosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Installs a handler only; it writes nothing until a crash (direct-boot safe).
        CrashGuard.install(this)
    }
}
