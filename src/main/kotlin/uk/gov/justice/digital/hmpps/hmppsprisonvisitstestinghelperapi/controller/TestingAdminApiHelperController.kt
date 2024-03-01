package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.VisitSchedulerClient
import java.time.LocalDate

const val ADD_PRISON_EXCLUDE_DATE: String = "/test/prison/{prisonCode}/add/exclude-date/{excludeDate}"
const val REMOVE_PRISON_EXCLUDE_DATE: String = "/test/prison/{prisonCode}/remove/exclude-date/{excludeDate}"

@RestController
class TestingAdminApiHelperController {
  @Autowired
  lateinit var visitSchedulerClient: VisitSchedulerClient

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @PutMapping(
    ADD_PRISON_EXCLUDE_DATE,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
  )
  @ResponseStatus(CREATED)
  @Operation(
    summary = "Create visit notification events for a visit - fallback for events that are not SQS based",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "Visit notification event visits added for visit reference",
      ),
    ],
  )
  fun addPrisonExcludeDate(
    @Schema(description = "prison code", example = "MDI", required = true)
    @PathVariable
    prisonCode: String,
    @Schema(description = "excludeDate", example = "2024-01-31", required = true)
    @PathVariable
    excludeDate: LocalDate,
  ): ResponseEntity<HttpStatus> {
    visitSchedulerClient.addExcludeDate(prisonCode, excludeDate)
    return ResponseEntity(CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @PutMapping(
    REMOVE_PRISON_EXCLUDE_DATE,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
  )
  @ResponseStatus(CREATED)
  @Operation(
    summary = "Create visit notification events for a visit - fallback for events that are not SQS based",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "Visit notification event visits added for visit reference",
      ),
    ],
  )
  fun removePrisonExcludeDate(
    @Schema(description = "prison code", example = "MDI", required = true)
    @PathVariable
    prisonCode: String,
    @Schema(description = "excludeDate", example = "2024-01-31", required = true)
    @PathVariable
    excludeDate: LocalDate,
  ): ResponseEntity<HttpStatus> {
    visitSchedulerClient.removeExcludeDate(prisonCode, excludeDate)
    return ResponseEntity(CREATED)
  }
}
