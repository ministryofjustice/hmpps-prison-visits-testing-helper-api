package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Component
class BookerRegistryClient(
  @Qualifier("bookerRegistryWebClient") private val webClient: WebClient,
  @Value("\${visit-scheduler.api.timeout:10s}") val apiTimeout: Duration,
) {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun resetBookerDetails(bookerReference: String) {
    LOG.info("Calling to clear booker details for booker $bookerReference")

    webClient.delete()
      .uri("/public/booker/config/$bookerReference")
      .retrieve()
      .toBodilessEntity()
      .doOnError { e -> LOG.error("Could not reset booker details :", e) }
      .block(apiTimeout)
    LOG.info("Finished calling resetBookerDetails for booker $bookerReference")
  }
}
