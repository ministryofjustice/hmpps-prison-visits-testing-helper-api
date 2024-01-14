package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.DBService

const val CHANGE_STATUS_URI: String = "/test/visit/{reference}/status/{status}"
const val DELETE_VISIT_NOTIFICATIONS_URI: String = "/test/visit/{reference}/notifications"

@RestController
class TestingDBApiHelperController {

  @Autowired
  lateinit var dBService: DBService

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @PutMapping(
    CHANGE_STATUS_URI,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Changes status of a visit",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Status changed process started",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Count not find visit for given reference",
      ),
    ],
  )
  fun changeVisitStatus(
    @Schema(description = "reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
    @Schema(description = "status", example = "BOOKED", required = true)
    @PathVariable
    status: VisitStatus,
  ): ResponseEntity<HttpStatus> {
    return if (dBService.setVisitStatus(reference, status)) {
      ResponseEntity(OK)
    } else {
      ResponseEntity(NOT_FOUND)
    }
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @DeleteMapping(
    DELETE_VISIT_NOTIFICATIONS_URI,
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Delete visit notification events for a visit",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Visit notification event visits deleted for visit reference",
      ),
    ],
  )
  fun deleteVisitNotificationEvents(
    @Schema(description = "visit booking reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
  ): ResponseEntity<Int> {
    val recordsDeleted = dBService.deleteVisitNotificationEvents(reference)
    return ResponseEntity(recordsDeleted, OK)
  }
}
