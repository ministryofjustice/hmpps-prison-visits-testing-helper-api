plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.15.6"
  kotlin("plugin.spring") version "1.9.23"
  id("org.jetbrains.kotlin.plugin.noarg") version "1.9.23"
  kotlin("plugin.jpa") version "1.9.23"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

repositories {
  maven { url = uri("https://repo.spring.io/milestone") }
  mavenCentral()
}

dependencies {

  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:3.1.3")
  implementation("com.amazonaws:aws-java-sdk-sts:1.12.722")
  implementation("org.springframework.cloud:spring-cloud-starter-aws-messaging:2.2.6.RELEASE")
  implementation("com.amazonaws:aws-java-sdk-s3:1.12.722")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.3.0")

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.5.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
  implementation("org.springdoc:springdoc-openapi-starter-common:2.5.0")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  runtimeOnly("org.postgresql:postgresql:42.7.3")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

kotlin {
  jvmToolchain(21)
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "21"
    }
  }
}
