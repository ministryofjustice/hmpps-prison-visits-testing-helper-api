package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums

enum class Events(val eventType: String) {
  NON_ASSOCIATION_CREATE_EVENT("non-associations.created"),
  PRISONER_RELEASE_EVENT("prison-offender-events.prisoner.released"),
  PRISONER_RECEIVE_EVENT("prison-offender-events.prisoner.received"),
  PRISONER_RESTRICTION_CHANGE_EVENT("prison-offender-events.prisoner.restriction.changed"),
  PRISONER_ALERT_UPDATED_EVENT("prisoner-offender-search.prisoner.alerts-updated"),
  VISITOR_RESTRICTION_CHANGE_EVENT("prison-offender-events.visitor.restriction.changed"),
}
