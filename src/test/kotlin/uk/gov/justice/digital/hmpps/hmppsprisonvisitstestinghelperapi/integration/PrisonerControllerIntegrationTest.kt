package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.prison.api.VisitBalancesDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callGet

@DisplayName("Visit Controller")
class PrisonerControllerIntegrationTest : IntegrationTestBase() {

  @Autowired
  protected lateinit var objectMapper: ObjectMapper

  @SpyBean
  lateinit var prisonApiClient: PrisonApiClient

  @Test
  fun `when get prisoner visit balances is called then visit balances are returned`() {
    // Given
    val prisonerId = "A1234AA"
    val visitBalancesDto = VisitBalancesDto(remainingVo = 1, remainingPvo = 1)
    prisonApiMockServer.stubGetPrisonerVisitBalances(prisonerId = prisonerId, visitBalancesDto = visitBalancesDto)

    // When
    val responseSpec = callGetPrisonerVisitBalances(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), prisonerId)

    // Then
    responseSpec.expectStatus().isOk
    val responseDto = objectMapper.readValue(responseSpec.expectBody().returnResult().responseBody, VisitBalancesDto::class.java)
    assertThat(responseDto.remainingVo).isEqualTo(1)
    assertThat(responseDto.remainingPvo).isEqualTo(1)
  }

  @Test
  fun `when get prisoner visit balances is called and prisoner not found, then exception is thrown`() {
    // Given
    val prisonerId = "A1234AA"
    prisonApiMockServer.stubGetPrisonerVisitBalances(prisonerId = prisonerId, visitBalancesDto = null)

    // When
    val responseSpec = callGetPrisonerVisitBalances(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), prisonerId)

    // Then
    responseSpec.expectStatus().isNotFound
    verify(prisonApiClient, times(1)).getPrisonerVisitBalances(prisonerId)
  }

  private fun callGetPrisonerVisitBalances(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    prisonerId: String,
  ): ResponseSpec = callGet(
    webTestClient,
    "test/prisoner/$prisonerId/visit/balances",
    authHttpHeaders,
  )
}
