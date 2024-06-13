package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank

data class PrisonerReleasedNotificationDto(
  @NotBlank
  val prisonerCode: String,
  @NotBlank
  val prisonCode: String,
  @NotBlank
  val reason: String,
)
