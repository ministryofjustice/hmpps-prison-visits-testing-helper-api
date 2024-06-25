package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller.CHANGE_CLOSED_SESSION_SLOT_CAPACITY_FOR_APPLICATION
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller.CHANGE_OPEN_SESSION_SLOT_CAPACITY_FOR_APPLICATION
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller.GET_CLOSED_SESSION_SLOT_CAPACITY_FOR_APPLICATION
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller.GET_OPEN_SESSION_SLOT_CAPACITY_FOR_APPLICATION
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callDelete
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callGet
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callPut
import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@DisplayName("Application Controller")
class ApplicationControllerIntegrationTest : IntegrationTestBase() {
  val prisonCode = "HEI"
  val prison2Code = "BLI"
  val sessionTemplateReference = "session-1"
  val sessionSlotReference = "session-slot-1"
  val sessionTimeSlotStart: LocalTime = LocalTime.of(9, 0)
  val sessionTimeSlotEnd: LocalTime = LocalTime.of(10, 0)
  val visitDate: LocalDate = LocalDate.now()
  val applicationReference = "abc-fgh-cbv"
  val openCapacity = 10
  val closedCapacity = 1

  @BeforeEach
  fun setup() {
    clearDb()

    dBRepository.createPrison(prisonCode)
    dBRepository.createPrison(prison2Code)
    dBRepository.createSessionTemplate(prisonCode, "room", "SOCIAL", openCapacity, closedCapacity, sessionTimeSlotStart, sessionTimeSlotEnd, LocalDate.now(), LocalDate.now().dayOfWeek, sessionTemplateReference, sessionTemplateReference)
    dBRepository.createSessionSlot(sessionSlotReference, visitDate, visitDate.atTime(sessionTimeSlotStart), visitDate.atTime(sessionTimeSlotEnd), sessionTemplateReference)
  }

  @AfterEach
  fun clearAll() {
    clearDb()
  }

  @Test
  fun `when update application modified date change called visit modified date is updated`() {
    // Given
    val newTimestamp = LocalDateTime.now().plusMinutes(20)
    val prisonId = dBRepository.getPrisonIdFromSessionTemplate(sessionTemplateReference)
    val sessionSlotId = dBRepository.getSessionSlotId(sessionSlotReference)

    dBRepository.createApplication(
      prisonId,
      "AA123",
      sessionSlotId,
      true,
      applicationReference,
      "SOCIAL",
      "OPEN",
      false,
      "TEST",
      Timestamp.valueOf(LocalDateTime.now()),
      "STAFF",
    )

    // When
    val responseSpec = callUpdateApplicationModifiedTimestamp(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), applicationReference, newTimestamp)

    // Then
    responseSpec.expectStatus().isOk
    val updatedTimestamp = dBRepository.getApplicationModifiedTimestamp(applicationReference)
    assertThat(updatedTimestamp).isEqualTo(Timestamp.valueOf(newTimestamp))
  }

  @Test
  fun `when delete application and children called then application and children are deleted`() {
    // Given
    val prisonId = dBRepository.getPrisonIdFromSessionTemplate(sessionTemplateReference)
    val sessionSlotId = dBRepository.getSessionSlotId(sessionSlotReference)

    dBRepository.createApplication(
      prisonId,
      "AA123",
      sessionSlotId,
      true,
      applicationReference,
      "SOCIAL",
      "OPEN",
      false,
      "TEST",
      Timestamp.valueOf(LocalDateTime.now()),
      "STAFF",
    )
    dBRepository.createApplicationVisitors(applicationReference, 4776543, true)
    dBRepository.createApplicationSupport(applicationReference, "application support description")
    dBRepository.createApplicationContact(applicationReference, "John", "07777777777")

    val applicationId = dBRepository.getApplicationIdByReference(applicationReference)

    // When
    val responseSpec = callDeleteApplicationAndAllChildren(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), applicationReference)

    // Then
    assertDeleteApplicationAndAssociatedObjectsHaveBeenDeleted(responseSpec, applicationReference, applicationId)
  }

  @Test
  fun `when change open slot capacity for application then capacity is changed `() {
    // Given
    val prisonId = dBRepository.getPrisonIdFromSessionTemplate(sessionTemplateReference)
    val sessionSlotId = dBRepository.getSessionSlotId(sessionSlotReference)

    dBRepository.createApplication(
      prisonId,
      "AA123",
      sessionSlotId,
      true,
      applicationReference,
      "SOCIAL",
      "OPEN",
      false,
      "TEST",
      Timestamp.valueOf(LocalDateTime.now()),
      "STAFF",
    )

    val responseSpec = callChangeOpenSessionSlotCapacityForApplication(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), applicationReference, 1)

    // Then
    responseSpec.expectStatus().isOk
  }

  @Test
  fun `when change closed slot capacity for application then capacity is changed `() {
    // Given
    val prisonId = dBRepository.getPrisonIdFromSessionTemplate(sessionTemplateReference)
    val sessionSlotId = dBRepository.getSessionSlotId(sessionSlotReference)

    dBRepository.createApplication(
      prisonId,
      "AA123",
      sessionSlotId,
      true,
      applicationReference,
      "SOCIAL",
      "OPEN",
      false,
      "TEST",
      Timestamp.valueOf(LocalDateTime.now()),
      "STAFF",
    )

    val responseSpec = callChangeClosedSessionSlotCapacityForApplication(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), applicationReference, 1)

    // Then
    responseSpec.expectStatus().isOk
  }

  @Test
  fun `when get open slot capacity for application then capacity returned`() {
    // Given
    val prisonId = dBRepository.getPrisonIdFromSessionTemplate(sessionTemplateReference)
    val sessionSlotId = dBRepository.getSessionSlotId(sessionSlotReference)

    dBRepository.createApplication(
      prisonId,
      "AA123",
      sessionSlotId,
      true,
      applicationReference,
      "SOCIAL",
      "OPEN",
      false,
      "TEST",
      Timestamp.valueOf(LocalDateTime.now()),
      "STAFF",
    )

    val responseSpec = callGetOpenSessionSlotCapacityForApplication(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), applicationReference)

    // Then
    responseSpec.expectStatus().isOk
    val responseBody = responseSpec.expectBody().returnResult().responseBody
    val capacity = String(responseBody!!, StandardCharsets.UTF_8).toInt()
    assertThat(capacity).isEqualTo(openCapacity)
  }

  @Test
  fun `when get closed slot capacity for application then capacity returned`() {
    // Given
    val prisonId = dBRepository.getPrisonIdFromSessionTemplate(sessionTemplateReference)
    val sessionSlotId = dBRepository.getSessionSlotId(sessionSlotReference)

    dBRepository.createApplication(
      prisonId,
      "AA123",
      sessionSlotId,
      true,
      applicationReference,
      "SOCIAL",
      "OPEN",
      false,
      "TEST",
      Timestamp.valueOf(LocalDateTime.now()),
      "STAFF",
    )

    val responseSpec = callGetClosedSessionSlotCapacityForApplication(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), applicationReference)

    // Then
    responseSpec.expectStatus().isOk
    val responseBody = responseSpec.expectBody().returnResult().responseBody
    val capacity = String(responseBody!!, StandardCharsets.UTF_8).toInt()
    assertThat(capacity).isEqualTo(closedCapacity)
  }

  private fun assertDeleteApplicationAndAssociatedObjectsHaveBeenDeleted(responseSpec: ResponseSpec, applicationReference: String, applicationId: Long) {
    responseSpec.expectStatus().isOk
    assertThat(dBRepository.hasApplicationWithReference(applicationReference)).isFalse()
    assertThat(dBRepository.hasApplicationVisitor(applicationId)).isFalse()
    assertThat(dBRepository.hasApplicationSupport(applicationId)).isFalse()
    assertThat(dBRepository.hasApplicationContact(applicationId)).isFalse()
  }

  private fun callDeleteApplicationAndAllChildren(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
  ): ResponseSpec {
    return callDelete(
      webTestClient,
      "test/application/$reference",
      authHttpHeaders,
    )
  }

  private fun callUpdateApplicationModifiedTimestamp(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
    timestamp: LocalDateTime,
  ): ResponseSpec {
    return callPut(
      null,
      webTestClient,
      "/test/application/$reference/modifiedTimestamp/$timestamp",
      authHttpHeaders,
    )
  }

  private fun callChangeClosedSessionSlotCapacityForApplication(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    applicationReference: String,
    capacity: Int,
  ): ResponseSpec {
    return callPut(
      webTestClient = webTestClient,
      url = CHANGE_CLOSED_SESSION_SLOT_CAPACITY_FOR_APPLICATION.replace("{reference}", applicationReference).replace("{capacity}", capacity.toString()),
      authHttpHeaders = authHttpHeaders,
    )
  }

  private fun callChangeOpenSessionSlotCapacityForApplication(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    applicationReference: String,
    capacity: Int,
  ): ResponseSpec {
    return callPut(
      webTestClient = webTestClient,
      url = CHANGE_OPEN_SESSION_SLOT_CAPACITY_FOR_APPLICATION.replace("{reference}", applicationReference).replace("{capacity}", capacity.toString()),
      authHttpHeaders = authHttpHeaders,
    )
  }

  private fun callGetOpenSessionSlotCapacityForApplication(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    applicationReference: String,
  ): ResponseSpec {
    return callGet(
      webTestClient = webTestClient,
      url = GET_OPEN_SESSION_SLOT_CAPACITY_FOR_APPLICATION.replace("{reference}", applicationReference),
      authHttpHeaders = authHttpHeaders,
    )
  }

  private fun callGetClosedSessionSlotCapacityForApplication(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    applicationReference: String,
  ): ResponseSpec {
    return callGet(
      webTestClient = webTestClient,
      url = GET_CLOSED_SESSION_SLOT_CAPACITY_FOR_APPLICATION.replace("{reference}", applicationReference),
      authHttpHeaders = authHttpHeaders,
    )
  }
}
