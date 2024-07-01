package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Note: As we connect to the visit-scheduler database directly, we have to put a fake entity in the JpaRepository interface.
 * That is why we define a class SessionSlotEntity and give that to the JpaRepository<SessionSlotEntity, Long>.
 * We then write our own SQL statements to query what we need.
 **/

@Entity
class SessionSlotEntity {
  @Id
  private var id: Long = 0
}

@Repository
interface SessionSlotRepository : JpaRepository<SessionSlotEntity, Long> {

  @Modifying
  @Query(
    "DELETE FROM session_slot " +
      " WHERE actioned_by.id not IN (" +
      "    SELECT a.session_slot_id FROM application a group by a.session_slot_id UNION SELECT v.session_slot_id FROM visit v group by v.session_slot_id " +
      " ) ",
    nativeQuery = true,
  )
  fun deleteUnused(): Int
}
