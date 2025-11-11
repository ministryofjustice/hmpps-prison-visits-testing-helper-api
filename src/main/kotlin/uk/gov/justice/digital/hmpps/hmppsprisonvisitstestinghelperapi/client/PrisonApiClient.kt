package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.prison.api.VisitBalancesDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.exception.NotFoundException
import java.time.Duration

@Component
class PrisonApiClient(
  @param:Qualifier("prisonApiWebClient") private val webClient: WebClient,
  @param:Value("\${prison.api.timeout:10s}") val apiTimeout: Duration,
) {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getPrisonerVisitBalances(prisonerId: String): VisitBalancesDto {
    LOG.info("Calling get visit balances for prisoner - $prisonerId")
    return webClient.get()
      .uri("/api/bookings/offenderNo/$prisonerId/visit/balances")
      .retrieve()
      .bodyToMono<VisitBalancesDto>()
      .onErrorResume { e ->
        if (!isNotFoundError(e)) {
          LOG.error("get visit balances for prisoner Failed for get request")
          Mono.error(e)
        } else {
          LOG.error("get visit balances for prisoner returned NOT_FOUND for get request")
          Mono.error(NotFoundException("No prisoner with Id found - $prisonerId, on prison-api") as Throwable)
        }
      }
      .blockOptional(apiTimeout).orElseThrow {
        NotFoundException("No prisoner with Id found - $prisonerId, on prison-api")
      }
      .also {
        LOG.info("Finished calling get visit balances for prisoner - $prisonerId")
      }
  }

  fun isNotFoundError(e: Throwable?) = e is WebClientResponseException && e.statusCode == HttpStatus.NOT_FOUND
}
