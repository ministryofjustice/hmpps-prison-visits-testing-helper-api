package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CreateLocationGroupDto(
  @Schema(description = "Group name", example = "Main group", required = true)
  @field:NotBlank
  val name: String,

  @JsonProperty("prisonId")
  @Schema(description = "prisonId", example = "MDI", required = true)
  @field:NotBlank
  val prisonCode: String,

  @Schema(description = "list of locations for group", required = false)
  @field:Valid
  @field:NotEmpty
  val locations: List<PermittedSessionLocationDto> = listOf(),
)
