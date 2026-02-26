package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.CancelVisitDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.OutcomeDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.PrisonExcludeDateDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateCategoryGroupDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateIncentiveGroupDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateLocationGroupDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateSessionTemplateDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.UserType
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.visit.scheduler.BookingRequestDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.visit.scheduler.VisitDto
import java.time.Duration
import java.time.LocalDate

@Component
class VisitSchedulerClient(
  @param:Qualifier("visitSchedulerWebClient") private val webClient: WebClient,
  @param:Qualifier("objectMapper") private val objectMapper: ObjectMapper,
  @param:Value("\${visit-scheduler.api.timeout:10s}") val apiTimeout: Duration,
) {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    val actionedBy: String = "testing-helper-api"
  }

  fun addExcludeDate(prisonCode: String, excludeDate: LocalDate) {
    LOG.info("Calling add exclude date for prison - $prisonCode, excluded date - $excludeDate")
    webClient.put()
      .uri("/prisons/prison/$prisonCode/exclude-date/add")
      .body(BodyInserters.fromValue(PrisonExcludeDateDto(excludeDate, actionedBy)))
      .retrieve()
      .toBodilessEntity()
      .doOnError { e -> LOG.error("Could not add exclude date :", e) }
      .block(apiTimeout)
    LOG.info("Finished calling addExcludeDate for prison - $prisonCode, excluded date - $excludeDate")
  }

  fun removeExcludeDate(prisonCode: String, excludeDate: LocalDate) {
    LOG.info("Calling remove exclude date for prison - $prisonCode, excluded date - $excludeDate")

    webClient.put()
      .uri("/prisons/prison/$prisonCode/exclude-date/remove")
      .body(BodyInserters.fromValue(PrisonExcludeDateDto(excludeDate, actionedBy)))
      .retrieve()
      .toBodilessEntity()
      .doOnError { e -> LOG.error("Could not remove exclude date :", e) }
      .block(apiTimeout)

    LOG.info("Finished calling remove exclude date for prison - $prisonCode, excluded date - $excludeDate")
  }

  fun cancelVisitByBookingReference(reference: String) {
    LOG.info("Calling the visit scheduler to cancel a visit with reference - $reference")

    val body = CancelVisitDto(
      OutcomeDto("CANCELLATION"),
      "testing-helper-api",
      UserType.STAFF,
      "NOT_APPLICABLE",
    )
    webClient.put()
      .uri("/visits/$reference/cancel")
      .body(BodyInserters.fromValue(body))
      .retrieve()
      .toBodilessEntity()
      .doOnError { e -> LOG.error("Could not cancel visit :", e) }
      .block(apiTimeout)

    LOG.info("Finished calling the visit scheduler to cancel a visit with reference - $reference")
  }

  fun bookVisit(applicationReference: String, isRequestBooking: Boolean): VisitDto? {
    LOG.info("Calling the visit scheduler to book a visit for application with reference - $applicationReference")

    val body = BookingRequestDto(
      actionedBy = "testing-helper-api",
      applicationMethodType = "NOT_KNOWN",
      allowOverBooking = true,
      isRequestBooking = isRequestBooking,
    )
    return webClient.put()
      .uri("/visits/$applicationReference/book")
      .body(BodyInserters.fromValue(body))
      .retrieve()
      .bodyToMono<VisitDto>()
      .doOnError { e -> LOG.error("Could not book visit :", e) }
      .block(apiTimeout).also {
        LOG.info("Finished calling the visit scheduler to book a visit for application with reference - $applicationReference")
      }
  }

  fun creatSessionTemplate(creatSessionTemplate: CreateSessionTemplateDto): String {
    LOG.info("Calling the visit scheduler to creatSessionTemplate - ${creatSessionTemplate.name}")

    val jsonValue = webClient.post()
      .uri("/admin/session-templates/template")
      .body(BodyInserters.fromValue(creatSessionTemplate))
      .retrieve()
      .bodyToMono<String>()
      .doOnError { e ->
        LOG.error("Could not create session template:", e)
      }
      .block(apiTimeout)

    LOG.info("Finished calling the visit scheduler to creatSessionTemplate - ${creatSessionTemplate.name}")

    val node = objectMapper.readTree(jsonValue).findValue("reference")
    return if (node != null) node.asText() else ""
  }

  fun createLocationGroup(createLocationGroup: CreateLocationGroupDto): String {
    LOG.info("Calling the visit scheduler to createLocationGroup - ${createLocationGroup.name}")

    val jsonValue = webClient.post()
      .uri("/admin/location-groups/group")
      .body(BodyInserters.fromValue(createLocationGroup))
      .retrieve()
      .bodyToMono<String>()
      .doOnError { e ->
        LOG.error("Could not create location group:", e)
      }
      .block(apiTimeout)

    LOG.info("Finished calling the visit scheduler to createLocationGroup - ${createLocationGroup.name}")

    val node = ObjectMapper().readTree(jsonValue).findValue("reference")
    return if (node != null) node.asText() else ""
  }

  fun createIncentiveGroup(createIncentiveGroupDto: CreateIncentiveGroupDto): String {
    LOG.info("Calling the visit scheduler to createIncentiveGroup - ${createIncentiveGroupDto.name}")

    val jsonValue = webClient.post()
      .uri("/admin/incentive-groups/group")
      .body(BodyInserters.fromValue(createIncentiveGroupDto))
      .retrieve()
      .bodyToMono<String>()
      .doOnError { e ->
        LOG.error("Could not create incentive group:", e)
      }
      .block(apiTimeout)

    LOG.info("Finished calling the visit scheduler to createIncentiveGroup - ${createIncentiveGroupDto.name}")

    val node = ObjectMapper().readTree(jsonValue).findValue("reference")
    return if (node != null) node.asText() else ""
  }

  fun createCategoryGroup(createCategoryGroupDto: CreateCategoryGroupDto): String {
    LOG.info("Calling the visit scheduler to createCategoryGroup - ${createCategoryGroupDto.name}")

    val jsonValue = webClient.post()
      .uri("/admin/category-groups/group")
      .body(BodyInserters.fromValue(createCategoryGroupDto))
      .retrieve()
      .bodyToMono<String>()
      .doOnError { e ->
        LOG.error("Could not create category group:", e)
      }
      .block(apiTimeout)

    LOG.info("Finished calling the visit scheduler to createCategoryGroup - ${createCategoryGroupDto.name}")

    val node = ObjectMapper().readTree(jsonValue).findValue("reference")
    return if (node != null) node.asText() else ""
  }

  fun deleteSessionTemplate(reference: String): String? {
    LOG.info("Delete session template - $reference")

    val message = webClient.delete()
      .uri("/admin/session-templates/template/$reference")
      .retrieve()
      .bodyToMono<String>()
      .doOnError { e ->
        LOG.error("Could not delete session template:", e)
      }
      .block(apiTimeout)

    LOG.info("Delete session template - $reference")

    return message
  }

  fun deleteIncentiveGroup(reference: String): String? {
    LOG.info("Delete incentive group - $reference")

    val message = webClient.delete()
      .uri("/admin/incentive-groups/group/$reference")
      .retrieve()
      .bodyToMono<String>()
      .doOnError { e ->
        LOG.error("Could not delete incentive group:", e)
      }
      .block(apiTimeout)

    LOG.info("Delete incentive groups  - $reference")

    return message
  }

  fun deleteCategoryGroup(reference: String): String? {
    LOG.info("Delete Category Group - $reference")

    val message = webClient.delete()
      .uri("/admin/category-groups/group/$reference")
      .retrieve()
      .bodyToMono<String>()
      .doOnError { e ->
        LOG.error("Could not delete category group:", e)
      }
      .block(apiTimeout)

    LOG.info("Delete Category Group - $reference")

    return message
  }

  fun deleteLocationGroup(reference: String): String? {
    LOG.info("Delete Location Group - $reference")

    val message = webClient.delete()
      .uri("/admin/location-groups/group/$reference")
      .retrieve()
      .bodyToMono<String>()
      .doOnError { e ->
        LOG.error("Could not delete location group:", e)
      }
      .block(apiTimeout)

    LOG.info("Delete Location Group - $reference")

    return message
  }
}
