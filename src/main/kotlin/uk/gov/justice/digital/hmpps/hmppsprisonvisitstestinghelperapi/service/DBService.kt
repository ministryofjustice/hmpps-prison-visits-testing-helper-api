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

  private val LOG: Logger = LoggerFactory.getLogger(VisitRepository::class.java)

  fun setVisitStatus(reference: String, status: VisitStatus): Boolean {
    LOG.debug("Enter setVisitStatus $reference $status")
    val result = visitRepository.setVisitStatus(reference, status.name)
    LOG.debug("setVisitStatus result: $result")
    return result > 0
  }

  fun isVisitBooked(reference: String): Boolean {
    LOG.debug("Enter setVisitStatus $reference $reference")
    val result = visitRepository.isVisitBooked(reference)
    LOG.debug("isVisitBooked result: $result")
    return result
  }
}
