import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation ("org.keycloak:keycloak-admin-client:20.0.0")  // O la versione di Keycloak che stai usando
	// Spring for Apache Kafka
	implementation ("org.springframework.kafka:spring-kafka")
	// Jackson for JSON serialization/deserialization
	implementation ("com.fasterxml.jackson.core:jackson-databind")
	implementation ("org.springdoc:springdoc-openapi-ui:1.8.0")
	// Debezium dependencies
	implementation("io.debezium:debezium-api:2.4.0.Final")
	implementation("io.debezium:debezium-embedded:2.4.0.Final")
	implementation("io.debezium:debezium-connector-postgres:2.4.0.Final")
	implementation("com.github.loki4j:loki-logback-appender:1.5.2")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
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
