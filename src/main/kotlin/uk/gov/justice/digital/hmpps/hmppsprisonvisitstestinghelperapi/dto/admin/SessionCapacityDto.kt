package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

@Schema(description = "Session Capacity")
data class SessionCapacityDto(
  @Schema(description = "closed capacity", example = "10", required = true)
  @field:Min(0)
  val closed: Int,
  @Schema(description = "open capacity", example = "50", required = true)
  @field:Min(0)
  val open: Int,
) {
  operator fun plus(sessionCapacityDto: SessionCapacityDto): SessionCapacityDto {
    return SessionCapacityDto(closed = this.closed + sessionCapacityDto.closed, open = this.open + sessionCapacityDto.open)
  }
}
