import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	java
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
}

group = "uk.gov.hmcts.example"
version = "0.0.1-SNAPSHOT"


object Versions {
	val lombokVersion   			= "1.18.30"
	val artemisJMSServerVersion 	= "2.31.0"
	val jacksonMappingVersion 		= "2.15.2"
	val jaywayJsonPathVersion 		= "2.8.0"
	val junitPlatformLauncherVersion= "1.10.0"
	val junitVersion       			= "5.10.0"
	val hamcrestVersion    			= "2.2"
	val mockitoVersion     			= "5.4.0"
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
	testImplementation("org.projectlombok:lombok:${Versions.lombokVersion}")
	// https://mvnrepository.com/artifact/org.projectlombok/lombok
	// https://projectlombok.org/setup/gradle
	compileOnly("org.projectlombok:lombok:${Versions.lombokVersion}")
	
	annotationProcessor("org.projectlombok:lombok:${Versions.lombokVersion}")

	implementation("org.springframework.boot:spring-boot-starter-activemq")
	implementation("org.springframework.boot:spring-boot-starter-artemis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework:spring-jms")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.jacksonMappingVersion}")
	implementation("com.fasterxml.jackson.core:jackson-core:${Versions.jacksonMappingVersion}")
	implementation("com.jayway.jsonpath:json-path:${Versions.jaywayJsonPathVersion}")

	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testImplementation("org.hamcrest:hamcrest:${Versions.hamcrestVersion}")
	testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junitVersion}")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:${Versions.junitVersion}")
	testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.junitVersion}")
	testImplementation("org.mockito:mockito-core:${Versions.mockitoVersion}")
	testImplementation("org.mockito:mockito-junit-jupiter:${Versions.mockitoVersion}")

	// https://mvnrepository.com/artifact/org.junit.platform/junit-platform-launcher
	testImplementation("org.junit.platform:junit-platform-launcher:${Versions.junitPlatformLauncherVersion}")

	// https://mvnrepository.com/artifact/org.apache.activemq/artemis-jms-server
	testImplementation("org.apache.activemq:artemis-jms-server:${Versions.artemisJMSServerVersion}")

	// https://mvnrepository.com/artifact/org.apache.activemq/artemis-junit-5
	testImplementation("org.apache.activemq:artemis-junit-5:${Versions.artemisJMSServerVersion}")

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
