package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.SessionTemplateRepository
import java.time.DayOfWeek.SATURDAY
import java.time.LocalDate
import java.time.LocalTime

@DisplayName("Visit Controller")
@Transactional
class SessionTemplateIntegrationTest : IntegrationTestBase() {

  @Autowired
  lateinit var sessionTemplateRepository: SessionTemplateRepository

  @Test
  fun `test deActivateSessionTemplate SQL`() {
    // Given
    val sessionTemplateReference = "testToSeeIfSqlValid"

    // When
    val results = sessionTemplateRepository.deActivateSessionTemplate(sessionTemplateReference)

    // Then
    assertThat(results).isEqualTo(0)
  }

  @Test
  fun `test activateSessionTemplate SQL`() {
    // Given
    val sessionTemplateReference = "testToSeeIfSqlValid"

    // When
    val results = sessionTemplateRepository.activateSessionTemplate(sessionTemplateReference)

    // Then
    assertThat(results).isEqualTo(0)
  }

  @Test
  fun `test getSessionTemplateDetails SQL`() {
    // Given
    val sessionTemplateReference = "testToSeeIfSqlValid"

    // When
    val results = sessionTemplateRepository.getSessionTemplateDetails(sessionTemplateReference)

    // Then
    assertThat(results).isNull()
  }

  @Test
  fun `test activateSessionTemplatesForSlot SQL`() {
    // Given
    val prisonerCode = "BRL"
    val validFrom = LocalDate.now()
    val validTo = validFrom.plusDays(1)
    val dayOfWeek = SATURDAY.name
    val slotStartTime = LocalTime.now()
    val slotEndTime = slotStartTime.plusHours(1)

    // When
    val results = sessionTemplateRepository.activateSessionTemplatesForSlot(
      prisonerCode,
      validFrom,
      validTo,
      dayOfWeek,
      slotStartTime,
      slotEndTime,
    )

    // Then
    assertThat(results).isEqualTo(0)
  }

  @Test
  fun `test deActiveSessionTemplatesForSlot SQL`() {
    // Given
    val prisonerCode = "BRL"
    val validFrom = LocalDate.now()
    val validTo = validFrom.plusDays(1)
    val dayOfWeek = SATURDAY.name
    val slotStartTime = LocalTime.now()
    val slotEndTime = slotStartTime.plusHours(1)

    // When
    val results = sessionTemplateRepository.deActiveSessionTemplatesForSlot(
      prisonerCode,
      validFrom,
      validTo,
      dayOfWeek,
      slotStartTime,
      slotEndTime,
    )

    // Then
    assertThat(results).isEqualTo(0)
  }
}
