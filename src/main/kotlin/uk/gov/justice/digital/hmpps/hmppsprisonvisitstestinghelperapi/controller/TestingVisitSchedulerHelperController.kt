package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.VisitSchedulerService

const val CANCEL_VISIT_URI: String = "$BASE_VISIT_URI/{reference}/cancel"

@RestController
class TestingVisitSchedulerHelperController {

  @Autowired
  lateinit var visitSchedulerService: VisitSchedulerService

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @PostMapping(
    CANCEL_VISIT_URI,
    produces = [MediaType.TEXT_PLAIN_VALUE],
  )
  @Operation(
    summary = "Cancel a visit via the visit-scheduler",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Visit has been cancelled",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Count not find visit for given reference",
      ),
    ],
  )
  fun cancelVisit(
    @Schema(description = "reference", example = "v9-d7-ed-7u", required = true)
    @PathVariable
    reference: String,
  ) {
    visitSchedulerService.cancelVisitByReference(reference)
  }
}
