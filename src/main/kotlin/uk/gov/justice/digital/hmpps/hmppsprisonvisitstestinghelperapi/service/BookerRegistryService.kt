package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.BookerRegistryClient
import java.util.*

@Service
@Transactional
class BookerRegistryService(private val bookerRegistryClient: BookerRegistryClient) {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun resetBookerDetails(bookerReference: String) {
    logger.debug("Enter resetBookerDetails for booker $bookerReference")
    bookerRegistryClient.resetBookerDetails(bookerReference)
  }
}
