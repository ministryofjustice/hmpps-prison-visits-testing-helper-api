package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper.JwtAuthHelper
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock.HmppsAuthExtension
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.integration.mock.VisitSchedulerMockServer
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.TestDBRepository

@ExtendWith(HmppsAuthExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
abstract class IntegrationTestBase {
  companion object {
    val visitSchedulerMockServer = VisitSchedulerMockServer()

    @BeforeAll
    @JvmStatic
    fun startMocks() {
      visitSchedulerMockServer.start()
    }

    @AfterAll
    @JvmStatic
    fun stopMocks() {
      visitSchedulerMockServer.stop()
    }
  }

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var dBRepository: TestDBRepository

  @Autowired
  protected lateinit var jwtAuthHelper: JwtAuthHelper

  internal fun setAuthorisation(
    user: String = "AUTH_ADM",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf(),
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisation(user, roles, scopes)

  protected fun clearDb() {
    dBRepository.truncateVisitNotificationEvent()
    clearApplication()
    clearVisit()
    clearSession()
    dBRepository.truncatePrison()
  }

  protected fun clearApplication() {
    dBRepository.truncateApplicationVisitors()
    dBRepository.truncateApplicationSupport()
    dBRepository.truncateApplicationContact()
    dBRepository.truncateApplication()
  }

  protected fun clearVisit() {
    dBRepository.truncateVisitVisitor()
    dBRepository.truncateVisitSupport()
    dBRepository.truncateVisitNotes()
    dBRepository.truncateVisitContact()
    dBRepository.truncateVisit()
  }

  protected fun clearSession() {
    dBRepository.truncateSessionToLocationGroup()
    dBRepository.truncateSessionSlot()
    dBRepository.truncateSessionTemplate()
  }
}
