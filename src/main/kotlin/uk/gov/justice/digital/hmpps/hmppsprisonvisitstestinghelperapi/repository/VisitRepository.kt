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
 * NotUsedEntity Dummy entity to allow us to use JpaRepository
 */
@Entity
class NotUsedEntity {
  @Id private var id: Long = 0
}

@Repository
interface VisitRepository : JpaRepository<NotUsedEntity, Long> {

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

  @Query(
    "SELECT COUNT(*) > 0 From visit Where reference = :bookingReference AND visit_status = 'BOOKED'",
    nativeQuery = true,
  )
  fun isVisitBooked(bookingReference: String): Boolean

  @Modifying
  @Query(
    "delete from visit_notification_event where booking_reference = :bookingReference",
    nativeQuery = true,
  )
  fun deleteVisitNotificationEvents(bookingReference: String): Int
}
