package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.core.env.ResourceIdResolver
import org.springframework.cloud.aws.messaging.core.QueueMessageChannel
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.SQSMessage

@Service
class SQSService(
  @Qualifier("awsSqsClient") private val amazonSqs: AmazonSQSAsync,
  @Value("\${hmpps.sqs.queues.prisonvisitsevents.queue.url}") private val queueUrl: String,
) {
  companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  private val queueTemplate: QueueMessagingTemplate = QueueMessagingTemplate(
    amazonSqs,
    null as ResourceIdResolver?,
    MappingJackson2MessageConverter().apply {
      val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
      mapper.dateFormat = StdDateFormat()
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      mapper.registerModule(JavaTimeModule())
      this.serializedPayloadClass = String::class.java
      this.objectMapper = mapper
    },
  )

  fun sendDomainEvent(payload: SQSMessage) {
    LOG.info("sending message to url - $queueUrl with payload - $payload")
    queueTemplate.convertAndSend(QueueMessageChannel(amazonSqs, queueUrl), payload)
    LOG.info("finished sending message to url - $queueUrl with payload - $payload")
  }
}
