package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller.RESET_BOOKER_REGISTRY_BY_EMAIL_ADDRESS
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.booker.registry.BookerDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callDelete

@DisplayName("Booker Registry Controller")
class BookerRegistryControllerIntegrationTest : IntegrationTestBase() {
  @Test
  fun `when reset booker reference is called, then status is OK`() {
    // Given
    val bookerReference = "bfop-zmmn-njay"
    bookerRegistryApiMockServer.stubResetBookerDetails(bookerReference)

    // When
    val responseSpec = callResetBookerDetails(webTestClient, setAuthorisation(roles = listOf("TEST_BOOKER_REGISTRY")), bookerReference)

    // Then
    responseSpec.expectStatus().isOk
  }

  @Test
  fun `when reset booker by email address is called, then status is OK`() {
    // Given
    val emailAddress = "test@example.com"
    val bookerReference = "test-abcd-test"

    bookerRegistryApiMockServer.stubGetBookerDetails(emailAddress, listOf(BookerDto(bookerReference)))
    bookerRegistryApiMockServer.stubResetBookerDetails(emailAddress)

    // When
    val responseSpec = callResetBookerDetailsByEmailAddress(webTestClient, setAuthorisation(roles = listOf("TEST_BOOKER_REGISTRY")), emailAddress)

    // Then
    responseSpec.expectStatus().isOk
  }

  @Test
  fun `when reset booker by email address is called, and status is NOT_FOUND status returned is NOT_FOUND`() {
    // Given
    val emailAddress = "test@example.com"

    bookerRegistryApiMockServer.stubGetBookerDetails(emailAddress, null, HttpStatus.NOT_FOUND)
    bookerRegistryApiMockServer.stubResetBookerDetails(emailAddress)

    // When
    val responseSpec = callResetBookerDetailsByEmailAddress(webTestClient, setAuthorisation(roles = listOf("TEST_BOOKER_REGISTRY")), emailAddress)

    // Then
    responseSpec.expectStatus().isNotFound
  }

  @Test
  fun `when reset booker by email address is called, and status is INTERNAL_SERVER_ERROR status returned is INTERNAL_SERVER_ERROR`() {
    // Given
    val emailAddress = "test@example.com"

    bookerRegistryApiMockServer.stubGetBookerDetails(emailAddress, null, HttpStatus.INTERNAL_SERVER_ERROR)
    bookerRegistryApiMockServer.stubResetBookerDetails(emailAddress)

    // When
    val responseSpec = callResetBookerDetailsByEmailAddress(webTestClient, setAuthorisation(roles = listOf("TEST_BOOKER_REGISTRY")), emailAddress)

    // Then
    responseSpec.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
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

  private fun callResetBookerDetailsByEmailAddress(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    emailAddress: String,
  ): ResponseSpec = callDelete(
    webTestClient,
    RESET_BOOKER_REGISTRY_BY_EMAIL_ADDRESS.replace("{emailAddress}", emailAddress),
    authHttpHeaders,
  )
}
