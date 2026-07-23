plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "11.0.1"
  kotlin("plugin.spring") version "2.4.10"
  id("org.jetbrains.kotlin.plugin.noarg") version "2.4.10"
  kotlin("plugin.jpa") version "2.4.10"
  id("org.owasp.dependencycheck") version "12.2.2"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

repositories {
  maven { url = uri("https://repo.spring.io/milestone") }
  mavenCentral()
}

dependencies {

  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:3.0.0")
  implementation("org.springframework.boot:spring-boot-starter-webclient")
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:7.4.0")
  implementation("com.amazonaws:aws-java-sdk-sts:1.12.797")
  implementation("com.amazonaws:aws-java-sdk-s3:1.12.797")

  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.30.0")

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  runtimeOnly("org.postgresql:postgresql:42.7.13")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:3.0.0")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
  testImplementation("org.wiremock:wiremock-standalone:3.13.2")
}

kotlin {
  jvmToolchain(25)
}

java {
  sourceCompatibility = JavaVersion.VERSION_24
  targetCompatibility = JavaVersion.VERSION_24
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24
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
