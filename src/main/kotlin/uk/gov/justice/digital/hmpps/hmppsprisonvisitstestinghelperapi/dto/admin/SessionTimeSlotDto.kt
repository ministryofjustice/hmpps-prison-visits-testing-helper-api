package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime

data class SessionTimeSlotDto(
  @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
  @Schema(description = "The start time of the generated visit session(s)", example = "10:30", required = true)
  val startTime: LocalTime,

  @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
  @Schema(description = "The end time of the generated visit session(s)", example = "11:30", required = true)
  val endTime: LocalTime,
)
