package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class HmppsAuthExtension :
  BeforeAllCallback,
  AfterAllCallback,
  BeforeEachCallback {

  companion object {
    @JvmField
    val hmppsAuthApi = HmppsAuthMockServer()
  }

  override fun beforeAll(context: ExtensionContext) {
    hmppsAuthApi.start()
  }

  override fun beforeEach(context: ExtensionContext) {
    hmppsAuthApi.resetAll()
    hmppsAuthApi.stubGrantToken()
  }

  override fun afterAll(context: ExtensionContext) {
    hmppsAuthApi.stop()
  }
}

class HmppsAuthMockServer : WireMockServer(WIREMOCK_PORT) {
  companion object {
    private const val WIREMOCK_PORT = 8090
  }

  fun stubGrantToken() {
    val responseBuilder = createJsonResponseBuilder()

    stubFor(
      post(urlEqualTo("/auth/oauth/token"))
        .willReturn(
          responseBuilder
            .withStatus(HttpStatus.OK.value())
            .withBody(
              """
              {
                "token_type": "bearer",
                "access_token": "atoken"
              }
              """.trimIndent(),
            ),
        ),
    )
  }

  fun createJsonResponseBuilder(): ResponseDefinitionBuilder = WireMock.aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withHeader("Connection", "close")
}
