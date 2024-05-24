package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class HmppsAuthExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

  companion object {
    @JvmField
    val hmppsAuthApi = HmppsAuthMockServer()
  }

  override fun beforeAll(context: ExtensionContext) {
    hmppsAuthApi.start()
    hmppsAuthApi.stubGrantToken()
  }

  override fun beforeEach(context: ExtensionContext) {
    hmppsAuthApi.resetRequests()
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
    stubFor(
      post(WireMock.urlEqualTo("/auth/oauth/token"))
        .willReturn(
          aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
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
}
