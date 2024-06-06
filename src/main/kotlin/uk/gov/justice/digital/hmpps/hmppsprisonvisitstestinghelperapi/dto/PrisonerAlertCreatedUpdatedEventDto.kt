package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PrisonerAlertCreatedUpdatedEventDto(
  @NotBlank
  val prisonerCode: String,

  @NotNull
  val description: String,

  @NotNull
  val alertsAdded: List<String>,
)
