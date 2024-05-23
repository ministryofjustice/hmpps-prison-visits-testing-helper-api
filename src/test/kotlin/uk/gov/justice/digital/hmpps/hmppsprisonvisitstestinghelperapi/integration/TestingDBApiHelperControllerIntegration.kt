package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.CreateNotificationEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitStatus.BOOKED
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.VisitStatus.CANCELLED
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.TestDBNotificationEventTypes.PRISON_VISITS_BLOCKED_FOR_DATE
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callDelete
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callPut
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@DisplayName("Testing Admin API helper Controller")
class TestingDBApiHelperControllerIntegration : IntegrationTestBase() {
  val prisonCode = "HEI"
  val sessionTemplateReference = "session-1"
  val sessionSlotReference = "session-slot-1"
  val sessionTimeSlot = SessionTimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
  val existingStatus = BOOKED
  val visitDate: LocalDate = LocalDate.now()
  val visitReference = "aa-bb-cc-dd"

  fun callChangeVisitStatus(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
    status: VisitStatus,
  ): WebTestClient.ResponseSpec {
    return callPut(
      null,
      webTestClient,
      "test/visit/$reference/status/$status",
      authHttpHeaders,
    )
  }

  fun callUpdateApplicationModifiedTimestamp(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
    timestamp: LocalDateTime,
  ): WebTestClient.ResponseSpec {
    return callPut(
      null,
      webTestClient,
      "/test/application/$reference/modifiedTimestamp/$timestamp",
      authHttpHeaders,
    )
  }

  fun callDeleteVisitNotificationEvents(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
  ): WebTestClient.ResponseSpec {
    return callDelete(
      webTestClient,
      "test/visit/$reference/notifications",
      authHttpHeaders,
    )
  }

  fun callCreateVisitNotificationEvents(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
    createNotificationEvent: CreateNotificationEventDto,
  ): WebTestClient.ResponseSpec {
    return callPut(
      createNotificationEvent,
      webTestClient,
      "test/visit/$reference/notifications",
      authHttpHeaders,
    )
  }

  @BeforeEach
  fun setup() {
    clearDb()

    dBRepository.createPrison(prisonCode)
    dBRepository.createSessionTemplate(prisonCode, "room", "SOCIAL", 10, 1, sessionTimeSlot.startTime, sessionTimeSlot.endTime, LocalDate.now(), LocalDate.now().dayOfWeek, sessionTemplateReference, sessionTemplateReference)
    dBRepository.createSessionSlot("session-slot-1", visitDate, visitDate.atTime(sessionTimeSlot.startTime), visitDate.atTime(sessionTimeSlot.endTime), sessionTemplateReference)
  }

  @AfterEach
  fun clearAll() {
    clearDb()
  }

  @Test
  fun `when update application modified date change called visit modified date is updated`() {

    // Given
    val newTimestamp = LocalDateTime.now().plusMinutes(20)
    val applicationReference = "abc-fgh-cbv"

    val prisonId = dBRepository.getPrisonIdFromSessionTemplate(sessionTemplateReference)

    dBRepository.createApplication(
      prisonId,
      "AA123",
      1,
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
    Assertions.assertThat(updatedTimestamp).isEqualTo(Timestamp.valueOf(newTimestamp))
  }

  @Test
  fun `when visit status change called visit status is updated`() {
    // Given
    val existingStatus = BOOKED
    val newStatus = CANCELLED

    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus, "OPEN", sessionSlotReference)

    // When
    val responseSpec = callChangeVisitStatus(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference, newStatus)

    // Then
    responseSpec.expectStatus().isOk
    val updatedStatus = dBRepository.getVisitStatus(visitReference)
    Assertions.assertThat(updatedStatus).isEqualTo(CANCELLED.toString())
  }

  @Test
  fun `when delete visit notification event called all visit notifications are deleted`() {
    // Given
    val visitNotificationReference = "aa-11-bb-22"
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus, "OPEN", sessionSlotReference)
    dBRepository.createVisitNotification("PRISON_VISITS_BLOCKED_FOR_DATE", visitNotificationReference, visitReference)

    var hasVisitNotifications = dBRepository.hasVisitNotifications(visitReference)
    Assertions.assertThat(hasVisitNotifications).isTrue()

    val responseSpec = callDeleteVisitNotificationEvents(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference)

    // Then
    responseSpec.expectStatus().isOk
    hasVisitNotifications = dBRepository.hasVisitNotifications(visitReference)
    Assertions.assertThat(hasVisitNotifications).isFalse()
  }

  @Test
  fun `when create visit notification event called visit notification events are created`() {
    // Given
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus, "OPEN", sessionSlotReference)

    var hasVisitNotifications = dBRepository.hasVisitNotifications(visitReference)
    Assertions.assertThat(hasVisitNotifications).isFalse()

    val responseSpec = callCreateVisitNotificationEvents(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference, CreateNotificationEventDto(PRISON_VISITS_BLOCKED_FOR_DATE))

    // Then
    responseSpec.expectStatus().isCreated
    hasVisitNotifications = dBRepository.hasVisitNotifications(visitReference)
    Assertions.assertThat(hasVisitNotifications).isTrue()
  }
}

class SessionTimeSlot(
  val startTime: LocalTime,
  val endTime: LocalTime,
)
