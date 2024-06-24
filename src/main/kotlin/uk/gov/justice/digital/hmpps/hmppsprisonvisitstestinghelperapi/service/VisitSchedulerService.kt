package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.VisitSchedulerClient

@Service
class VisitSchedulerService(
  private val visitSchedulerClient: VisitSchedulerClient,
) {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun cancelVisitByReference(reference: String) {
    logger.debug("cancelVisitByReference called with reference - {}", reference)

    visitSchedulerClient.cancelVisitByReference(reference)
  }
}
