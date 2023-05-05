plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.8.0"
    `maven-publish`
}

group = "uk.gibby.dsl"
version = "pr-0.0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    implementation("io.ktor:ktor-client-websockets:2.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.2")
    implementation("io.ktor:ktor-client-core:2.2.2")
    implementation("io.ktor:ktor-client-cio:2.2.2")
    implementation("io.ktor:ktor-client-encoding:2.2.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}
