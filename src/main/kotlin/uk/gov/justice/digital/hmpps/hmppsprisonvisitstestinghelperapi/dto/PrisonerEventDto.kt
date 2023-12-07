package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank

data class PrisonerEventDto(
  @NotBlank
  val prisonCode: String,

  @NotBlank
  val prisonerCode: String,
)
