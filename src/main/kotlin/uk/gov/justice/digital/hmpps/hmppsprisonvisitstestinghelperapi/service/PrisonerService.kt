package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.prison.api.VisitBalancesDto

@Service
@Transactional
class PrisonerService(
  private val prisonApiClient: PrisonApiClient,
) {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun getVisitBalancesForPrisoner(prisonerId: String): VisitBalancesDto {
    log.info("PrisonerService getVisitBalancesForPrisoner: Entered with parameter prisonerId: $prisonerId")
    return prisonApiClient.getPrisonerVisitBalances(prisonerId)
  }
}
