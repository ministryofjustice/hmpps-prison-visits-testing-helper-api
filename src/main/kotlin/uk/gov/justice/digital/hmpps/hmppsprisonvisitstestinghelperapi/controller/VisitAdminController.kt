package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.VisitSchedulerClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateSessionTemplateRequestDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.SessionService
import java.time.LocalDate

const val ADD_PRISON_EXCLUDE_DATE: String = "/test/prison/{prisonCode}/add/exclude-date/{excludeDate}"
const val REMOVE_PRISON_EXCLUDE_DATE: String = "/test/prison/{prisonCode}/remove/exclude-date/{excludeDate}"
const val ADD_SESSION_TEMPLATE_PATH: String = "/test/prison/{prisonCode}/template/add"
const val DELETE_SESSION_TEMPLATE_PATH: String = "/test/template/{reference}/delete"

@RestController
class VisitAdminController {

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

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
    @RequestBody
    @Valid
    createSessionTemplateRequestDto: CreateSessionTemplateRequestDto,
  ): ResponseEntity<String> {
    val startTime = createSessionTemplateRequestDto.sessionStartDateTime.toLocalTime()
    val endTime = startTime.plusHours(2)
    val slotDate = createSessionTemplateRequestDto.sessionStartDateTime.toLocalDate()
    val validToDate = slotDate.plusDays(((1 * createSessionTemplateRequestDto.weeklyFrequency) + 1).toLong())

    logger.debug("createSessionTemplateRequestDto received - {}", createSessionTemplateRequestDto)

    val result = sessionService.createSessionTemplate(
      createSessionTemplateRequestDto.sessionStartDateTime,
      endTime,
      slotDate,
      validToDate,
      createSessionTemplateRequestDto.prisonCode,
      createSessionTemplateRequestDto.closedCapacity,
      createSessionTemplateRequestDto.openCapacity,
      createSessionTemplateRequestDto.weeklyFrequency,
      locationLevels = createSessionTemplateRequestDto.locationLevels,
      category = createSessionTemplateRequestDto.category,
      incentive = createSessionTemplateRequestDto.incentive,
      disableAllOtherSessionsForSlotAndPrison = createSessionTemplateRequestDto.disableAllOtherSessionsForSlotAndPrison,
      customSessionName = createSessionTemplateRequestDto.sessionName,
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
    @PathVariable(name = "reference")
    sessionTemplateReference: String,
    @RequestParam(required = false)
    enableAllOtherSessionsForSlotAndPrison: Boolean = false,
  ): ResponseEntity<HttpStatus> {
    // De active session template to allow it to be deleted
    sessionService.deActivateSessionTemplate(sessionTemplateReference)
    sessionService.deleteSessionTemplate(sessionTemplateReference, enableAllOtherSessionsForSlotAndPrison)
    return ResponseEntity(OK)
  }
}
