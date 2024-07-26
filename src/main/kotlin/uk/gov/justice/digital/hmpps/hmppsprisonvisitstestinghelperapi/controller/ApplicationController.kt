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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.visit.scheduler.CreateApplicationDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.ApplicationService
import java.time.LocalDateTime

const val BASE_APPLICATION_URI: String = "/test/application/{reference}"
const val CREATE_APPLICATION: String = "/test/application/create"
const val UPDATE_MODIFIED_DATE_URI: String = "$BASE_APPLICATION_URI/modifiedTimestamp/{modifiedTimestamp}"
const val CHANGE_OPEN_SESSION_SLOT_CAPACITY_FOR_APPLICATION: String = "$BASE_APPLICATION_URI/session/capacity/open/{capacity}"
const val CHANGE_CLOSED_SESSION_SLOT_CAPACITY_FOR_APPLICATION: String = "$BASE_APPLICATION_URI/session/capacity/closed/{capacity}"
const val GET_OPEN_SESSION_SLOT_CAPACITY_FOR_APPLICATION: String = "$BASE_APPLICATION_URI/session/open/capacity"
const val GET_CLOSED_SESSION_SLOT_CAPACITY_FOR_APPLICATION: String = "$BASE_APPLICATION_URI/session/closed/capacity"

@RestController
class ApplicationController {

  @Autowired
  lateinit var applicationService: ApplicationService

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @DeleteMapping(
    BASE_APPLICATION_URI,
  )
  @ResponseStatus(OK)
  @Operation(
    summary = "Delete application and children by reference",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Status changed process started",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find visit for given reference",
      ),
    ],
  )
  fun deleteApplicationAndAllChildren(
    @Schema(description = "application reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
  ) {
    applicationService.deleteApplicationAndChildren(reference)
  }

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    UPDATE_MODIFIED_DATE_URI,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Change modified date of an application",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Modified date changed",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find application for given reference",
      ),
    ],
  )
  fun updateApplicationModifiedDate(
    @Schema(description = "reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
    @Schema(description = "Updated modified timestamp", example = "2007-12-28T10:15:30", required = true)
    @PathVariable
    modifiedTimestamp: LocalDateTime,
  ): ResponseEntity<HttpStatus> {
    return if (applicationService.updateApplicationModifyTimestamp(reference, modifiedTimestamp)) {
      ResponseEntity(OK)
    } else {
      ResponseEntity(NOT_FOUND)
    }
  }

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    CHANGE_OPEN_SESSION_SLOT_CAPACITY_FOR_APPLICATION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Change open session slot capacity for application",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Updated as expected",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find application",
      ),
    ],
  )
  fun changeOpenSessionSlotCapacityForApplication(
    @Schema(description = "reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
    @Schema(description = "capacity", example = "1", required = true)
    @PathVariable
    capacity: Int,
  ): ResponseEntity<HttpStatus> {
    return if (applicationService.changeOpenSessionSlotCapacityForApplication(reference, capacity)) {
      ResponseEntity(OK)
    } else {
      ResponseEntity(NOT_FOUND)
    }
  }

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    CHANGE_CLOSED_SESSION_SLOT_CAPACITY_FOR_APPLICATION,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Change closed session slot capacity for application",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Updated as expected",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find application",
      ),
    ],
  )
  fun changeClosedSessionSlotCapacityForApplication(
    @Schema(description = "reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
    @Schema(description = "capacity", example = "1", required = true)
    @PathVariable
    capacity: Int,
  ): ResponseEntity<HttpStatus> {
    return if (applicationService.changeClosedSessionSlotCapacityForApplication(reference, capacity)) {
      ResponseEntity(OK)
    } else {
      ResponseEntity(NOT_FOUND)
    }
  }

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @GetMapping(
    GET_OPEN_SESSION_SLOT_CAPACITY_FOR_APPLICATION,
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Get open session slot capacity for application",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Return open capacity",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find application",
      ),
    ],
  )
  fun getOpenSessionSlotCapacityForApplication(
    @Schema(description = "reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
  ): ResponseEntity<Int> {
    return ResponseEntity.status(OK).body(applicationService.getOpenSessionSlotCapacityForApplication(reference))
  }

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @GetMapping(
    GET_CLOSED_SESSION_SLOT_CAPACITY_FOR_APPLICATION,
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  @Operation(
    summary = "Get closed session slot capacity for application",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Return closed capacity",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find application",
      ),
    ],
  )
  fun getClosedSessionSlotCapacityForApplication(
    @Schema(description = "reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
  ): ResponseEntity<Int> {
    return ResponseEntity.status(OK).body(applicationService.getClosedSessionSlotCapacityForApplication(reference))
  }

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PutMapping(
    CREATE_APPLICATION,
  )
  @ResponseStatus(OK)
  @Operation(
    summary = "Application successfully created",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Status changed process started",
      ),
    ],
  )
  fun createApplication(
    @RequestBody
    createApplicationDto: CreateApplicationDto,
  ): ResponseEntity<String> {
    val applicationReference = applicationService.createApplication(createApplicationDto)
    return ResponseEntity.status(OK).body(applicationReference)
  }
}
