package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

/**
 * Note: As we connect to the visit-scheduler database directly, we have to put a fake entity in the JpaRepository interface.
 * That is why we define a class NotUsedApplicationEntity and give that to the JpaRepository<NotUsedApplicationEntity, Long>.
 * We then write our own SQL statements to query what we need.
 **/

@Entity
class NotUsedApplicationEntity {
  @Id
  private var id: Long = 0
}

@Repository
interface ApplicationRepository : JpaRepository<NotUsedApplicationEntity, Long> {

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "UPDATE application SET modify_timestamp = :updatedModifiedTimestamp  WHERE reference = :applicationReference",
    nativeQuery = true,
  )
  fun updateApplicationModifyTimestamp(
    applicationReference: String,
    updatedModifiedTimestamp: Timestamp,
  ): Int

  @Query(
    "Select reference from application where visit_id = :visitId limit 1",
    nativeQuery = true,
  )
  fun getApplicationReferenceByVisitId(visitId: Long): String?

  @Query(
    "Select id from application where reference = :reference",
    nativeQuery = true,
  )
  fun getApplicationIdByReference(reference: String): Long?

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

  @Query(
    "SELECT ss.session_template_reference  FROM application a " +
      " LEFT JOIN session_slot ss ON ss.id = a.session_slot_id " +
      " WHERE a.reference = :applicationReference ",
    nativeQuery = true,
  )
  fun getSessionTemplateReferenceFromApplication(applicationReference: String): String?

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "UPDATE session_template set open_capacity = :capacity WHERE reference = :sessionTemplateReference",
    nativeQuery = true,
  )
  fun updateOpenSessionTemplateCapacity(
    sessionTemplateReference: String,
    capacity: Int,
  ): Int

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "UPDATE session_template set closed_capacity = :capacity WHERE reference = :sessionTemplateReference",
    nativeQuery = true,
  )
  fun updateClosedSessionTemplateCapacity(
    sessionTemplateReference: String,
    capacity: Int,
  ): Int

  @Query(
    "SELECT open_capacity FROM session_template WHERE reference = :sessionTemplateReference",
    nativeQuery = true,
  )
  fun getOpenSessionTemplateCapacity(
    sessionTemplateReference: String,
  ): Int

  @Query(
    "SELECT closed_capacity FROM session_template WHERE reference = :sessionTemplateReference",
    nativeQuery = true,
  )
  fun getClosedSessionTemplateCapacity(
    sessionTemplateReference: String,
  ): Int
}
