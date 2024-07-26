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
import java.time.LocalDateTime

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

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "insert into application(prison_id, prisoner_id, session_slot_id, reference, user_type, visit_type, restriction, create_timestamp, modify_timestamp)" +
      " select p.id, :prisonerId, ss.id, :applicationReference, :userType, 'SOCIAL', :visitRestriction, now(), now() from prison p left join session_slot ss " +
      " ON p.id =  ss.prison_id " +
      " where ss.slot_start = :sessionSlotStart " +
      " and ss.slot_end = :sessionSlotEnd " +
      " and p.code = :prisonCode",
    nativeQuery = true,
  )
  fun createApplication(
    prisonCode: String,
    prisonerId: String,
    visitRestriction: String,
    applicationReference: String,
    sessionSlotStart: LocalDateTime,
    sessionSlotEnd: LocalDateTime,
    userType: String,
  ): Int

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "insert into application_visitor(application_id, nomis_person_id) values(:applicationId, :visitorId)",
    nativeQuery = true,
  )
  fun createApplicationVisitor(
    applicationId: Long,
    visitorId: Long,
  ): Int

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "insert into application_contact(application_id, contact_name) values(:applicationId, :contactName)",
    nativeQuery = true,
  )
  fun createApplicationContact(
    applicationId: Long,
    contactName: String,
  ): Int

  @Query(
    "select id from application where reference = :applicationReference",
    nativeQuery = true,
  )
  fun getApplicationId(
    applicationReference: String,
  ): Long?
}
