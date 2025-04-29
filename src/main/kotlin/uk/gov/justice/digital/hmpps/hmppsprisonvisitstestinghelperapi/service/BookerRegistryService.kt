package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.BookerRegistryClient

@Service
class BookerRegistryService(private val bookerRegistryClient: BookerRegistryClient) {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun resetBookerDetailsByEmailAddress(emailAddress: String) {
    logger.debug("getBookerDetails for email address $emailAddress")
    val bookerReferences = bookerRegistryClient.getBookerDetailsByEmailAddress(emailAddress)?.map { it.reference }
    if (!bookerReferences.isNullOrEmpty()) {
      for (bookerReference in bookerReferences) {
        bookerRegistryClient.resetBookerDetails(bookerReference)
      }
    } else {
      logger.warn("Could not retrieve booker details for email address $emailAddress")
    }
  }

  fun resetBookerDetailsByBookerReference(bookerReference: String) {
    logger.debug("Enter resetBookerDetails for booker $bookerReference")
    bookerRegistryClient.resetBookerDetails(bookerReference)
  }
}
