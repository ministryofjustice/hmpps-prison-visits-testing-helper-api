package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Note: As we connect to the visit-scheduler database directly, we have to put a fake entity in the JpaRepository interface.
 * That is why we define a class NotUsedEventAuditEntity and give that to the JpaRepository<NotUsedEventAuditEntity, Long>.
 * We then write our own SQL statements to query what we need.
 **/

@Entity
class NotUsedEventAuditEntity {
  @Id
  private var id: Long = 0
}

@Repository
interface EventAuditRepository : JpaRepository<NotUsedEventAuditEntity, Long> {

  @Modifying
  @Query("delete from event_audit where booking_reference = :bookingReference", nativeQuery = true)
  fun deleteByBookingReference(bookingReference: String): Int

  @Modifying
  @Query("delete from event_audit where application_reference = :applicationReference", nativeQuery = true)
  fun deleteByApplicationReference(applicationReference: String): Int
}
