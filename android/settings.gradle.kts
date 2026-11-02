pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "kairos"

// Pure-Kotlin (JVM) modules: domain logic and api contracts. These build and test
// without an Android SDK (KC-169).
include(
    ":core:kernel",
    ":core:eventbus",
    ":core:config",
    ":feature:trip:api",
    ":feature:trip:domain",
    ":feature:location:api",
    ":feature:location:domain",
    ":feature:map:api",
    ":feature:map:domain",
)

// Android modules (ARCH-SYSTEM §4). They are skipped with -PjvmOnly=true, e.g. on
// the aarch64 VM, which has no Android SDK.
val jvmOnly = providers.gradleProperty("jvmOnly").orNull == "true"
if (!jvmOnly) {
    include(
        ":app",
        ":core:designsystem",
        ":feature:trip:data",
        ":feature:trip:platform",
        ":feature:trip:ui",
        ":feature:location:data",
        ":feature:location:platform",
        ":feature:location:ui",
        ":feature:map:platform",
        ":feature:map:ui",
    )
}
