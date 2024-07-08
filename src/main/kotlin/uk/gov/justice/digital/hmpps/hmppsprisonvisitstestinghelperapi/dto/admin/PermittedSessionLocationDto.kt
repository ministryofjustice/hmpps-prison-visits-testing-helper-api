package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class PermittedSessionLocationDto(

  @Schema(description = "Level one location code", example = "w", required = true)
  @NotNull
  var levelOneCode: String,

  @Schema(description = "Level two location code", example = "c", required = false)
  var levelTwoCode: String? = null,

  @Schema(description = "Level three location code", example = "1", required = false)
  var levelThreeCode: String? = null,

  @Schema(description = "Level four location code", example = "001", required = false)
  var levelFourCode: String? = null,
)
