package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.prison.api

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Balances of visit orders and privilege visit orders")
data class VisitBalancesDto(
  @param:Schema(required = true, description = "Balance of visit orders remaining")
  val remainingVo: Int,

  @param:Schema(required = true, description = "Balance of privilege visit orders remaining")
  val remainingPvo: Int,

  @param:Schema(description = "Date of last IEP adjustment for Visit orders")
  val latestIepAdjustDate: LocalDate? = null,

  @param:Schema(description = "Date of last IEP adjustment for Privilege Visit orders")
  val latestPrivIepAdjustDate: LocalDate? = null,
)
