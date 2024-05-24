package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitStatus
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.TestDBNotificationEventTypes
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.DBRepository
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class DBService(
  private val dBRepository: DBRepository,
) {

  private val logger: Logger = LoggerFactory.getLogger(dBRepository::class.java)

  fun setVisitStatus(reference: String, status: VisitStatus): Boolean {
    logger.debug("Enter setVisitStatus {} {} ", reference, status)
    val result = dBRepository.setVisitStatus(reference, status.name)
    logger.debug("setVisitStatus result: {}", result)
    return result > 0
  }

  fun updateApplicationModifyTimestamp(reference: String, updatedModifiedTimeStamp: LocalDateTime): Boolean {
    logger.debug("Enter updateModifiedDateApplication {} {} ", reference, updatedModifiedTimeStamp)
    val result = dBRepository.updateApplicationModifyTimestamp(reference, Timestamp.valueOf(updatedModifiedTimeStamp))
    logger.debug("updateModifiedDateApplication result: {}", result)
    return result > 0
  }

  fun isVisitBooked(reference: String): Boolean {
    logger.debug("Enter isVisitBooked, {}", reference)
    val result = dBRepository.isVisitBooked(reference)
    logger.debug("isVisitBooked result: {}", result)
    return result
  }

  fun deleteVisitNotificationEvents(bookingReference: String): Int {
    logger.debug("Delete visit notification events for booking reference - {}", bookingReference)
    val result = dBRepository.deleteVisitNotificationEvents(bookingReference)
    logger.debug("Deleted {} visit notification events for booking reference - {}", result, bookingReference)
    return result
  }

  fun createVisitNotificationEvents(bookingReference: String, notificationType: TestDBNotificationEventTypes) {
    logger.debug("Create visit notification event {} for booking reference - {}", notificationType, bookingReference)
    val reference = UUID.randomUUID().toString()
    dBRepository.createVisitNotificationEvents(bookingReference, notificationType.toString(), reference)
    logger.debug("Created visit notification event {} for booking reference - {}", notificationType, bookingReference)
  }

  fun deleteVisitAndChildren(bookingReference: String) {
    val visitId = dBRepository.getVisitId(bookingReference)
    visitId?.let {
      dBRepository.deleteVisitVisitors(it)
      dBRepository.deleteVisitSupport(it)
      dBRepository.deleteVisitNotes(it)
      dBRepository.deleteVisitContact(it)
      dBRepository.deleteVisitLegacy(it)
      dBRepository.deleteVisit(it)

      val applicationId = dBRepository.getApplicationIdByVisitId(it)
      deleteApplicationAndChildren(applicationId)
    }
  }

  fun deleteApplicationAndChildren(applicationReference: String) {
    val applicationId = dBRepository.getApplicationIdByReference(applicationReference)
    deleteApplicationAndChildren(applicationId)
  }

  private fun deleteApplicationAndChildren(applicationId: Long?) {
    applicationId?.let {
      dBRepository.deleteApplicationVisitors(it)
      dBRepository.deleteApplicationSupport(it)
      dBRepository.deleteApplicationContact(it)
      dBRepository.deleteApplication(it)
    }
  }
}
