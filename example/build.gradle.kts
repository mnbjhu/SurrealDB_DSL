plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.8.0"
    id ("com.google.devtools.ksp")
}

group = "uk.gibby.dsl"
version = "pr-0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    ksp(project(":processor"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")// for JVM:
    testImplementation("org.amshove.kluent:kluent:1.72")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}