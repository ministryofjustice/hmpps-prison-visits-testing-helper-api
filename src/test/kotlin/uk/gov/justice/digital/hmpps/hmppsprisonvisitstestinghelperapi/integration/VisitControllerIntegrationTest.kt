package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import org.apache.commons.lang3.RandomStringUtils
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
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitSubStatus.AUTO_APPROVED
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callDelete
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.callPut
import java.time.LocalDate
import java.time.LocalTime

@DisplayName("Visit Controller")
class VisitControllerIntegrationTest : IntegrationTestBase() {
  val prisonCode = "HEI"
  val prison2Code = "BLI"
  val sessionTemplateReference = "session-1"
  val sessionSlotReference = "session-slot-1"
  val sessionTimeSlotStart: LocalTime = LocalTime.of(9, 0)
  val sessionTimeSlotEnd: LocalTime = LocalTime.of(10, 0)
  val existingStatus = BOOKED
  val visitDate: LocalDate = LocalDate.now()
  val visitReference = "aa-bb-cc-dd"

  @BeforeEach
  fun setup() {
    clearDb()

    dBRepository.createPrison(prisonCode)
    dBRepository.createPrison(prison2Code)
    dBRepository.createSessionTemplate(prisonCode, "room", "SOCIAL", 10, 1, sessionTimeSlotStart, sessionTimeSlotEnd, LocalDate.now(), LocalDate.now().dayOfWeek, sessionTemplateReference, sessionTemplateReference)
    dBRepository.createSessionSlot("session-slot-1", visitDate, visitDate.atTime(sessionTimeSlotStart), visitDate.atTime(sessionTimeSlotEnd), sessionTemplateReference)
  }

  @AfterEach
  fun clearAll() {
    clearDb()
  }

  @Test
  fun `when visit status change called visit status is updated`() {
    // Given
    val newStatus = CANCELLED
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus.name, "OPEN", sessionSlotReference, AUTO_APPROVED.name)

    // When
    val responseSpec = callChangeVisitStatus(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference, newStatus)

    // Then
    responseSpec.expectStatus().isOk
    val updatedStatus = dBRepository.getVisitStatus(visitReference)
    assertThat(updatedStatus).isEqualTo(CANCELLED.toString())
  }

  @Test
  fun `when visit prison change called visit prison is updated`() {
    // Given
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus.name, "OPEN", sessionSlotReference, AUTO_APPROVED.name)

    // When
    val responseSpec = callChangeVisitPrison(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference, prison2Code)

    // Then
    responseSpec.expectStatus().isOk
    val updatedPrisonCode = dBRepository.getVisitPrisonCode(visitReference)
    assertThat(updatedPrisonCode).isEqualTo(prison2Code)
  }

  @Test
  fun `when delete visit and children called then visit and all children related are deleted`() {
    // Given
    val visitNotificationReference = "aa-11-bb-22-aa"
    visitSchedulerMockServer.stubCancelVisit(visitReference)

    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus.name, "OPEN", sessionSlotReference, AUTO_APPROVED.name)
    val visitId = dBRepository.getVisitIdByReference(visitReference)
    dBRepository.createVisitVisitor(visitReference, 4776543, true)
    dBRepository.createVisitSupport(visitReference, "visit support description")
    dBRepository.createVisitNote(visitReference, VisitNoteType.VISIT_COMMENT, "visit note description")
    dBRepository.createVisitContact(visitReference, "John", "07777777777")
    dBRepository.createVisitNotification("PRISON_VISITS_BLOCKED_FOR_DATE", visitNotificationReference, visitReference, visitId)
    dBRepository.createActionedBy(null, userName = RandomStringUtils.randomAlphabetic(6), "STAFF")
    val actionById = dBRepository.getActionById()
    dBRepository.createEventAudit(visitReference, "appRef", sessionSlotReference, "BOOKED_VISIT", "NOT_KNOWN", actionById)

    // When
    val responseSpec = callDeleteVisitAndAllChildren(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference)

    // Then
    assertDeletedVisitAndAssociatedObjectsHaveBeenDeleted(responseSpec, visitReference, visitId, actionById)
  }

  @Test
  fun `when delete visit notification event called all visit notifications are deleted`() {
    // Given
    val visitNotificationReference = "aa-11-bb-22"
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus.name, "OPEN", sessionSlotReference, AUTO_APPROVED.name)
    val visitId = dBRepository.getVisitIdByReference(visitReference)
    dBRepository.createVisitNotification("PRISON_VISITS_BLOCKED_FOR_DATE", visitNotificationReference, visitReference, visitId)

    val hasVisitNotificationsBeforeCall = dBRepository.hasVisitNotificationsByBookingReference(visitReference)

    val responseSpec = callDeleteVisitNotificationEvents(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference)

    // Then
    responseSpec.expectStatus().isOk

    assertThat(hasVisitNotificationsBeforeCall).isTrue()
    val hasVisitNotifications = dBRepository.hasVisitNotificationsByBookingReference(visitReference)
    assertThat(hasVisitNotifications).isFalse()
  }

  @Test
  fun `when create visit notification event called visit notification events are created`() {
    // Given
    dBRepository.createVisit("AA123", visitReference, "SOCIAL", "ROOM-1", existingStatus.name, "OPEN", sessionSlotReference, AUTO_APPROVED.name)

    val hasVisitNotificationsBeforeCall = dBRepository.hasVisitNotificationsByBookingReference(visitReference)

    val responseSpec = callCreateVisitNotificationEvents(webTestClient, setAuthorisation(roles = listOf("ROLE_TEST_VISIT_SCHEDULER")), visitReference, CreateNotificationEventDto(PRISON_VISITS_BLOCKED_FOR_DATE))

    // Then
    responseSpec.expectStatus().isCreated
    assertThat(hasVisitNotificationsBeforeCall).isFalse()
    val hasVisitNotifications = dBRepository.hasVisitNotificationsByBookingReference(visitReference)
    assertThat(hasVisitNotifications).isTrue()
  }

  private fun assertDeletedVisitAndAssociatedObjectsHaveBeenDeleted(responseSpec: ResponseSpec, visitReference: String, visitId: Long, actionById: Int) {
    responseSpec.expectStatus().isOk
    assertThat(dBRepository.hasVisitWithReference(visitReference)).isFalse()
    assertThat(dBRepository.hasVisitVisitor(visitId)).isFalse()
    assertThat(dBRepository.hasVisitSupport(visitId)).isFalse()
    assertThat(dBRepository.hasVisitContact(visitId)).isFalse()
    assertThat(dBRepository.hasVisitNotes(visitId)).isFalse()
    assertThat(dBRepository.hasVisitNotificationsByBookingReference(visitReference)).isFalse()
    assertThat(dBRepository.hasEventAuditByBookingReference(visitReference)).isFalse()
    assertThat(dBRepository.hasActionedBy(actionById)).isFalse()
  }

  private fun callDeleteVisitAndAllChildren(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
  ): ResponseSpec = callDelete(
    webTestClient,
    "test/visit/$reference",
    authHttpHeaders,
  )

  private fun callChangeVisitStatus(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
    status: VisitStatus,
  ): ResponseSpec = callPut(
    null,
    webTestClient,
    "test/visit/$reference/status/$status",
    authHttpHeaders,
  )

  private fun callChangeVisitPrison(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
    prisonCode: String,
  ): ResponseSpec = callPut(
    null,
    webTestClient,
    "test/visit/$reference/change/prison/$prisonCode",
    authHttpHeaders,
  )

  private fun callDeleteVisitNotificationEvents(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
  ): ResponseSpec = callDelete(
    webTestClient,
    "test/visit/$reference/notifications",
    authHttpHeaders,
  )

  private fun callCreateVisitNotificationEvents(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
    createNotificationEvent: CreateNotificationEventDto,
  ): ResponseSpec = callPut(
    createNotificationEvent,
    webTestClient,
    "test/visit/$reference/notifications",
    authHttpHeaders,
  )

  private fun callCancelVisit(
    webTestClient: WebTestClient,
    authHttpHeaders: (HttpHeaders) -> Unit,
    reference: String,
  ): ResponseSpec = callDelete(
    webTestClient,
    "test/visit/$reference/cancel",
    authHttpHeaders,
  )
}
