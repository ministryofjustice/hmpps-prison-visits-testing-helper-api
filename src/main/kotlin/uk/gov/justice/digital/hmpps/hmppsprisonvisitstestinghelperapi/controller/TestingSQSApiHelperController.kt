package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.VisitRepository
import java.time.LocalDate

const val SQS_RELEASED: String = "/test/prison/{prisonCode}/prisoner/{prisonerCode}/released"
const val SQS_RECEIVED: String = "/test/prison/{prisonCode}/prisoner/{prisonerCode}/received"
const val SQS_NON_ASSOCIATION: String = "/test/prisoner/{prisonerCode}/non-association/{nonAssociationPrisonerCode}"
const val SQS_VISITOR_RESTRICTION: String = "/test/visitor/{visitorId}/restriction/start/{startDate}/end/{endDate}"
const val SQS_PRISONER_RESTRICTION: String = "/test/prisoner/{prisonerCode}/restriction/start/{startDate}/end/{endDate}"

@RestController
class TestingSQSApiHelperController {

  @Autowired
  lateinit var visitRepository: VisitRepository

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @GetMapping(
    SQS_RELEASED,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of prisoner released",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "SQS process started",
      ),
    ],
  )
  fun sqsReleased(
    @Schema(description = "prisonCode", example = "BRI", required = true)
    @PathVariable
    prisonCode: String,
    @Schema(description = "prisonerCode", example = "G8006UQ", required = true)
    @PathVariable
    prisonerCode: String,
  ): ResponseEntity<HttpStatus> {
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @GetMapping(
    SQS_RECEIVED,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of prisoner received",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "SQS process started",
      ),
    ],
  )
  fun sqsReceived(
    @Schema(description = "prisonCode", example = "BRI", required = true)
    @PathVariable
    prisonCode: String,
    @Schema(description = "prisonerCode", example = "G8006UQ", required = true)
    @PathVariable
    prisonerCode: String,
  ): ResponseEntity<HttpStatus> {
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @GetMapping(
    SQS_NON_ASSOCIATION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of non association",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "SQS process started",
      ),
    ],
  )
  fun sqsNonAssociation(
    @Schema(description = "prisonerCode", example = "G8006UQ", required = true)
    @PathVariable
    prisonerCode: String,
    @Schema(description = "nonAssociationPrisonerCode", example = "G8006UQ", required = true)
    @PathVariable
    nonAssociationPrisonerCode: String,
  ): ResponseEntity<HttpStatus> {
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @GetMapping(
    SQS_VISITOR_RESTRICTION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of visitor restriction",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "SQS process started",
      ),
    ],
  )
  fun sqsVisitorRestriction(
    @Schema(description = "visitorId", example = "1232442", required = true)
    @PathVariable
    visitorId: String,
    @PathVariable(value = "startDate", required = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Parameter(
      description = "start date",
      example = "2021-11-03",
    )
    startDate: LocalDate,
    @PathVariable(value = "endDate", required = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Parameter(
      description = "start date",
      example = "2021-12-03",
    )
    endDate: LocalDate,
  ): ResponseEntity<HttpStatus> {
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @GetMapping(
    SQS_PRISONER_RESTRICTION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of prisoner restriction",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "SQS process started",
      ),
    ],
  )
  fun sqsPrisonerRestriction(
    @Schema(description = "prisonerCode", example = "G8006UQ", required = true)
    @PathVariable
    prisonerCode: String,
    @PathVariable(value = "startDate", required = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Parameter(
      description = "start date",
      example = "2021-11-03",
    )
    startDate: LocalDate,
    @PathVariable(value = "endDate", required = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Parameter(
      description = "start date",
      example = "2021-12-03",
    )
    endDate: LocalDate,
  ): ResponseEntity<HttpStatus> {
    return ResponseEntity(HttpStatus.CREATED)
  }
}
