package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.VisitRepository

@Service
@Transactional
class DBService(
  private val visitRepository: VisitRepository,
) {

  private val logger: Logger = LoggerFactory.getLogger(VisitRepository::class.java)

  fun setVisitStatus(reference: String, status: VisitStatus): Boolean {
    logger.debug("Enter setVisitStatus $reference $status")
    val result = visitRepository.setVisitStatus(reference, status.name)
    logger.debug("setVisitStatus result: $result")
    return result > 0
  }

  fun isVisitBooked(reference: String): Boolean {
    logger.debug("Enter setVisitStatus $reference $reference")
    val result = visitRepository.isVisitBooked(reference)
    logger.debug("isVisitBooked result: $result")
    return result
  }

  fun deleteVisitNotificationEvents(bookingReference: String): Int {
    logger.debug("Delete visit notification events for booking reference - $bookingReference")
    val result = visitRepository.deleteVisitNotificationEvents(bookingReference)
    logger.debug("Deleted $result visit notification events for booking reference -  $bookingReference")
    return result
  }
}
