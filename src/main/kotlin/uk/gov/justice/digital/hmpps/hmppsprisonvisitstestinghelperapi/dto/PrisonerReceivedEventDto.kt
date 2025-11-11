package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank

data class PrisonerReceivedEventDto(
  @field:NotBlank
  val prisonerCode: String,
  @field:NotBlank
  val prisonCode: String,
  @field:NotBlank
  val reason: String,
)
