package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.visit.scheduler.CreateApplicationDto
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.ActionedByRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.ApplicationRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.EventAuditRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.SessionSlotRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.util.ReferenceGenerator
import java.sql.Timestamp
import java.time.LocalDateTime

@Service
@Transactional
class ApplicationService(
  private val applicationRepository: ApplicationRepository,
  private val sessionSlotRepository: SessionSlotRepository,
  private val actionedByRepository: ActionedByRepository,
  private val eventAuditRepository: EventAuditRepository,
  private val referenceGenerator: ReferenceGenerator,
  private val sessionSlotService: SessionSlotService,
) {

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun updateApplicationModifyTimestamp(reference: String, updatedModifiedTimeStamp: LocalDateTime): Boolean {
    logger.debug("Enter updateModifiedDateApplication {} {} ", reference, updatedModifiedTimeStamp)
    val result = applicationRepository.updateApplicationModifyTimestamp(reference, Timestamp.valueOf(updatedModifiedTimeStamp))
    logger.debug("updateModifiedDateApplication result: {}", result)
    return result > 0
  }

  fun changeOpenSessionSlotCapacityForApplication(applicationReference: String, capacity: Int): Boolean {
    logger.debug("ChangeOpenSessionSlotCapacityForApplication - {}", applicationReference)
    val sessionTemplateRef = applicationRepository.getSessionTemplateReferenceFromApplication(applicationReference) ?: throw Exception("Could not find session template for application $applicationReference")
    val rows = applicationRepository.updateOpenSessionTemplateCapacity(sessionTemplateRef, capacity)
    logger.debug("ChangeOpenSessionSlotCapacityForApplication - {} {} {}", sessionTemplateRef, applicationReference, rows)
    return rows > 0
  }

  fun changeClosedSessionSlotCapacityForApplication(applicationReference: String, capacity: Int): Boolean {
    logger.debug("changeClosedSessionSlotCapacityForApplication - {}", applicationReference)
    val sessionTemplateRef = applicationRepository.getSessionTemplateReferenceFromApplication(applicationReference) ?: throw Exception("Could not find session template for application $applicationReference")
    val rows = applicationRepository.updateClosedSessionTemplateCapacity(sessionTemplateRef, capacity)
    logger.debug("changeClosedSessionSlotCapacityForApplication - {} {} {}", sessionTemplateRef, applicationReference, rows)
    return rows > 0
  }

  fun getOpenSessionSlotCapacityForApplication(applicationReference: String): Int {
    logger.debug("getOpenSessionSlotCapacityForApplication - {}", applicationReference)
    val sessionTemplateRef = applicationRepository.getSessionTemplateReferenceFromApplication(applicationReference) ?: throw Exception("Could not find session template for application $applicationReference")
    return applicationRepository.getOpenSessionTemplateCapacity(sessionTemplateRef)
  }

  fun getClosedSessionSlotCapacityForApplication(applicationReference: String): Int {
    logger.debug("getClosedSessionSlotCapacityForApplication - {}", applicationReference)
    val sessionTemplateRef = applicationRepository.getSessionTemplateReferenceFromApplication(applicationReference) ?: throw Exception("Could not find session template for application $applicationReference")
    return applicationRepository.getClosedSessionTemplateCapacity(sessionTemplateRef)
  }

  fun deleteApplicationAndChildren(applicationReference: String?) {
    logger.debug("deleteApplicationAndChildren - {}", applicationReference)

    applicationReference?.let {
      val applicationId = applicationRepository.getApplicationIdByReference(applicationReference)

      applicationId?.let {
        applicationRepository.deleteApplicationVisitors(it)
        applicationRepository.deleteApplicationSupport(it)
        applicationRepository.deleteApplicationContact(it)
        applicationRepository.deleteApplication(it)
        eventAuditRepository.deleteByApplicationReference(applicationReference)
      }
    }

    sessionSlotRepository.deleteUnused()
    actionedByRepository.deleteUnused()
  }

  fun deleteAllPrisonerApplications(prisonerId: String) {
    logger.debug("Deleting all future applications for prisoner - {}", prisonerId)

    val applicationReferences = applicationRepository.getApplicationsByPrisonerId(prisonerId)
    applicationReferences?.forEach { applicationReference ->
      logger.debug("Deleting application with reference - {}", applicationReference)
      deleteApplicationAndChildren(applicationReference)
    }

    logger.debug("Finished deleting all future applications for prisoner - {}", prisonerId)
  }

  fun getApplicationReferenceByVisitId(visitId: Long): String? = applicationRepository.getApplicationReferenceByVisitId(visitId)

  fun createApplication(application: CreateApplicationDto): String {
    val applicationReference = referenceGenerator.generateReference()
    logger.debug("creating application with reference - {}, details - {}", applicationReference, application)

    val sessionSlotId = sessionSlotService.getSessionSlot(
      sessionStart = application.sessionStart,
      sessionEnd = application.sessionEnd,
      sessionDate = application.sessionDate,
      prisonCode = application.prisonCode,
    )

    sessionSlotId?.let {
      applicationRepository.createApplication(
        prisonCode = application.prisonCode,
        prisonerId = application.prisonerId,
        visitRestriction = application.visitRestriction,
        applicationReference = applicationReference,
        sessionSlotStart = application.sessionDate.atTime(application.sessionStart),
        sessionSlotEnd = application.sessionDate.atTime(application.sessionEnd),
        userType = application.userType,
      )

      val applicationId = applicationRepository.getApplicationId(applicationReference)

      applicationId?.let {
        logger.debug("created  application with reference - {}", applicationReference)
        createApplicationVisitors(applicationId, applicationReference, application.visitors)
        createApplicationContact(applicationId, applicationReference, application.contactName)
      } ?: throw RuntimeException("Failed to create application with details - $application")

      logger.debug("completed application creation with reference - {}", applicationReference)
      return applicationReference
    } ?: throw RuntimeException("Failed to create session slot for details - $application, make sure the session template exists")
  }

  fun createApplicationVisitors(applicationId: Long, applicationReference: String, visitors: List<Long>) {
    for (visitorId in visitors) {
      logger.debug("creating  application visitor with visitorId - {} for application reference - {}", visitorId, applicationReference)
      applicationRepository.createApplicationVisitor(applicationId, visitorId)
      logger.debug("created  application visitor with visitorId - {} for application reference - {}", visitorId, applicationReference)
    }
  }

  fun createApplicationContact(applicationId: Long, applicationReference: String, contactName: String) {
    logger.debug("creating main contact with contactName - {} for application reference - {}", contactName, applicationReference)
    applicationRepository.createApplicationContact(applicationId, contactName)
    logger.debug("created main contact with contactName - {} for application reference - {}", contactName, applicationReference)
  }
}
