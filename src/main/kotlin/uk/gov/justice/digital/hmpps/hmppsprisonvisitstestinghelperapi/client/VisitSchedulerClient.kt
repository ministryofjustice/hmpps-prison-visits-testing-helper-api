package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonExcludeDateDto
import java.time.Duration
import java.time.LocalDate

@Component
class VisitSchedulerClient(
  @Qualifier("visitSchedulerWebClient") private val webClient: WebClient,
  @Value("\${visit-scheduler.api.timeout:10s}") val apiTimeout: Duration,
) {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun addExcludeDate(prisonCode: String, excludeDate: LocalDate) {
    LOG.info("Calling add exclude date for prison - $prisonCode, excluded date - $excludeDate")
    webClient.put()
      .uri("/admin/prisons/prison/$prisonCode/exclude-date/add")
      .body(BodyInserters.fromValue(PrisonExcludeDateDto(excludeDate)))
      .retrieve()
      .toBodilessEntity()
      .block(apiTimeout)
    LOG.info("Finished calling addExcludeDate for prison - $prisonCode, excluded date - $excludeDate")
  }

  fun removeExcludeDate(prisonCode: String, excludeDate: LocalDate) {
    LOG.info("Calling remove exclude date for prison - $prisonCode, excluded date - $excludeDate")

    webClient.put()
      .uri("/admin/prisons/prison/$prisonCode/exclude-date/remove")
      .body(BodyInserters.fromValue(PrisonExcludeDateDto(excludeDate)))
      .retrieve()
      .toBodilessEntity()
      .block(apiTimeout)

    LOG.info("Finished calling remove exclude date for prison - $prisonCode, excluded date - $excludeDate")
  }
}
