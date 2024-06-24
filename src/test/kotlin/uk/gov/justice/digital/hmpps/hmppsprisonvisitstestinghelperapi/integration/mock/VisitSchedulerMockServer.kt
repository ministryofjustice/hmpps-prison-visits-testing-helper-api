package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.put
import org.springframework.http.HttpStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock.MockUtils.Companion.createJsonResponseBuilder

class VisitSchedulerMockServer : WireMockServer(8092) {
  fun stubCancelVisit(reference: String) {
    val responseBuilder = createJsonResponseBuilder()

    stubFor(
      put("/visits/$reference/cancel")
        .willReturn(
          responseBuilder
            .withStatus(HttpStatus.OK.value()),
        ),
    )
  }
}
