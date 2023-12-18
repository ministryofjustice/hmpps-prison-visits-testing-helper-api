package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

@Suppress("unused")
enum class VisitStatus(
  val description: String,
) {
  RESERVED("Reserved"),
  CHANGING("Changing"),
  BOOKED("Booked"),
  CANCELLED("Cancelled"),
}
