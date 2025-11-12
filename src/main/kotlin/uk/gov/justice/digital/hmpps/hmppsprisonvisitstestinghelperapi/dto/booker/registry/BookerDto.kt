package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.booker.registry

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

class BookerDto(
  @param:JsonProperty("reference")
  @param:Schema(name = "reference", description = "booker reference", required = true)
  val reference: String,
)
