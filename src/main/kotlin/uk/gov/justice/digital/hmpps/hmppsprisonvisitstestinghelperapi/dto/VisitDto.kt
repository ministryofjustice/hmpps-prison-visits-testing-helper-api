package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Visit")
data class VisitDto(
  @Schema(description = "Visit Reference", example = "v9-d7-ed-7u", required = true)
  val reference: String,
)
