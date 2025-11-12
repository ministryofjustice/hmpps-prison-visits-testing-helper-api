package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SQSMessage(
  @param:JsonProperty("Type")
  val type: String,

  @param:JsonProperty("Message")
  val message: String,

  @param:JsonProperty("MessageId")
  val messageId: String? = null,
)
