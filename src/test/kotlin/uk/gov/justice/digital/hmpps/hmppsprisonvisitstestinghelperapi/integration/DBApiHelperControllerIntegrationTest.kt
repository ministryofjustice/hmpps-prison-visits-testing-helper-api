package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.CreateNotificationEventDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.TestDBNotificationEventTypes.PRISON_VISITS_BLOCKED_FOR_DATE
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitNoteType
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitStatus.BOOKED
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitStatus.CANCELLED
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callDelete
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callPut
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@DisplayName("Testing DB API helper Controller")
class DBApiHelperControllerIntegrationTest : IntegrationTestBase() {
  val prisonCode = "HEI"
  val sessionTemplateReference = "session-1"
  val sessionSlotReference = "session-slot-1"
  val sessionTimeSlot = SessionTimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
  val existingStatus = BOOKED
  val visitDate: LocalDate = LocalDate.now()
  val visitReference = "aa-bb-cc-dd"
  val applicationReference = "abc-fgh-cbv"

  fun callDeleteVisitAndAllChildren(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
  ): WebTestClient.ResponseSpec {
    return callDelete(
      webTestClient,
      "test/visit/$reference",
      authHttpHeaders,
    )
  }

  fun callDeleteApplicationAndAllChildren(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
  ): WebTestClient.ResponseSpec {
    return callDelete(
      webTestClient,
      "test/application/$reference",
      authHttpHeaders,
    )
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
    assertThat(updatedTimestamp).isEqualTo(Timestamp.valueOf(newTimestamp))
  }

  @Test
  fun `when delete application and children called then application and children are deleted`() {
    // Given
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
  fun `when visit status change called visit status is updated`() {
    // Given
    val newStatus = CANCELLED
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus, "OPEN", sessionSlotReference)

    // When
    val responseSpec = callChangeVisitStatus(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference, newStatus)

    // Then
    responseSpec.expectStatus().isOk
    val updatedStatus = dBRepository.getVisitStatus(visitReference)
    assertThat(updatedStatus).isEqualTo(CANCELLED.toString())
  }

  @Test
  fun `when delete visit and children called then visit and all children related are deleted`() {
    // Given
    val visitNotificationReference = "aa-11-bb-22-aa"

    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus, "OPEN", sessionSlotReference)
    dBRepository.createVisitVisitor(visitReference, 4776543, true)
    dBRepository.createVisitSupport(visitReference, "visit support description")
    dBRepository.createVisitNote(visitReference, VisitNoteType.VISIT_COMMENT, "visit note description")
    dBRepository.createVisitContact(visitReference, "John", "07777777777")
    dBRepository.createVisitNotification("PRISON_VISITS_BLOCKED_FOR_DATE", visitNotificationReference, visitReference)

    val visitId = dBRepository.getVisitIdByReference(visitReference)

    // When
    val responseSpec = callDeleteVisitAndAllChildren(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference)

    // Then
    assertDeletedVisitAndAssociatedObjectsHaveBeenDeleted(responseSpec, visitReference, visitId)
  }

  @Test
  fun `when delete visit notification event called all visit notifications are deleted`() {
    // Given
    val visitNotificationReference = "aa-11-bb-22"
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus, "OPEN", sessionSlotReference)
    dBRepository.createVisitNotification("PRISON_VISITS_BLOCKED_FOR_DATE", visitNotificationReference, visitReference)

    var hasVisitNotificationsBeforeCall = dBRepository.hasVisitNotificationsByBookingReference(visitReference)

    val responseSpec = callDeleteVisitNotificationEvents(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference)

    // Then
    responseSpec.expectStatus().isOk

    assertThat(hasVisitNotificationsBeforeCall).isTrue()
    var hasVisitNotifications = dBRepository.hasVisitNotificationsByBookingReference(visitReference)
    assertThat(hasVisitNotifications).isFalse()
  }

  @Test
  fun `when create visit notification event called visit notification events are created`() {
    // Given
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus, "OPEN", sessionSlotReference)

    var hasVisitNotificationsBeforeCall = dBRepository.hasVisitNotificationsByBookingReference(visitReference)

    val responseSpec = callCreateVisitNotificationEvents(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference, CreateNotificationEventDto(PRISON_VISITS_BLOCKED_FOR_DATE))

    // Then
    responseSpec.expectStatus().isCreated
    assertThat(hasVisitNotificationsBeforeCall).isFalse()
    var hasVisitNotifications = dBRepository.hasVisitNotificationsByBookingReference(visitReference)
    assertThat(hasVisitNotifications).isTrue()
  }

  fun assertDeletedVisitAndAssociatedObjectsHaveBeenDeleted(responseSpec: ResponseSpec, visitReference: String, visitId: Long) {
    responseSpec.expectStatus().isOk
    assertThat(dBRepository.hasVisitWithReference(visitReference)).isFalse()
    assertThat(dBRepository.hasVisitVisitor(visitId)).isFalse()
    assertThat(dBRepository.hasVisitSupport(visitId)).isFalse()
    assertThat(dBRepository.hasVisitContact(visitId)).isFalse()
    assertThat(dBRepository.hasVisitNotes(visitId)).isFalse()
    assertThat(dBRepository.hasVisitNotificationsByBookingReference(visitReference)).isFalse()
  }

  fun assertDeleteApplicationAndAssociatedObjectsHaveBeenDeleted(responseSpec: ResponseSpec, applicationReference: String, applicationId: Long) {
    responseSpec.expectStatus().isOk
    assertThat(dBRepository.hasApplicationWithReference(applicationReference)).isFalse()
    assertThat(dBRepository.hasApplicationVisitor(applicationId)).isFalse()
    assertThat(dBRepository.hasApplicationSupport(applicationId)).isFalse()
    assertThat(dBRepository.hasApplicationContact(applicationId)).isFalse()
  }
}

class SessionTimeSlot(
  val startTime: LocalTime,
  val endTime: LocalTime,
)
