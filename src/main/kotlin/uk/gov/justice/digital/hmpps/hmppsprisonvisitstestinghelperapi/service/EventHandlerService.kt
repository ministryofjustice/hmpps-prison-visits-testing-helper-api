package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.DomainEvent
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.NonAssociationEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerAlertCreatedUpdatedEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerReceivedEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerReleasedNotificationDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerRestrictionEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.SQSMessage
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitorRestrictionEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.Events
import java.util.*

@Service
class EventHandlerService(
  private val sqsService: SQSService,
  private val objectMapper: ObjectMapper,
) {
  companion object {
    private const val NOTIFICATION_TYPE = "Notification"
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun handleNonAssociationEvent(nonAssociationEventDto: NonAssociationEventDto) {
    LOG.info("Received non association event with details - $nonAssociationEventDto")
    val values = mutableMapOf<String, String>()
    values["nsPrisonerNumber1"] = nonAssociationEventDto.prisonerCode
    values["nsPrisonerNumber2"] = nonAssociationEventDto.nonAssociationPrisonerCode

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(Events.NON_ASSOCIATION_CREATE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed non association event with details - $nonAssociationEventDto")
  }

  fun handlePrisonerReleaseEvent(prisonerReleasedNotificationDto: PrisonerReleasedNotificationDto) {
    LOG.info("Received prisoner release event with details - $prisonerReleasedNotificationDto")
    val values = mutableMapOf<String, String>()
    values["prisonId"] = prisonerReleasedNotificationDto.prisonCode
    values["nomsNumber"] = prisonerReleasedNotificationDto.prisonerCode
    values["reason"] = prisonerReleasedNotificationDto.reason

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(Events.PRISONER_RELEASE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed prisoner release event with details - $prisonerReleasedNotificationDto")
  }

  fun handlePrisonerReceivedEvent(prisonerEventDto: PrisonerReceivedEventDto) {
    LOG.info("Received prisoner receive event with details - $prisonerEventDto")
    val values = mutableMapOf<String, String>()
    values["prisonId"] = prisonerEventDto.prisonCode
    values["nomsNumber"] = prisonerEventDto.prisonerCode
    values["reason"] = prisonerEventDto.reason

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(Events.PRISONER_RECEIVE_EVENT.eventType, values)), UUID.randomUUID().toString()))
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

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(Events.PRISONER_RESTRICTION_CHANGE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed prisoner restriction change event with details - $prisonerRestrictionEventDto")
  }

  fun handlePrisonerAlertCreatedUpdatedEvent(prisonerAlertCreatedUpdatedEventDto: PrisonerAlertCreatedUpdatedEventDto) {
    LOG.info("received prisoner alert updated event with details - $prisonerAlertCreatedUpdatedEventDto")

    val values = mutableMapOf<String, Any>()
    values["nomsNumber"] = prisonerAlertCreatedUpdatedEventDto.prisonerCode
    values["description"] = prisonerAlertCreatedUpdatedEventDto.description
    values["alertsAdded"] = prisonerAlertCreatedUpdatedEventDto.alertsAdded
    values["alertsRemoved"] = prisonerAlertCreatedUpdatedEventDto.alertsRemoved

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(Events.PRISONER_ALERT_UPDATED_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed prisoner alert updated event with details - $prisonerAlertCreatedUpdatedEventDto")
  }

  fun handleVisitorRestrictionChangeEvent(visitorRestrictionEventDto: VisitorRestrictionEventDto) {
    LOG.info("Received visitor restriction change event with details - $visitorRestrictionEventDto")
    val values = mutableMapOf<String, String>()
    values["personId"] = visitorRestrictionEventDto.visitorId
    values["effectiveDate"] = visitorRestrictionEventDto.startDate.toString()
    visitorRestrictionEventDto.endDate?.let {
      values["expiryDate"] = it.toString()
    }

    sqsService.sendDomainEvent(SQSMessage(NOTIFICATION_TYPE, objectMapper.writeValueAsString(DomainEvent(Events.VISITOR_RESTRICTION_CHANGE_EVENT.eventType, values)), UUID.randomUUID().toString()))
    LOG.info("processed visitor restriction change event with details - $visitorRestrictionEventDto")
  }
}
