package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class PrisonerRestrictionEventDto(
  @NotBlank
  val prisonerCode: String,

  @NotNull
  val startDate: LocalDate,

  val endDate: LocalDate?,
)
