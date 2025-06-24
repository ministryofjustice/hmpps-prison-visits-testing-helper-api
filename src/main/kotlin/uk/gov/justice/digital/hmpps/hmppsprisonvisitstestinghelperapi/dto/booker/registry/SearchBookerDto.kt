package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.booker.registry

import jakarta.validation.constraints.NotBlank

data class SearchBookerDto(
  @field:NotBlank
  val email: String,
)
