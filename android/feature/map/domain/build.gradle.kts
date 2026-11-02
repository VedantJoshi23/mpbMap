plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":core:kernel"))
    api(project(":feature:map:api"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
}
