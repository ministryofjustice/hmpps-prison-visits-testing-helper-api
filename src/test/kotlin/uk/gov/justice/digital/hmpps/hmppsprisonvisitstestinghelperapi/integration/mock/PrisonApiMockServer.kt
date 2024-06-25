package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.prison.api.VisitBalancesDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock.MockUtils.Companion.getJsonString

class PrisonApiMockServer : WireMockServer(8093) {
  fun stubGetPrisonerVisitBalances(
    prisonerId: String,
    visitBalancesDto: VisitBalancesDto?,
  ) {
    stubFor(
      get("/api/bookings/offenderNo/$prisonerId/visit/balances")
        .willReturn(
          if (visitBalancesDto == null) {
            aResponse().withStatus(HttpStatus.NOT_FOUND.value())
          } else {
            aResponse()
              .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
              .withStatus(200)
              .withBody(
                getJsonString(visitBalancesDto),
              )
          },
        ),
    )
  }
}
