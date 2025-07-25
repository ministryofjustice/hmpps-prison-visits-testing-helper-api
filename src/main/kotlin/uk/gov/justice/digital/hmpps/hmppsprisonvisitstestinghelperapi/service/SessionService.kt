package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.client.VisitSchedulerClient
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateCategoryGroupDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateIncentiveGroupDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateLocationGroupDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.CreateSessionTemplateDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.PermittedSessionLocationDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.SessionCapacityDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.SessionDateRangeDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.admin.SessionTimeSlotDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.SessionTemplateRepository
import java.lang.Thread.sleep
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Base64
import java.util.UUID

@Service
@Transactional
class SessionService(
  private val visitSchedulerClient: VisitSchedulerClient,
  private val sessionTemplateRepository: SessionTemplateRepository,
) {

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun createSessionTemplate(
    sessionStartDateTime: LocalDateTime,
    endTime: LocalTime,
    slotDate: LocalDate,
    validToDate: LocalDate,
    prisonCode: String,
    closedCapacity: Int,
    openCapacity: Int,
    weeklyFrequency: Int,
    locationLevels: String?,
    incentive: String?,
    category: String?,
    disableAllOtherSessionsForSlotAndPrison: Boolean,
    customSessionName: String? = null,
  ): String {
    logger.debug(
      "createSessionTemplate for slot:{} prison:{}, slotDate:{}, validToDate: {}, openCapacity: {}, closedCapacity: {}, incentive:{}, category: {}",
      sessionStartDateTime,
      prisonCode,
      slotDate,
      validToDate,
      openCapacity,
      closedCapacity,
      incentive,
      category,
    )

    val sessionTimeSlotDto = SessionTimeSlotDto(startTime = sessionStartDateTime.toLocalTime(), endTime)
    val dayOfWeek = sessionStartDateTime.dayOfWeek
    val sessionName = customSessionName ?: "$dayOfWeek,  ${sessionStartDateTime.toLocalDate()}, ${sessionTimeSlotDto.startTime} (Test)"
    val sessionDateRange = SessionDateRangeDto(validFromDate = slotDate, validToDate = validToDate)
    val locationReferenceList = mutableListOf<String>()
    val visitRoom = "Main test room"
    val group = "test group " + Base64.getEncoder().encode(UUID.randomUUID().toString().encodeToByteArray())

    locationLevels?.let {
      logger.debug("locationLevels provided $locationLevels, calling visit-scheduler to create locationLevels group")
      val levels = locationLevels.split("-").toList()
      val groupName = "$locationLevels $group"
      val location = PermittedSessionLocationDto(levels[0], levels.getOrNull(1), levels.getOrNull(2), levels.getOrNull(3))
      val createLocationGroup = CreateLocationGroupDto(groupName, prisonCode, listOf(location))
      locationReferenceList.add(visitSchedulerClient.createLocationGroup(createLocationGroup))
    }

    val incentiveReferenceList = mutableListOf<String>()
    incentive?.let {
      logger.debug("incentive provided $incentive, calling visit-scheduler to create incentive group")
      val groupName = "$incentive  $group"
      val createIncentiveGroup = CreateIncentiveGroupDto(groupName, prisonCode, listOf(incentive))
      incentiveReferenceList.add(visitSchedulerClient.createIncentiveGroup(createIncentiveGroup))
    }

    val categoryReferenceList = mutableListOf<String>()
    category?.let {
      logger.debug("category provided $category, calling visit-scheduler to create category group")
      val groupName = "$category $group"
      val createCategoryGroup = CreateCategoryGroupDto(groupName, prisonCode, listOf(category))
      categoryReferenceList.add(visitSchedulerClient.createCategoryGroup(createCategoryGroup))
    }

    val creatSessionTemplate = CreateSessionTemplateDto(
      name = sessionName,
      prisonCode = prisonCode,
      sessionTimeSlot = sessionTimeSlotDto,
      sessionDateRange = sessionDateRange,
      visitRoom = visitRoom,
      sessionCapacity = SessionCapacityDto(closed = closedCapacity, open = openCapacity),
      dayOfWeek = dayOfWeek,
      weeklyFrequency = weeklyFrequency,
      includeLocationGroupType = true,
      includeCategoryGroupType = true,
      includeIncentiveGroupType = true,
      locationGroupReferences = locationReferenceList,
      categoryGroupReferences = categoryReferenceList,
      incentiveLevelGroupReferences = incentiveReferenceList,
    )

    val sessionTemplateReference = visitSchedulerClient.creatSessionTemplate(creatSessionTemplate)
    if (disableAllOtherSessionsForSlotAndPrison) {
      sessionTemplateRepository.deActiveSessionTemplatesForSlot(prisonCode, slotDate, validToDate, dayOfWeek.name, sessionTimeSlotDto.startTime, sessionTimeSlotDto.endTime)
    }

    // Adding a sleep here to allow the visit-scheduler to create the session template before attempting to activate it.
    sleep(2000)
    sessionTemplateRepository.activateSessionTemplate(sessionTemplateReference)

    return sessionTemplateReference
  }

  @Transactional(propagation = REQUIRES_NEW)
  fun deActivateSessionTemplate(sessionTemplateReference: String) {
    logger.debug("Enter deActivateSessionTemplate $sessionTemplateReference")
    // De active session template to allow it to be deleted
    val updatedRows = sessionTemplateRepository.deActivateSessionTemplate(sessionTemplateReference)
    logger.debug("ran deActivateSessionTemplate $sessionTemplateReference rows updated $updatedRows")
  }

  @Transactional(propagation = REQUIRES_NEW)
  fun deleteSessionTemplate(sessionTemplateReference: String, enableAllOtherSessionsForSlotAndPrison: Boolean) {
    val sessionTemplatedInfo = sessionTemplateRepository.getSessionTemplateDetails(sessionTemplateReference) ?: throw RuntimeException("Session template does not exist")

    val incentiveGroupRef = sessionTemplateRepository.getIncentiveGroup(sessionTemplateReference)
    val categoryGroupRef = sessionTemplateRepository.getCategoryGroup(sessionTemplateReference)
    val locationGroupRef = sessionTemplateRepository.getLocationGroup(sessionTemplateReference)

    val message = visitSchedulerClient.deleteSessionTemplate(sessionTemplateReference)
    logger.debug("Message from deleteSessionTemplate $message")

    incentiveGroupRef?.let {
      visitSchedulerClient.deleteIncentiveGroup(it)
    }
    categoryGroupRef?.let {
      visitSchedulerClient.deleteCategoryGroup(it)
    }
    locationGroupRef?.let {
      visitSchedulerClient.deleteLocationGroup(it)
    }

    if (enableAllOtherSessionsForSlotAndPrison) {
      with(sessionTemplatedInfo) {
        sessionTemplateRepository.activateSessionTemplatesForSlot(
          prisonCode,
          validFromDate,
          validToDate,
          dayOfWeek.name,
          startTime,
          endTime,
        )
      }
    }
  }
}
