package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.delete
import org.springframework.http.MediaType

class BookerRegistryApiMockServer : WireMockServer(8094) {
  fun stubResetBookerDetails(bookerReference: String) {
    stubFor(
      delete("/public/booker/config/$bookerReference")
        .willReturn(
          aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(200),
        ),
    )
  }
}
