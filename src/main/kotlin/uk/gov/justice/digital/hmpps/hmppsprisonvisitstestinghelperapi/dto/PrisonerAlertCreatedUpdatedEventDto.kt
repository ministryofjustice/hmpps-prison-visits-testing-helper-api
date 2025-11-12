package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PrisonerAlertCreatedUpdatedEventDto(
  @field:NotBlank
  val prisonerCode: String,

  @field:NotNull
  val description: String,

  @field:NotNull
  val alertsAdded: List<String>,

  @field:NotNull
  val alertsRemoved: List<String>,
)
