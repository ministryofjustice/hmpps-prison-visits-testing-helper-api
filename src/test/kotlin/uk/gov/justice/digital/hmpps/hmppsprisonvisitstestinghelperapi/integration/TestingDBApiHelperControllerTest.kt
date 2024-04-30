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
import java.time.LocalDate
import java.time.LocalTime

@DisplayName("Testing Admin API helper Controller")
class TestingDBApiHelperControllerTest : IntegrationTestBase() {
  companion object {
    const val PRISON_CODE = "HEI"
  }
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
  }

  @AfterEach
  fun clearAll() {
    clearDb()
  }

  @Test
  fun `when visit status change called visit status is updated`() {
    // Given
    val visitReference = "aa-bb-cc-dd"
    val visitDate = LocalDate.now()
    val sessionTemplateReference = "session-1"
    val sessionSlotReference = "session-slot-1"
    val sessionTimeSlot = SessionTimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))

    val existingStatus = BOOKED
    val newStatus = CANCELLED

    dBRepository.createPrison(PRISON_CODE)
    dBRepository.createSessionTemplate(PRISON_CODE, "room", "SOCIAL", 10, 1, sessionTimeSlot.startTime, sessionTimeSlot.endTime, LocalDate.now(), LocalDate.now().dayOfWeek, sessionTemplateReference, sessionTemplateReference)
    dBRepository.createSessionSlot("session-slot-1", visitDate, visitDate.atTime(sessionTimeSlot.startTime), visitDate.atTime(sessionTimeSlot.endTime), sessionTemplateReference)
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus, "OPEN", sessionSlotReference)
    val responseSpec = callChangeVisitStatus(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference, newStatus)

    // Then
    responseSpec.expectStatus().isOk
    val updatedStatus = dBRepository.getVisitStatus(visitReference)
    Assertions.assertThat(updatedStatus).isEqualTo(CANCELLED.toString())
  }

  @Test
  fun `when delete visit notification event called all visit notifications are deleted`() {
    // Given
    val visitReference = "aa-bb-cc-dd"
    val visitDate = LocalDate.now()
    val sessionTemplateReference = "session-1"
    val sessionSlotReference = "session-slot-1"
    val sessionTimeSlot = SessionTimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
    val visitNotificationReference = "aa-11-bb-22"
    val existingStatus = BOOKED

    dBRepository.createPrison(PRISON_CODE)
    dBRepository.createSessionTemplate(PRISON_CODE, "room", "SOCIAL", 10, 1, sessionTimeSlot.startTime, sessionTimeSlot.endTime, LocalDate.now(), LocalDate.now().dayOfWeek, sessionTemplateReference, sessionTemplateReference)
    dBRepository.createSessionSlot("session-slot-1", visitDate, visitDate.atTime(sessionTimeSlot.startTime), visitDate.atTime(sessionTimeSlot.endTime), sessionTemplateReference)
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
    val visitReference = "aa-bb-cc-dd"
    val visitDate = LocalDate.now()
    val sessionTemplateReference = "session-1"
    val sessionSlotReference = "session-slot-1"
    val sessionTimeSlot = SessionTimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
    val existingStatus = BOOKED

    dBRepository.createPrison(PRISON_CODE)
    dBRepository.createSessionTemplate(PRISON_CODE, "room", "SOCIAL", 10, 1, sessionTimeSlot.startTime, sessionTimeSlot.endTime, LocalDate.now(), LocalDate.now().dayOfWeek, sessionTemplateReference, sessionTemplateReference)
    dBRepository.createSessionSlot("session-slot-1", visitDate, visitDate.atTime(sessionTimeSlot.startTime), visitDate.atTime(sessionTimeSlot.endTime), sessionTemplateReference)
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
