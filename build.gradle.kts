plugins {
	java
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
}

group = "uk.gov.hmcts.example"
version = "0.0.1-SNAPSHOT"


object Versions {
	val junitVersion       = "5.10.0";
	val hamcrestVersion    = "2.2";
	val mockitoVersion     = "5.4.0";
}


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
}

tasks.withType<Test> {
	useJUnitPlatform()
}
