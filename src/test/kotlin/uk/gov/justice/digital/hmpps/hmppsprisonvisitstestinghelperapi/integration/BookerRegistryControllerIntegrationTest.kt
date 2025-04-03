package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.BookerRegistryClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callDelete

@DisplayName("Booker Registry Controller")
class BookerRegistryControllerIntegrationTest : IntegrationTestBase() {

  @MockitoSpyBean
  lateinit var bookerRegistryClient: BookerRegistryClient

  @Test
  fun `when reset booker reference is called, then status is OK`() {
    // Given
    val bookerReference = "ab-cd-ef-gh"
    bookerRegistryApiMockServer.stubResetBookerDetails(bookerReference)

    // When
    val responseSpec = callResetBookerDetails(webTestClient, setAuthorisation(roles = listOf("TEST_BOOKER_REGISTRY")), bookerReference)

    // Then
    responseSpec.expectStatus().isOk
  }

  private fun callResetBookerDetails(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    bookerReference: String,
  ): ResponseSpec = callDelete(
    webTestClient,
    "/test/booker/$bookerReference",
    authHttpHeaders,
  )
}
