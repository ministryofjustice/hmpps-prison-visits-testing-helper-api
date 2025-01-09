package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@Configuration
class SpringCloudAwsConfig {
  @Bean
  @ConditionalOnProperty(name = ["hmpps.sqs.provider"], havingValue = "aws")
  @Primary
  fun awsSqsClient(
    @Value("\${hmpps.sqs.queues.prisonvisitsevents.endpoint.region}") region: String,
  ): SqsAsyncClient {
    val amazonSQSAsync = SqsAsyncClient.builder()
      .region(Region.of(region))
      .credentialsProvider(DefaultCredentialsProvider.create())
      .build()

    return amazonSQSAsync
  }
}
