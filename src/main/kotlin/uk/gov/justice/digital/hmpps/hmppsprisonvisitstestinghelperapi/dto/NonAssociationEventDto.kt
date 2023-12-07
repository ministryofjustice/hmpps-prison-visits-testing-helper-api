package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank

data class NonAssociationEventDto(
  @NotBlank
  val prisonerCode: String,

  @NotBlank
  val nonAssociationPrisonerCode: String,
)
