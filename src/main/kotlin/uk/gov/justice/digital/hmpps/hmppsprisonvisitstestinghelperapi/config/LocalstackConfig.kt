package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Configuration
class LocalstackConfig {
  @Bean("awsSqsClient")
  @ConditionalOnProperty(name = ["hmpps.sqs.provider"], havingValue = "localstack")
  @Primary
  fun awsSqsClient(
    @Value("\${hmpps.sqs.queues.prisonvisitsevents.endpoint.url}") serviceEndpoint: String,
    @Value("\${hmpps.sqs.queues.prisonvisitsevents.endpoint.region}") region: String,
  ): SqsAsyncClient {
    return SqsAsyncClient.builder()
      .endpointOverride(URI.create(serviceEndpoint))
      .region(Region.of(region))
      .credentialsProvider(AnonymousCredentialsProvider.create())
      .build()
  }
}
