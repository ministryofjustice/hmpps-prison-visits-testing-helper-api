package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Entity
class NotUsedEntity {
  @Id
  private var id: Long = 0
}

@Repository
interface DBRepository : JpaRepository<NotUsedEntity, Long> {

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "UPDATE visit SET visit_status = :status  WHERE reference = :bookingReference",
    nativeQuery = true,
  )
  fun setVisitStatus(
    bookingReference: String,
    status: String,
  ): Int

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "UPDATE application SET modifyTimestamp = :updatedModifiedDate  WHERE reference = :applicationReference",
    nativeQuery = true,
  )
  fun updateApplicationModifyTimestamp(
    applicationReference: String,
    updatedModifiedDate: LocalDateTime,
  ): Int

  @Query(
    "SELECT COUNT(*) > 0 From visit Where reference = :bookingReference AND visit_status = 'BOOKED'",
    nativeQuery = true,
  )
  fun isVisitBooked(bookingReference: String): Boolean

  @Query(
    "Select id from visit where reference = :bookingReference",
    nativeQuery = true,
  )
  fun getVisitId(bookingReference: String): Long?

  @Modifying
  @Query(
    "delete from visit where id = :id",
    nativeQuery = true,
  )
  fun deleteVisit(id: Long): Int

  @Modifying
  @Query(
    "delete from visit_notes where visit_id = :id",
    nativeQuery = true,
  )
  fun deleteVisitNotes(id: Long): Int

  @Modifying
  @Query(
    "delete from visit_support where visit_id = :id",
    nativeQuery = true,
  )
  fun deleteVisitSupport(id: Long): Int

  @Modifying
  @Query(
    "delete from visit_visitor where visit_id = :id",
    nativeQuery = true,
  )
  fun deleteVisitVisitors(id: Long): Int

  @Modifying
  @Query(
    "delete from visit_contact where visit_id = :id",
    nativeQuery = true,
  )
  fun deleteVisitContact(id: Long): Int

  @Modifying
  @Query(
    "delete from legacy_data where visit_id = :id",
    nativeQuery = true,
  )
  fun deleteVisitLegacy(id: Long): Int

  @Query(
    "Select id from application where visit_id = :visitId limit 1",
    nativeQuery = true,
  )
  fun getApplicationIdByVisitId(visitId: Long): Long?

  @Query(
    "Select id from application where reference = :reference",
    nativeQuery = true,
  )
  fun getApplicationIdByReference(reference: String): Long?

  @Query(
    "Select visit_id from application where reference = :reference",
    nativeQuery = true,
  )
  fun getVisitIdByReference(reference: String): Long?

  @Modifying
  @Query(
    "delete from application where id = :id",
    nativeQuery = true,
  )
  fun deleteApplication(id: Long): Int

  @Modifying
  @Query(
    "delete from application_support where application_id = :id",
    nativeQuery = true,
  )
  fun deleteApplicationSupport(id: Long): Int

  @Modifying
  @Query(
    "delete from application_visitor where application_id = :id",
    nativeQuery = true,
  )
  fun deleteApplicationVisitors(id: Long): Int

  @Modifying
  @Query(
    "delete from application_contact where application_id = :id",
    nativeQuery = true,
  )
  fun deleteApplicationContact(id: Long): Int

  @Modifying
  @Query(
    "delete from visit_notification_event where booking_reference = :bookingReference",
    nativeQuery = true,
  )
  fun deleteVisitNotificationEvents(bookingReference: String): Int

  @Modifying
  @Query(
    "insert into visit_notification_event(booking_reference, type, reference) values (:bookingReference, :notificationType, :reference)",
    nativeQuery = true,
  )
  fun createVisitNotificationEvents(bookingReference: String, notificationType: String, reference: String): Int
}
