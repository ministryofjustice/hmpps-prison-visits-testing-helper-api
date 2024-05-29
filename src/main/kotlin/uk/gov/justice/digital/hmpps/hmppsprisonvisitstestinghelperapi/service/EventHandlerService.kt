package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.DomainEvent
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.NonAssociationEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerReceivedEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerRestrictionEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.SQSMessage
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitorRestrictionEventDto
import java.util.*

@Service
class EventHandlerService(
  private val sqsService: SQSService,
  private val objectMapper: ObjectMapper,
) {
  enum class EVENTS(val eventType: String) {
    NON_ASSOCIATION_CREATE_EVENT("non-associations.created"),
    PRISONER_RELEASE_EVENT("prison-offender-events.prisoner.released"),
    PRISONER_RECEIVE_EVENT("prison-offender-events.prisoner.received"),
    PRISONER_RESTRICTION_CHANGE_EVENT("prison-offender-events.prisoner.restriction.changed"),
    VISITOR_RESTRICTION_CHANGE_EVENT("prison-offender-events.visitor.restriction.changed"),
  }

  companion object {
    private const val NOTIFICATION_TYPE = "Notification"
    private const val RELEASE_REASON_TYPE = "RELEASED"
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun handleNonAssociationEvent(nonAssociationEventDto: NonAssociationEventDto) {
    LOG.info("Received non association event with details - $nonAssociationEventDto")
    val values = mutableMapOf<String, String>()
    values["nsPrisonerNumber1"] = nonAssociationEventDto.prisonerCode
    values["nsPrisonerNumber2"] = nonAssociationEventDto.nonAssociationPrisonerCode

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(EVENTS.NON_ASSOCIATION_CREATE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed non association event with details - $nonAssociationEventDto")
  }

  fun handlePrisonerReleaseEvent(prisonerEventDto: PrisonerEventDto) {
    LOG.info("Received prisoner release event with details - $prisonerEventDto")
    val values = mutableMapOf<String, String>()
    values["prisonId"] = prisonerEventDto.prisonCode
    values["nomsNumber"] = prisonerEventDto.prisonerCode
    values["reason"] = RELEASE_REASON_TYPE

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(EVENTS.PRISONER_RELEASE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed prisoner release event with details - $prisonerEventDto")
  }

  fun handlePrisonerReceivedEvent(prisonerEventDto: PrisonerReceivedEventDto) {
    LOG.info("Received prisoner receive event with details - $prisonerEventDto")
    val values = mutableMapOf<String, String>()
    values["prisonId"] = prisonerEventDto.prisonCode
    values["nomsNumber"] = prisonerEventDto.prisonerCode
    values["reason"] = prisonerEventDto.reason

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(EVENTS.PRISONER_RECEIVE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed prisoner receive event with details - $prisonerEventDto")
  }

  fun handlePrisonerRestrictionChangeEvent(prisonerRestrictionEventDto: PrisonerRestrictionEventDto) {
    LOG.info("Received prisoner restriction change event with details - $prisonerRestrictionEventDto")
    val values = mutableMapOf<String, String>()
    values["nomsNumber"] = prisonerRestrictionEventDto.prisonerCode
    values["effectiveDate"] = prisonerRestrictionEventDto.startDate.toString()
    prisonerRestrictionEventDto.endDate?.let {
      values["expiryDate"] = it.toString()
    }

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(EVENTS.PRISONER_RESTRICTION_CHANGE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed prisoner restriction change event with details - $prisonerRestrictionEventDto")
  }

  fun handleVisitorRestrictionChangeEvent(visitorRestrictionEventDto: VisitorRestrictionEventDto) {
    LOG.info("Received visitor restriction change event with details - $visitorRestrictionEventDto")
    val values = mutableMapOf<String, String>()
    values["personId"] = visitorRestrictionEventDto.visitorId
    values["effectiveDate"] = visitorRestrictionEventDto.startDate.toString()
    visitorRestrictionEventDto.endDate?.let {
      values["expiryDate"] = it.toString()
    }

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(EVENTS.VISITOR_RESTRICTION_CHANGE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed visitor restriction change event with details - $visitorRestrictionEventDto")
  }
}
