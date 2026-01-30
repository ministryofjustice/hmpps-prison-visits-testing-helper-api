package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.http.MediaType

class MockUtils {
  companion object {
    private val objectMapper: ObjectMapper = JsonMapper.builder()
      .addModule(JavaTimeModule())
      .addModule(kotlinModule())
      .build()

    fun getJsonString(obj: Any): String = objectMapper.writer().writeValueAsString(obj)

    fun createJsonResponseBuilder(): ResponseDefinitionBuilder = WireMock.aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
  }
}
