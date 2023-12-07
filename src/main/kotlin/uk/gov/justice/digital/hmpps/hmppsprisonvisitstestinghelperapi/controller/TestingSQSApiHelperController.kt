package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.NonAssociationEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerRestrictionEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitorRestrictionEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.VisitRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.EventHandlerService

const val SQS_RELEASED: String = "/test/prisoner/released"
const val SQS_RECEIVED: String = "/test/prisoner/received"
const val SQS_NON_ASSOCIATION: String = "/test/prisoner/non-association"
const val SQS_VISITOR_RESTRICTION: String = "/test/visitor/restriction"
const val SQS_PRISONER_RESTRICTION: String = "/test/prisoner/restriction"

@RestController
class TestingSQSApiHelperController(
  private val visitRepository: VisitRepository,
  private val eventHandlerService: EventHandlerService,
) {
  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_RELEASED,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
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
    @Schema(description = "Prisoner Event Dto - prison code and prisoner code", required = true)
    @RequestBody
    prisonEventDto: PrisonerEventDto,
  ): ResponseEntity<HttpStatus> {
    eventHandlerService.handlePrisonerReleaseEvent(prisonEventDto)
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_RECEIVED,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
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
    @Schema(description = "Prisoner Event Dto - prison code and prisoner code", required = true)
    @RequestBody
    prisonEventDto: PrisonerEventDto,
  ): ResponseEntity<HttpStatus> {
    eventHandlerService.handlePrisonerReceivedEvent(prisonEventDto)
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_NON_ASSOCIATION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
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
    @Schema(description = "Non Association Event Dto - with both prisoner numbers", required = true)
    @RequestBody
    @Valid
    nonAssociationEventDto: NonAssociationEventDto,
  ): ResponseEntity<HttpStatus> {
    eventHandlerService.handleNonAssociationEvent(nonAssociationEventDto)
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_VISITOR_RESTRICTION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
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
    @Schema(description = "Prisoner Restriction Dto", required = true)
    @RequestBody
    visitorRestrictionEventDto: VisitorRestrictionEventDto,

  ): ResponseEntity<HttpStatus> {
    eventHandlerService.handleVisitorRestrictionChangeEvent(visitorRestrictionEventDto)
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('VISIT_SCHEDULER','VISIT_SCHEDULER_CONFIG','TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_PRISONER_RESTRICTION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
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
    @Schema(description = "Prisoner Restriction Dto", required = true)
    @RequestBody
    prisonerRestrictionEventDto: PrisonerRestrictionEventDto,
  ): ResponseEntity<HttpStatus> {
    eventHandlerService.handlePrisonerRestrictionChangeEvent(prisonerRestrictionEventDto)
    return ResponseEntity(HttpStatus.CREATED)
  }
}
