plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "6.0.4"
  kotlin("plugin.spring") version "2.0.20"
  id("org.jetbrains.kotlin.plugin.noarg") version "2.0.20"
  kotlin("plugin.jpa") version "2.0.20"
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
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:4.3.2")
  implementation("com.amazonaws:aws-java-sdk-sts:1.12.770")
  implementation("org.springframework.cloud:spring-cloud-starter-aws-messaging:2.2.6.RELEASE")
  implementation("com.amazonaws:aws-java-sdk-s3:1.12.770")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.7.0")

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
  implementation("org.springdoc:springdoc-openapi-starter-common:2.6.0")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  runtimeOnly("org.postgresql:postgresql:42.7.4")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.jsonwebtoken:jjwt:0.12.6")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")
}

kotlin {
  jvmToolchain(21)
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
  }

  withType<Test> {
    val includeIntegrationTests = System.getProperty("include.integration.tests")?.toBoolean() ?: false
    if (includeIntegrationTests) {
      include("**/*Integration*")
    } else {
      exclude("**/*Integration*")
    }
  }
}
