package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.config

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class SpringCloudAwsConfig {
  @Bean
  @ConditionalOnProperty(name = ["hmpps.sqs.provider"], havingValue = "aws")
  @Primary
  fun awsSqsClient(
    @Value("\${hmpps.sqs.queues.prisonvisitsevents.endpoint.region}") region: String,
  ): AmazonSQSAsync {
    val amazonSQSAsync = AmazonSQSAsyncClientBuilder.standard()
      .withCredentials(DefaultAWSCredentialsProviderChain())
      .withRegion(region)
      .build()

    return amazonSQSAsync
  }
}
