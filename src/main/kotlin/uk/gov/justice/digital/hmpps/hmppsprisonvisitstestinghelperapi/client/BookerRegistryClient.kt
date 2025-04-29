package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.booker.registry.BookerDto
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

  fun getBookerDetailsByEmailAddress(emailAddress: String): List<BookerDto>? {
    LOG.info("Calling to get booker details for booker email $emailAddress")

    return webClient.get()
      .uri("/public/booker/config/email/$emailAddress")
      .retrieve()
      .bodyToMono<List<BookerDto>>()
      .block(apiTimeout).also {
        LOG.info("Finished calling getBookerDetails for booker email $emailAddress")
      }
  }
}
