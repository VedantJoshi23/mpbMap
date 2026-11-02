package io.github.vedantjoshi23.kairos

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/** Launch smoke test: the app must start, stay up, and record no crash or handled error. */
@RunWith(AndroidJUnit4::class)
class LaunchSmokeTest {
    @Test
    fun appLaunchesAndStaysUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        CrashGuard.clear(context)
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(10_000)
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
        }
        val report = CrashGuard.lastCrash(context)
        assertNull("Kairos recorded an error:\n$report", report)
    }
}
