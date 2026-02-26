package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageResponse
import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.SQSMessage
import java.util.concurrent.CompletableFuture

@Service
class SQSService(
  @param:Qualifier("awsSqsClient") private val amazonSqs: SqsAsyncClient,
  @param:Qualifier("objectMapper") private val objectMapper: ObjectMapper,
  @param:Value("\${hmpps.sqs.queues.prisonvisitsevents.queue.url}") private val queueUrl: String,
) {
  companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun sendDomainEvent(payload: SQSMessage) {
    LOG.info("Sending message to URL - $queueUrl with payload - $payload")

    // Serialize the payload into a JSON string
    val messageBody = objectMapper.writeValueAsString(payload)

    // Create the SendMessageRequest with the serialized message
    val sendMessageRequest = SendMessageRequest.builder()
      .queueUrl(queueUrl)
      .messageBody(messageBody)
      .build()

    // Send the message asynchronously
    val sendMessageResponseFuture: CompletableFuture<SendMessageResponse> = amazonSqs.sendMessage(sendMessageRequest)

    // Handle the response or error asynchronously
    sendMessageResponseFuture.thenAccept { sendMessageResponse ->
      LOG.info("Message sent successfully with message ID: ${sendMessageResponse.messageId()}")
    }.exceptionally { ex ->
      LOG.error("Error sending message", ex)
      null
    }
  }
}
