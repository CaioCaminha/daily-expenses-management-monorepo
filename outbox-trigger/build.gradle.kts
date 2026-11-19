plugins {
    id("java")
    application
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.caminha.javadailyexpenses"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.apache.kafka:kafka-clients:4.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
}

application {
    mainClass.set("com.caminha.outboxtrigger.MainClassKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}