package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SQSMessage(
  @JsonProperty("Type")
  val type: String,

  @JsonProperty("Message")
  val message: String,

  @JsonProperty("MessageId")
  val messageId: String? = null,
)
