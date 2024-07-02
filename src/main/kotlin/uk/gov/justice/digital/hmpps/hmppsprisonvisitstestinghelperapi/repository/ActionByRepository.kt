package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Note: As we connect to the visit-scheduler database directly, we have to put a fake entity in the JpaRepository interface.
 * That is why we define a class NotUsedActionedByEntity and give that to the JpaRepository<NotUsedActionedByEntity, Long>.
 * We then write our own SQL statements to query what we need.
 **/

@Entity
class NotUsedActionedByEntity {
  @Id
  private var id: Long = 0
}

@Repository
interface ActionedByRepository : JpaRepository<NotUsedEventAuditEntity, Long> {

  @Modifying
  @Query(
    "DELETE FROM actioned_by " +
      " WHERE actioned_by.id not IN (" +
      "    SELECT actioned_by_id FROM event_audit group by actioned_by_id " +
      " ) ",
    nativeQuery = true,
  )
  fun deleteUnused(): Int
}
