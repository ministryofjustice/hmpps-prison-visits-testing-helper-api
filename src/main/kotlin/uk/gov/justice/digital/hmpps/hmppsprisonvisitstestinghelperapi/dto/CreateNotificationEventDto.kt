package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.TestDBNotificationEventTypes

class CreateNotificationEventDto(
  val notificationEvent: TestDBNotificationEventTypes,
)
