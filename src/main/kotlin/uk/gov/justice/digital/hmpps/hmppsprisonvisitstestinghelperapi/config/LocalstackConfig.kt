package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class LocalstackConfig {
  @Bean("awsSqsClient")
  @ConditionalOnProperty(name = ["hmpps.sqs.provider"], havingValue = "localstack")
  @Primary
  fun awsSqsClient(
    @Value("\${hmpps.sqs.queues.prisonvisitsevents.endpoint.url}") serviceEndpoint: String?,
    @Value("\${hmpps.sqs.queues.prisonvisitsevents.endpoint.region}") region: String?,
  ): AmazonSQSAsync {
    val creds = AnonymousAWSCredentials()
    return AmazonSQSAsyncClientBuilder.standard()
      .withCredentials(AWSStaticCredentialsProvider(creds))
      .withEndpointConfiguration(EndpointConfiguration(serviceEndpoint, region))
      .build()
  }
}
