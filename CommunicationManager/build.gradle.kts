import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
}

group = "it.polito"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.camel.springboot:camel-spring-boot-starter:4.5.0")
    implementation("org.apache.camel.springboot:camel-google-mail-starter:4.5.0")
    implementation("org.apache.camel:camel-google-mail:4.5.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.apache.camel:camel-jackson:3.20.2")
    implementation ("org.apache.camel:camel-http:3.20.2")
    // Spring for Apache Kafka
    implementation ("org.springframework.kafka:spring-kafka")
    // Jackson for JSON serialization/deserialization
    implementation ("com.fasterxml.jackson.core:jackson-databind")
    implementation ("org.springdoc:springdoc-openapi-ui:1.8.0")
    //implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    //implementation("org.springframework.boot:spring-boot-starter-jdbc")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
    //runtimeOnly("org.postgresql:postgresql")
    //runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
