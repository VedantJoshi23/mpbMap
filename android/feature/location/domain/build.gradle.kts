plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":core:kernel"))
    api(project(":feature:location:api"))
    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
}
