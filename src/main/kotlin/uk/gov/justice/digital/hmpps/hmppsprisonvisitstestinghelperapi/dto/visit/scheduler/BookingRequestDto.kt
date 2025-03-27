package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.visit.scheduler

import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.UserType

data class BookingRequestDto(
  val actionedBy: String,
  val applicationMethodType: String,
  val allowOverBooking: Boolean = true,
  val userType: UserType = UserType.STAFF,
)
