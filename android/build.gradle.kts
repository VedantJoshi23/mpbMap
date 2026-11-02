// Root build. Plugins are declared here (not applied) so every module resolves the
// same versions from gradle/libs.versions.toml. Resolving the Android plugins needs
// no Android SDK; only Android build tasks do (KC-169).
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
