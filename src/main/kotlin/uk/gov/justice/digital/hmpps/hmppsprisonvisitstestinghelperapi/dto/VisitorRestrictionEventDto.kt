package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class VisitorRestrictionEventDto(
  @NotBlank
  val visitorId: String,

  @NotNull
  val startDate: LocalDate,

  val endDate: LocalDate?,
)
