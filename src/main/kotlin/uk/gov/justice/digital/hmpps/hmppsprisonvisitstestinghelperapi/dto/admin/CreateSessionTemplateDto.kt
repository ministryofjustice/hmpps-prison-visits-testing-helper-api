package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.UserType
import java.time.DayOfWeek

data class CreateSessionTemplateDto(
  @Schema(description = "Name for Session template", example = "Monday Xmas", required = true)
  @field:NotBlank
  @field:Size(max = 100)
  val name: String,

  @JsonProperty("prisonId")
  @Schema(description = "prisonId", example = "MDI", required = true)
  @field:NotBlank
  val prisonCode: String,

  @Schema(description = "The start and end time of the generated visit session(s)", required = true)
  val sessionTimeSlot: SessionTimeSlotDto,

  @Schema(description = "The start and end date of the Validity period for the session template", required = true)
  val sessionDateRange: SessionDateRangeDto,

  @Schema(description = "Visit Room", example = "Visits Main Hall", required = true)
  @field:NotBlank
  @field:Size(max = 255)
  val visitRoom: String,

  @Schema(description = "The open and closed capacity of the session template", required = true)
  @field:Valid
  val sessionCapacity: SessionCapacityDto,

  @Schema(description = "day of week fpr visit", example = "MONDAY", required = true)
  val dayOfWeek: DayOfWeek,

  @Schema(description = "number of weeks until the weekly day is repeated", example = "1", required = true)
  @field:Min(1)
  @field:NotNull
  val weeklyFrequency: Int,

  @Schema(description = "list of group references for permitted session location groups", required = false)
  val locationGroupReferences: List<String>? = listOf(),

  @Schema(description = "list of group references for allowed prisoner category groups", required = false)
  val categoryGroupReferences: List<String>? = listOf(),

  @Schema(description = "list of group references for allowed prisoner incentive levels", required = false)
  val incentiveLevelGroupReferences: List<String>? = listOf(),

  @Schema(description = "Determines behaviour of location groups. True equates to these location groups being included, false equates to them being excluded.", required = true)
  val includeLocationGroupType: Boolean,

  @Schema(description = "Session template user clients.", required = false)
  val clients: List<UserClientDto> = listOf(UserClientDto(UserType.STAFF, true), UserClientDto(UserType.PUBLIC, true)),

  @Schema(description = "Determines behaviour of category groups. True equates to these category groups being included, false equates to them being excluded.", required = true)
  val includeCategoryGroupType: Boolean,

  @Schema(description = "Determines behaviour of incentive groups. True equates to these incentive groups being included, false equates to them being excluded.", required = true)
  val includeIncentiveGroupType: Boolean,
)
