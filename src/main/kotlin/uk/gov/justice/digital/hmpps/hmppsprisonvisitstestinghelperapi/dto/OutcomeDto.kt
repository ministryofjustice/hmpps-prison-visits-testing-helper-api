package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "Visit Outcome")
class OutcomeDto(
  @Schema(description = "Outcome Status", example = "CANCELLED", required = true)
  @field:NotNull
  val outcomeStatus: String,
  @Schema(description = "Outcome text", example = "Because he got covid", required = false)
  val text: String? = null,
)
