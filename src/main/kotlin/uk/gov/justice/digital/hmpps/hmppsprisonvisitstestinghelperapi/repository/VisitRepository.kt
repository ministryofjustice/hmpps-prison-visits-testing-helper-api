package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional

/**
 * Note: As we connect to the visit-scheduler database directly, we have to put a fake entity in the JpaRepository interface.
 * That is why we define a class NotUsedApplicationEntity and give that to the JpaRepository<NotUsedApplicationEntity, Long>.
 * We then write our own SQL statements to query what we need.
 **/

@Entity
class NotUsedVisitEntity {
  @Id
  private var id: Long = 0
}

@Repository
interface VisitRepository : JpaRepository<NotUsedVisitEntity, Long> {

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "UPDATE visit SET prison_id = (SELECT p.id FROM prison p WHERE p.code = :prisonCode) WHERE reference = :bookingReference",
    nativeQuery = true,
  )
  fun setVisitPrison(
    bookingReference: String,
    prisonCode: String,
  ): Int

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "UPDATE visit SET visit_status = :status, visit_sub_status = :subStatus  WHERE reference = :bookingReference",
    nativeQuery = true,
  )
  fun setVisitStatus(
    bookingReference: String,
    status: String,
    subStatus: String,
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

  @Query(
    "Select reference from visit where prisoner_id = :prisonerId",
    nativeQuery = true,
  )
  fun getVisitsByPrisonerId(prisonerId: String): List<String>?

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

  @Modifying
  @Query(
    "delete from visit_notification_event where booking_reference = :bookingReference",
    nativeQuery = true,
  )
  fun deleteVisitNotificationEventsByBookingReference(bookingReference: String): Int

  @Modifying
  @Query(
    "insert into visit_notification_event(booking_reference, type, reference, visit_id) values (:bookingReference, :notificationType, :reference, :visitId)",
    nativeQuery = true,
  )
  fun createVisitNotificationEvents(bookingReference: String, notificationType: String, reference: String, visitId: Long): Int
}
