package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.delete
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.booker.registry.BookerDto

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

  fun stubGetBookerDetails(emailAddress: String, bookerDtos: List<BookerDto>?, httpStatus: HttpStatus = HttpStatus.NOT_FOUND) {
    stubFor(
      get("/public/booker/config/email/$emailAddress")
        .willReturn(
          if (bookerDtos == null) {
            aResponse().withStatus(httpStatus.value())
          } else {
            aResponse()
              .withBody(getJsonString(bookerDtos))
              .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
              .withStatus(200)
          },
        ),
    )
  }

  private fun getJsonString(bookerDtos: List<BookerDto>): String = ObjectMapper()
    .registerModule(JavaTimeModule())
    .writer()
    .withDefaultPrettyPrinter()
    .writeValueAsString(bookerDtos)
}
