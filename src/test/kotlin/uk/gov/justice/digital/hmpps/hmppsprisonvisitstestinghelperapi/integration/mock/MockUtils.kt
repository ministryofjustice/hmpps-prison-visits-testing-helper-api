package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.http.MediaType
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper

class MockUtils {
  companion object {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun getJsonString(obj: Any): String = objectMapper.writer().writeValueAsString(obj)

    fun createJsonResponseBuilder(): ResponseDefinitionBuilder = WireMock.aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
  }
}
