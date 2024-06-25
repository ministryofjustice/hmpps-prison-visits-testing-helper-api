package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.OK
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.prison.api.VisitBalancesDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.PrisonerService

const val BASE_PRISONER_URI: String = "/test/prisoner/{prisonerId}"
const val GET_PRISONER_VO_BALANCES: String = "$BASE_PRISONER_URI/visit/balances"

@RestController
class PrisonerController {

  @Autowired
  lateinit var prisonerService: PrisonerService

  @PreAuthorize("hasAnyRole('TEST_VISIT_SCHEDULER')")
  @GetMapping(
    GET_PRISONER_VO_BALANCES,
  )
  @ResponseStatus(OK)
  @Operation(
    summary = "Get visit balances for prisoner",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Successfully return visit balances",
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find prisoner",
      ),
    ],
  )
  fun getVisitBalancesForPrisoner(
    @Schema(description = "Prisoner Id", example = "AA123456", required = true)
    @PathVariable
    prisonerId: String,
  ): VisitBalancesDto {
    return prisonerService.getVisitBalancesForPrisoner(prisonerId)
  }
}
