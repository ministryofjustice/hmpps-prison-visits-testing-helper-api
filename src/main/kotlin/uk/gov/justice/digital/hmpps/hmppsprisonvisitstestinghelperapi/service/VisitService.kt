package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.VisitSchedulerClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.TestDBNotificationEventTypes
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.ActionedByRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.EventAuditRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.SessionSlotRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.VisitRepository
import java.lang.Thread.sleep
import java.util.UUID

@Service
@Transactional
class VisitService(
  private val visitRepository: VisitRepository,
  private val visitSchedulerClient: VisitSchedulerClient,
  private val applicationService: ApplicationService,
  private val eventAuditRepository: EventAuditRepository,
  private val actionedByRepository: ActionedByRepository,
  private val sessionSlotRepository: SessionSlotRepository,
) {

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun setVisitPrison(reference: String, prisonCode: String): Boolean {
    logger.debug("Enter setVisitPrison {} {} ", reference, prisonCode)
    val result = visitRepository.setVisitPrison(reference, prisonCode)
    logger.debug("setVisitPrison result: {}", result)
    return result > 0
  }

  fun setVisitStatus(reference: String, status: VisitStatus): Boolean {
    logger.debug("Enter setVisitStatus {} {} ", reference, status)
    val result = visitRepository.setVisitStatus(reference, status.name)
    logger.debug("setVisitStatus result: {}", result)
    return result > 0
  }

  fun isVisitBooked(reference: String): Boolean {
    logger.debug("Enter isVisitBooked, {}", reference)
    val result = visitRepository.isVisitBooked(reference)
    logger.debug("isVisitBooked result: {}", result)
    return result
  }

  fun deleteVisitNotificationEventsByBookingReference(bookingReference: String): Int {
    logger.debug("Delete visit notification events for booking reference - {}", bookingReference)
    val result = visitRepository.deleteVisitNotificationEventsByBookingReference(bookingReference)
    logger.debug("Deleted {} visit notification events for booking reference - {}", result, bookingReference)
    return result
  }

  fun createVisitNotificationEvents(bookingReference: String, notificationType: TestDBNotificationEventTypes) {
    logger.debug("Create visit notification event {} for booking reference - {}", notificationType, bookingReference)
    val reference = UUID.randomUUID().toString()
    visitRepository.createVisitNotificationEvents(bookingReference, notificationType.toString(), reference)
    logger.debug("Created visit notification event {} for booking reference - {}", notificationType, bookingReference)
  }

  fun deleteVisitAndChildren(bookingReference: String) {
    val visitId = visitRepository.getVisitId(bookingReference)

    visitId?.let {
      visitSchedulerClient.cancelVisitByBookingReference(bookingReference)

      // Wait for 5seconds before deleting the visit and it's children, as downstream services need time to process the
      // cancellation and refund the VO balance and update nomis. This is done via the visit-scheduler publishing a visit cancelled
      // event, which is then picked up by other services to process.
      sleep(5000)

      visitRepository.deleteVisitVisitors(it)
      visitRepository.deleteVisitSupport(it)
      visitRepository.deleteVisitNotes(it)
      visitRepository.deleteVisitContact(it)
      visitRepository.deleteVisitLegacy(it)
      visitRepository.deleteVisit(it)
      visitRepository.deleteVisitNotificationEventsByBookingReference(bookingReference)

      val applicationReference = applicationService.getApplicationReferenceByVisitId(it)
      applicationService.deleteApplicationAndChildren(applicationReference)

      eventAuditRepository.deleteByBookingReference(bookingReference)
      sessionSlotRepository.deleteUnused()
      actionedByRepository.deleteUnused()
    }
  }

  fun cancelVisitByBookingReference(bookingReference: String) {
    logger.debug("cancelVisitByReference called with reference - {}", bookingReference)

    visitSchedulerClient.cancelVisitByBookingReference(bookingReference)
  }
}
