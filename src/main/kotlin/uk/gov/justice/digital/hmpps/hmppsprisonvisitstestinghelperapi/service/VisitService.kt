package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.VisitSchedulerClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.TestDBNotificationEventTypes
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.VisitRepository
import java.util.UUID

@Service
@Transactional
class VisitService(
  private val visitRepository: VisitRepository,
  private val visitSchedulerClient: VisitSchedulerClient,
  private val applicationService: ApplicationService,
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

      visitRepository.deleteVisitVisitors(it)
      visitRepository.deleteVisitSupport(it)
      visitRepository.deleteVisitNotes(it)
      visitRepository.deleteVisitContact(it)
      visitRepository.deleteVisitLegacy(it)
      visitRepository.deleteVisit(it)

      visitRepository.deleteVisitNotificationEventsByBookingReference(bookingReference)

      val applicationReference = applicationService.getApplicationReferenceByVisitId(it)
      applicationService.deleteApplicationAndChildren(applicationReference)
    }
  }

  fun cancelVisitByBookingReference(bookingReference: String) {
    logger.debug("cancelVisitByReference called with reference - {}", bookingReference)

    visitSchedulerClient.cancelVisitByBookingReference(bookingReference)
  }
}
