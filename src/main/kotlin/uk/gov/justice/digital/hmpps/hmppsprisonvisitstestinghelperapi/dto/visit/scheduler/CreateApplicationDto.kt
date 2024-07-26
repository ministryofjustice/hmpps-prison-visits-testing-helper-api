package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.visit.scheduler

import java.time.LocalDate
import java.time.LocalTime

data class CreateApplicationDto(
  val prisonCode: String,
  val prisonerId: String,
  val sessionDate: LocalDate,
  val sessionStart: LocalTime,
  val sessionEnd: LocalTime,
  val userType: String,
  val contactName: String,
  val visitors: List<Long>,
  val visitRestriction: String,
)
