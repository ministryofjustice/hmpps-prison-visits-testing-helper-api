package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.DomainEvent
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.NonAssociationEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.SQSMessage
import java.util.*

@Service
class EventHandlerService(
  private val sqsService: SQSService,
  private val objectMapper: ObjectMapper,
) {

  companion object {
    private const val NON_ASSOCIATION_CREATE_EVENT = "non-associations.created"
    private const val NOTIFICATION_TYPE = "Notification"
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun handleNonAssociationEvent(nonAssociationEventDto: NonAssociationEventDto) {
    LOG.info("received non association event with details - $nonAssociationEventDto")
    val values = mutableMapOf<String, String>()
    values["nsPrisonerNumber1"] = nonAssociationEventDto.prisonerCode
    values["nsPrisonerNumber2"] = nonAssociationEventDto.nonAssociationPrisonerCode

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(NON_ASSOCIATION_CREATE_EVENT, values)), UUID.randomUUID().toString()))
  }
}
