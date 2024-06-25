package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.ApplicationRepository
import java.sql.Timestamp
import java.time.LocalDateTime

@Service
@Transactional
class ApplicationService(
  private val applicationRepository: ApplicationRepository,
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
      }
    }
  }

  fun getApplicationReferenceByVisitId(visitId: Long): String? {
    return applicationRepository.getApplicationReferenceByVisitId(visitId)
  }
}
