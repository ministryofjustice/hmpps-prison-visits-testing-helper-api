package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.VisitSchedulerClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.TestDBNotificationEventTypes
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitSubStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.visit.scheduler.VisitDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.ActionedByRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.EventAuditRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.SessionSlotRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.VisitNotifyHistoryRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.VisitRepository
import java.lang.Thread.sleep
import java.util.*

@Service
@Transactional
class VisitService(
  private val visitRepository: VisitRepository,
  private val visitSchedulerClient: VisitSchedulerClient,
  private val applicationService: ApplicationService,
  private val eventAuditRepository: EventAuditRepository,
  private val visitNotifyHistory: VisitNotifyHistoryRepository,
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
    val visitSubStatus = if (status == VisitStatus.BOOKED) {
      VisitSubStatus.AUTO_APPROVED
    } else {
      VisitSubStatus.CANCELLED
    }

    val result = visitRepository.setVisitStatus(reference, status.name, visitSubStatus.name)
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
    val visitId = visitRepository.getVisitId(bookingReference) ?: throw RuntimeException("Visit with booking reference - $bookingReference does not exist")
    visitRepository.createVisitNotificationEvents(bookingReference, notificationType.toString(), reference, visitId)
    logger.debug("Created visit notification event {} for booking reference - {}", notificationType, bookingReference)
  }

  fun deleteAllPrisonerVisits(prisonerId: String) {
    logger.debug("Deleting all future visits for prisoner - {}", prisonerId)

    val visitReferences = visitRepository.getVisitsByPrisonerId(prisonerId)
    visitReferences?.forEach { visitReference ->
      logger.debug("Delete all visit with reference - {}", visitReference)
      deleteVisitAndChildren(visitReference)
    }

    logger.debug("Finished deleting all future visits for prisoner - {}", prisonerId)
  }

  fun deleteVisitAndChildren(bookingReference: String) {
    val visitId = visitRepository.getVisitId(bookingReference)

    visitId?.let {
      if (visitRepository.isVisitBooked(bookingReference)) {
        try {
          visitSchedulerClient.cancelVisitByBookingReference(bookingReference)

          // Wait for 3 seconds before deleting the visit and it's children, as downstream services need time to process the
          // cancellation and refund the VO balance and update nomis. This is done via the visit-scheduler publishing a visit cancelled
          // event, which is then picked up by other services to process.
          sleep(3000)
        } catch (e: Exception) {
          // ignore any visits that cannot be cancelled
          logger.info("Unable to cancel visit with booking reference - {}, exception details - {}", bookingReference, e.toString())
        }
      }

      visitRepository.deleteVisitVisitors(it)
      visitRepository.deleteVisitSupport(it)
      visitRepository.deleteVisitNotes(it)
      visitRepository.deleteVisitContact(it)
      visitRepository.deleteVisitLegacy(it)
      visitRepository.deleteVisitNotificationEventsByBookingReference(bookingReference)
      visitRepository.deleteVisit(it)

      val applicationReference = applicationService.getApplicationReferenceByVisitId(it)
      applicationService.deleteApplicationAndChildren(applicationReference)

      visitNotifyHistory.deleteByBookingReference(bookingReference)
      eventAuditRepository.deleteByBookingReference(bookingReference)
      sessionSlotRepository.deleteUnused()
      actionedByRepository.deleteUnused()
    }
  }

  fun cancelVisitByBookingReference(bookingReference: String) {
    logger.debug("cancelVisitByReference called with reference - {}", bookingReference)

    visitSchedulerClient.cancelVisitByBookingReference(bookingReference)
  }

  fun bookVisit(applicationReference: String): VisitDto {
    logger.debug("book visit called for application with reference - {}", applicationReference)
    val visit = visitSchedulerClient.bookVisit(applicationReference) ?: throw RuntimeException("Unble to book visit.")
    logger.debug("booked visit with visit reference - {}", visit.reference)
    return visit
  }
}
