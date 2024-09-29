import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.protoc
import org.apache.tools.ant.taskdefs.condition.Os
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"

	id("com.google.protobuf") version "0.8.17"
	id("org.jmailen.kotlinter") version "4.4.1"
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

buildscript {
	dependencies {
		classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.17")
	}
}

sourceSets {
	main {
		proto {
			srcDir("src/main/proto")
		}
		java {
			srcDir("build/generated/source/proto/main/java")
		}
	}
}

protobuf.protobuf.protoc {
		artifact = "com.google.protobuf:protoc:3.18.1"
}

group = "dev.minuk"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	implementation("com.github.f4b6a3:ulid-creator:5.2.3")

	implementation("com.google.protobuf:protobuf-java:3.18.1")
	implementation("com.google.protobuf:protobuf-java-util:3.18.1")

	implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")
	runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.8.0-alpha")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mongodb")
	testImplementation("com.tngtech.archunit:archunit:1.3.0")
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("com.ninja-squad:springmockk:4.0.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs("-Xshare:off")
}

tasks.withType<KotlinCompile>().all {
	kotlinOptions {
		jvmTarget = "17"
	}
}

tasks {
	withType<Copy> {
		filesMatching("**/*.proto") {
			duplicatesStrategy = DuplicatesStrategy.INCLUDE
		}
	}
}

kotlinter {
	failBuildWhenCannotAutoFormat = true
	ignoreFailures = true
	reporters = arrayOf("checkstyle", "plain")
}

tasks.withType<LintTask> {
	source = source.minus(fileTree("build/generated")).asFileTree
}

tasks.withType<FormatTask> {
	source = source.minus(fileTree("build/generated")).asFileTree
}