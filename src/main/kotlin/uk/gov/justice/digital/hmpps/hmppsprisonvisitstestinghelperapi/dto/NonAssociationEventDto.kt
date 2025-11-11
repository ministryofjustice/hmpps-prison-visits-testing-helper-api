package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import jakarta.validation.constraints.NotBlank

data class NonAssociationEventDto(
  @field:NotBlank
  val prisonerCode: String,

  @field:NotBlank
  val nonAssociationPrisonerCode: String,
)
