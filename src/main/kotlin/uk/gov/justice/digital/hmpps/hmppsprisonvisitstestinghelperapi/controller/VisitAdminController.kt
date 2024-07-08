package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.VisitSchedulerClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.SessionService
import java.time.LocalDate
import java.time.LocalDateTime

const val ADD_PRISON_EXCLUDE_DATE: String = "/test/prison/{prisonCode}/add/exclude-date/{excludeDate}"
const val REMOVE_PRISON_EXCLUDE_DATE: String = "/test/prison/{prisonCode}/remove/exclude-date/{excludeDate}"
const val ADD_SESSION_TEMPLATE_PATH: String = "/test/prison/{prisonCode}/add/template"
const val DELETE_SESSION_TEMPLATE_PATH: String = "/test/template/{reference}/delete"

@RestController
class VisitAdminController {
  @Autowired
  lateinit var visitSchedulerClient: VisitSchedulerClient

  @Autowired
  lateinit var sessionService: SessionService

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    ADD_PRISON_EXCLUDE_DATE,
    produces = [MediaType.TEXT_PLAIN_VALUE],
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

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    REMOVE_PRISON_EXCLUDE_DATE,
    produces = [MediaType.TEXT_PLAIN_VALUE],
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

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    ADD_SESSION_TEMPLATE_PATH,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @ResponseStatus(CREATED)
  @Operation(
    summary = "Add session template",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "Add session template",
      ),
    ],
  )
  fun addSessionTemplate(
    @Schema(description = "prison code", example = "MDI", required = true)
    @PathVariable
    prisonCode: String,
    @Schema(description = "sessionStartDateTime", example = "2007-12-03T10:15:30", required = false)
    @RequestParam(required = false)
    sessionStartDateTime: LocalDateTime = LocalDateTime.now().plusDays(2),
    @Schema(description = "weeklyFrequency", example = "1", required = false)
    @RequestParam(required = false)
    weeklyFrequency: Int = 1,
    @Schema(description = "closedCapacity", example = "1", required = false)
    @RequestParam(required = false)
    closedCapacity: Int = 1,
    @Schema(description = "openCapacity", example = "1", required = false)
    @RequestParam(required = false)
    openCapacity: Int = 1,
    @Schema(description = "Location level string", example = "A/1/3/007", required = false)
    @RequestParam(required = false)
    locationLevels: String?,
    @Schema(description = "incentive string", example = "ENHANCED", required = false)
    @RequestParam(required = false)
    incentive: String?,
    @Schema(description = "category string", example = "A_EXCEPTIONAL", required = false)
    @RequestParam(required = false)
    category: String?,
    @Schema(description = "disable all other sessions for slot and prison", example = "false", required = false)
    @RequestParam(required = false)
    disableAllOtherSessionsForSlotAndPrison: Boolean = false,
  ): ResponseEntity<String> {
    val startTime = sessionStartDateTime.toLocalTime()
    val endTime = startTime.plusHours(2)
    val slotDate = sessionStartDateTime.toLocalDate()
    val validToDate = slotDate.plusDays(((1 * weeklyFrequency) + 1).toLong())

    val result = sessionService.createSessionTemplate(
      sessionStartDateTime,
      endTime,
      slotDate,
      validToDate,
      prisonCode,
      closedCapacity,
      openCapacity,
      weeklyFrequency,
      locationLevels = locationLevels,
      category = category,
      incentive = incentive,
      disableAllOtherSessionsForSlotAndPrison = disableAllOtherSessionsForSlotAndPrison,
    )

    return ResponseEntity(result, CREATED)
  }

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    DELETE_SESSION_TEMPLATE_PATH,
  )
  @ResponseStatus(OK)
  @Operation(
    summary = "Delete session template, this will fail if visits are connected to session template",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Session template has been deleted",
      ),
    ],
  )
  fun deleteSessionTemplate(
    @Schema(description = "session template reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
    @RequestParam(required = false)
    enableAllOtherSessionsForSlotAndPrison: Boolean = false,
  ): ResponseEntity<HttpStatus> {
    sessionService.deleteSessionTemplate(reference, enableAllOtherSessionsForSlotAndPrison)
    return ResponseEntity(OK)
  }
}
