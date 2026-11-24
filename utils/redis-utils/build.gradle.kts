plugins {
    id("java")
    id("daily-expenses-management.spring-library")
}


group = "com.caminha.javadailyexpenses"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    implementation(project(":utils:json-utils"))

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}