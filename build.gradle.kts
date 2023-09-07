import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	java
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
}

group = "uk.gov.hmcts.example"
version = "0.0.1-SNAPSHOT"


object Versions {
	val jacksonMappingVersion = "2.15.2";
	val jaywayJsonPathVersion = "2.8.0";
	val junitPlatformLauncherVersion = "1.10.0";
	val junitVersion       = "5.10.0";
	val hamcrestVersion    = "2.2";
	val mockitoVersion     = "5.4.0";
}

// HINT: Force override of Spring Dependency management - Avoid NoSuchMethodError conflicts
// SEE ALSO: https://stackoverflow.com/questions/47453558/override-spring-boot-dependency-version-with-gradle-kotlin-dsl
// SEE ALSO: https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/
extra["junit-jupiter.version"] = Versions.junitVersion


java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-activemq")
	implementation("org.springframework.boot:spring-boot-starter-artemis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework:spring-jms")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.jacksonMappingVersion}")
	implementation("com.fasterxml.jackson.core:jackson-core:${Versions.jacksonMappingVersion}")
	implementation("com.jayway.jsonpath:json-path:${Versions.jaywayJsonPathVersion}")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testImplementation("org.hamcrest:hamcrest:${Versions.hamcrestVersion}")
	testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junitVersion}")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:${Versions.junitVersion}")
	testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.junitVersion}")
	testImplementation("org.mockito:mockito-core:${Versions.mockitoVersion}")
	testImplementation("org.mockito:mockito-junit-jupiter:${Versions.mockitoVersion}")

	// https://mvnrepository.com/artifact/org.junit.platform/junit-platform-launcher
	testImplementation("org.junit.platform:junit-platform-launcher:${Versions.junitPlatformLauncherVersion}")

	// https://mvnrepository.com/artifact/org.apache.activemq/activemq-broker
	testImplementation("org.apache.activemq:activemq-broker:5.18.2")

	// https://mvnrepository.com/artifact/org.apache.activemq.tooling/activemq-junit
	testImplementation("org.apache.activemq.tooling:activemq-junit:5.18.2")

	// https://mvnrepository.com/artifact/org.apache.activemq/activemq-pool
	testImplementation("org.apache.activemq:activemq-pool:5.18.2")

	// https://mvnrepository.com/artifact/org.apache.activemq/activemq-kahadb-store
	testImplementation("org.apache.activemq:activemq-kahadb-store:5.18.2")

	// https://mvnrepository.com/artifact/javax.jms/javax.jms-api
	testImplementation("javax.jms:javax.jms-api:2.0.1")

}

tasks.withType<Test> {
	useJUnitPlatform()

	testLogging {
		events = hashSetOf(
				TestLogEvent.FAILED,
				TestLogEvent.PASSED,
				TestLogEvent.SKIPPED,
				TestLogEvent.STANDARD_OUT,
				TestLogEvent.STANDARD_ERROR
		)
		showStandardStreams = true
	}
}
