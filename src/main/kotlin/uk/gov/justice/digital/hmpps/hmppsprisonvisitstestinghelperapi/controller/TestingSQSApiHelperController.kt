package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
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
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerAlertCreatedUpdatedEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerReceivedEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonerRestrictionEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitorRestrictionEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.EventHandlerService

const val SQS_BASE_PRISONER_URL: String = "/test/prisoner"
const val SQS_PRISONER_RELEASED: String = "$SQS_BASE_PRISONER_URL/released"
const val SQS_PRISONER_RECEIVED: String = "$SQS_BASE_PRISONER_URL/received"
const val SQS_PRISONER_NON_ASSOCIATION: String = "$SQS_BASE_PRISONER_URL/non-association"
const val SQS_PRISONER_RESTRICTION: String = "$SQS_BASE_PRISONER_URL/restriction"
const val SQS_PRISONER_ALERT_UPDATED: String = "$SQS_BASE_PRISONER_URL/alerts/updated"

const val SQS_BASE_VISITOR_URL: String = "/test/visitor"
const val SQS_VISITOR_RESTRICTION: String = "$SQS_BASE_VISITOR_URL/restriction"

@RestController
class TestingSQSApiHelperController(
  private val eventHandlerService: EventHandlerService,
) {
  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_PRISONER_RELEASED,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of prisoner released",
    requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(implementation = PrisonerEventDto::class),
        ),
      ],
    ),
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

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_PRISONER_RECEIVED,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of prisoner received",
    requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(implementation = PrisonerReceivedEventDto::class),
        ),
      ],
    ),
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "SQS process started",
      ),
    ],
  )
  fun sqsReceived(
    @Schema(description = "Prisoner received Event Dto", required = true)
    @RequestBody
    prisonEventDto: PrisonerReceivedEventDto,
  ): ResponseEntity<HttpStatus> {
    eventHandlerService.handlePrisonerReceivedEvent(prisonEventDto)
    return ResponseEntity(HttpStatus.CREATED)
  }

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_PRISONER_NON_ASSOCIATION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of non association",
    requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(implementation = NonAssociationEventDto::class),
        ),
      ],
    ),
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

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_VISITOR_RESTRICTION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of visitor restriction",
    requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(implementation = VisitorRestrictionEventDto::class),
        ),
      ],
    ),
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

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_PRISONER_RESTRICTION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of prisoner restriction",
    requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(implementation = PrisonerRestrictionEventDto::class),
        ),
      ],
    ),
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

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    SQS_PRISONER_ALERT_UPDATED,
    produces = [MediaType.TEXT_PLAIN_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Kick's off the SQS process of prisoner alert created / updated",
    requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(implementation = PrisonerAlertCreatedUpdatedEventDto::class),
        ),
      ],
    ),
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "SQS process started",
      ),
    ],
  )
  fun sqsPrisonerAlertCreatedUpdated(
    @Schema(description = "Prisoner Alert Created Updated Dto", required = true)
    @RequestBody
    prisonerAlertCreatedUpdatedEventDto: PrisonerAlertCreatedUpdatedEventDto,
  ): ResponseEntity<HttpStatus> {
    eventHandlerService.handlePrisonerAlertCreatedUpdatedEvent(prisonerAlertCreatedUpdatedEventDto)
    return ResponseEntity(HttpStatus.CREATED)
  }
}
