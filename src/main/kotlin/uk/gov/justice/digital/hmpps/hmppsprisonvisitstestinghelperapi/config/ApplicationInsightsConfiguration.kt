package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.config

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Application insights now controlled by the spring-boot-starter dependency.  However when the key is not specified
 * we don't get a telemetry bean and application won't start.  Therefore need this backup configuration.
 */
@Configuration
open class ApplicationInsightsConfiguration {

  @Bean
  open fun telemetryClient(): TelemetryClient {
    return TelemetryClient()
  }
}

@Suppress("unused")
fun TelemetryClient.trackEvent(name: String, properties: Map<String, String>) = this.trackEvent(name, properties, null)
