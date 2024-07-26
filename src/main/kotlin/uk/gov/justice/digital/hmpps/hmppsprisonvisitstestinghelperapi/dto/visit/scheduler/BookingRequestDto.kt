package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.visit.scheduler

data class BookingRequestDto(
  val actionedBy: String,
  val applicationMethodType: String,
  val allowOverBooking: Boolean = true,
)
