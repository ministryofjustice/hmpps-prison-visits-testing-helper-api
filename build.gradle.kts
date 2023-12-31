plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.10.1"
  kotlin("plugin.spring") version "1.9.21"
  id("org.jetbrains.kotlin.plugin.noarg") version "1.9.21"
  kotlin("plugin.jpa") version "1.9.21"
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
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:2.1.1")
  implementation("com.amazonaws:aws-java-sdk-sts:1.12.608")
  implementation("com.hazelcast:hazelcast:5.3.6")
  implementation("com.hazelcast:hazelcast-spring:5.3.6")
  implementation("org.springframework.cloud:spring-cloud-starter-aws-messaging:2.2.6.RELEASE")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:1.32.0")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
  implementation("org.springframework.data:spring-data-commons:3.2.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.3.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
  implementation("org.springdoc:springdoc-openapi-starter-common:2.3.0")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  runtimeOnly("org.postgresql:postgresql:42.7.1")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.19")
  testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")
  testImplementation("org.mockito:mockito-inline:5.2.0")
  testImplementation("org.testcontainers:localstack:1.19.3")
  testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
  testImplementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.jsonwebtoken:jjwt:0.12.3")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.testcontainers:postgresql:1.19.3")
  testImplementation("org.testcontainers:localstack:1.19.3")
  testImplementation("com.amazonaws:aws-java-sdk-s3:1.12.608")
  testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0")
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
