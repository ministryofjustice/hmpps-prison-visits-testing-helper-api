package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank

data class PrisonerEventDto(
  @field:NotBlank
  val prisonCode: String,

  @field:NotBlank
  val prisonerCode: String,
)
