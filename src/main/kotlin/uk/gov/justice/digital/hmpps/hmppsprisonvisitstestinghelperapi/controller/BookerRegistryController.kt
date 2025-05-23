package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.BookerRegistryService

const val BASE_BOOKER_REGISTRY_URI: String = "/test/booker/{bookerReference}"
const val RESET_BOOKER_REGISTRY_BY_EMAIL_ADDRESS: String = "/test/booker/email/{emailAddress}"

@RestController
class BookerRegistryController {

  @Autowired
  lateinit var bookerRegistryService: BookerRegistryService

  @PreAuthorize("hasAnyRole('TEST_BOOKER_REGISTRY')")
  @DeleteMapping(
    RESET_BOOKER_REGISTRY_BY_EMAIL_ADDRESS,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Reset a booker details by email address",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Booker details reset",
      ),
    ],
  )
  fun resetBookerDetailsByEmailAddress(
    @Schema(description = "emailAddress", example = "test@example.com", required = true)
    @PathVariable
    emailAddress: String,
  ): ResponseEntity<HttpStatus> {
    bookerRegistryService.resetBookerDetailsByEmailAddress(emailAddress)
    return ResponseEntity(OK)
  }

  @PreAuthorize("hasAnyRole('TEST_BOOKER_REGISTRY')")
  @DeleteMapping(
    BASE_BOOKER_REGISTRY_URI,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Reset a booker details",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Booker details reset",
      ),
    ],
  )
  fun resetBookerDetailsByBookerReference(
    @Schema(description = "bookerReference", example = "bfop-zmmn-njay", required = true)
    @PathVariable
    bookerReference: String,
  ): ResponseEntity<HttpStatus> {
    bookerRegistryService.resetBookerDetailsByBookerReference(bookerReference)
    return ResponseEntity(OK)
  }
}
