package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class CreateSessionTemplateRequestDto(
  @param:Schema(description = "prison code", example = "MDI", required = true)
  val prisonCode: String,
  @param:Schema(description = "sessionStartDateTime", example = "2007-12-03T10:15:30", required = false)
  val sessionStartDateTime: LocalDateTime = LocalDateTime.now().plusDays(2),
  @param:Schema(description = "weeklyFrequency", example = "1", required = false)
  val weeklyFrequency: Int = 1,
  @param:Schema(description = "closedCapacity", example = "1", required = false)
  val closedCapacity: Int = 1,
  @param:Schema(description = "openCapacity", example = "1", required = false)
  val openCapacity: Int = 1,
  @param:Schema(description = "Location level string", example = "A-1-3-007", required = false)
  val locationLevels: String?,
  @param:Schema(description = "incentive string", example = "ENHANCED", required = false)
  val incentive: String?,
  @param:Schema(description = "category string", example = "A_EXCEPTIONAL", required = false)
  val category: String?,
  @param:Schema(description = "disable all other sessions for slot and prison", example = "false", required = false)
  val disableAllOtherSessionsForSlotAndPrison: Boolean = false,
  @param:Schema(description = "Custom session name - if needed", example = "Saturday Mornings", required = false)
  val sessionName: String? = null,
)
