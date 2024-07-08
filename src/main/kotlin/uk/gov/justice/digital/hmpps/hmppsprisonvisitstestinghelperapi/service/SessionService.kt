package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
  ): String {
    logger.debug("createSessionTemplate for slot:$sessionStartDateTime prison:$prisonCode")

    val sessionName = "Test:" + UUID.randomUUID()
    val visitRoom = "$sessionName Room"
    val dayOfWeek = sessionStartDateTime.dayOfWeek
    val sessionTimeSlotDto = SessionTimeSlotDto(startTime = sessionStartDateTime.toLocalTime(), endTime)
    val sessionDateRange = SessionDateRangeDto(validFromDate = slotDate, validToDate = validToDate)
    val groupName = "$sessionName Group"
    val locationReferenceList = mutableListOf<String>()

    locationLevels?.let {
      val levels = locationLevels.split("/").toList()
      val location = PermittedSessionLocationDto(levels[0], levels.getOrNull(1), levels.getOrNull(2), levels.getOrNull(3))
      val createLocationGroup = CreateLocationGroupDto(groupName, prisonCode, listOf(location))
      locationReferenceList.add(visitSchedulerClient.createLocationGroup(createLocationGroup))
    }

    val incentiveReferenceList = mutableListOf<String>()
    incentive?.let {
      val createIncentiveGroup = CreateIncentiveGroupDto(groupName, prisonCode, listOf(incentive))
      incentiveReferenceList.add(visitSchedulerClient.createIncentiveGroup(createIncentiveGroup))
    }

    val categoryReferenceList = mutableListOf<String>()
    category?.let {
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
      locationGroupReferences = locationReferenceList,
      categoryGroupReferences = categoryReferenceList,
      incentiveLevelGroupReferences = incentiveReferenceList,
    )

    val sessionTemplateReference = visitSchedulerClient.creatSessionTemplate(creatSessionTemplate)
    if (disableAllOtherSessionsForSlotAndPrison) {
      sessionTemplateRepository.deActiveSessionTemplatesForSlot(prisonCode, slotDate, validToDate, dayOfWeek.name, sessionTimeSlotDto.startTime, sessionTimeSlotDto.endTime)
    }

    return sessionTemplateReference
  }

  fun deleteSessionTemplate(sessionTemplateReference: String, enableAllOtherSessionsForSlotAndPrison: Boolean) {
    // De active session template to allow it to be deleted
    sessionTemplateRepository.deActivateSessionTemplate(sessionTemplateReference)

    val message = visitSchedulerClient.deleteSessionTemplate(sessionTemplateReference)
    logger.debug(message)

    val sessionTemplatedInfo = sessionTemplateRepository.getSessionTemplateDetails(sessionTemplateReference) ?: throw RuntimeException("Session template does not exist")

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
