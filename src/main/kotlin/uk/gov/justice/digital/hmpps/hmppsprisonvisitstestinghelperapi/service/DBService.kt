package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.DBNotificationEventType
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.VisitRepository
import java.util.UUID

@Service
@Transactional
class DBService(
  private val visitRepository: VisitRepository,
) {

  private val logger: Logger = LoggerFactory.getLogger(VisitRepository::class.java)

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

  fun deleteVisitNotificationEvents(bookingReference: String): Int {
    logger.debug("Delete visit notification events for booking reference - {}", bookingReference)
    val result = visitRepository.deleteVisitNotificationEvents(bookingReference)
    logger.debug("Deleted {} visit notification events for booking reference - {}", result, bookingReference)
    return result
  }

  fun createVisitNotificationEvents(bookingReference: String, notificationType: DBNotificationEventType) {
    logger.debug("Create visit notification event {} for booking reference - {}", notificationType, bookingReference)
    val reference = UUID.randomUUID().toString()
    visitRepository.createVisitNotificationEvents(bookingReference, notificationType.toString(), reference)
    logger.debug("Created visit notification event {} for booking reference - {}", notificationType, bookingReference)
  }
}
