package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DomainEvent(
  @JsonProperty("eventType")
  val eventType: String,

  @JsonProperty("additionalInformation")
  val additionalInformation: Map<String, Any>,
)
