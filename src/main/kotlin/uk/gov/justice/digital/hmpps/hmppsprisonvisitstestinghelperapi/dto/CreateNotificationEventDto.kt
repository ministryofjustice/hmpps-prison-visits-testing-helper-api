package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.DBNotificationEventType

class CreateNotificationEventDto(
  val notificationEvent: DBNotificationEventType,
)
