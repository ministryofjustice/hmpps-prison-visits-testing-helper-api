package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitNoteType
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.dto.enums.VisitStatus
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
class NotUsedEntity {
  @Id
  private var id: Long = 0
}

@Repository
interface TestDBRepository : JpaRepository<NotUsedEntity, Long> {
  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into prison(code, active) values(:prisonCode, true) ",
    nativeQuery = true,
  )
  fun createPrison(
    prisonCode: String,
  ): Int

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into session_template(visit_room, visit_type, open_capacity, closed_capacity, start_time, end_time, valid_from_date, day_of_week, prison_id, name, reference, active) " +
      "select :visitRoom, :visitType, :openCapacity, :closedCapacity, :startTime, :endTime, :validFromDate, :dayOfWeek, id, :name, :reference, true from prison where code = :prisonCode",
    nativeQuery = true,
  )
  fun createSessionTemplate(
    prisonCode: String,
    visitRoom: String,
    visitType: String,
    openCapacity: Int,
    closedCapacity: Int,
    startTime: LocalTime,
    endTime: LocalTime,
    validFromDate: LocalDate,
    dayOfWeek: DayOfWeek,
    name: String,
    reference: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into session_slot(reference, session_template_reference, prison_id, slot_date, slot_start, slot_end) " +
      "select :reference, :sessionTemplateReference, prison_id, :slotDate, :startTime, :endTime from session_template where reference = :sessionTemplateReference",
    nativeQuery = true,
  )
  fun createSessionSlot(
    reference: String,
    slotDate: LocalDate,
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    sessionTemplateReference: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into visit(prison_id, prisoner_id, session_slot_id, reference, visit_type, visit_room, visit_status, visit_restriction) " +
      "select prison_id, :prisonerId, id, :reference, :visitType, :visitRoom, :visitStatus, :visitRestriction from session_slot where reference = :sessionSlotReference",
    nativeQuery = true,
  )
  fun createVisit(
    prisonerId: String,
    reference: String,
    visitType: String,
    visitRoom: String,
    visitStatus: VisitStatus,
    visitRestriction: String,
    sessionSlotReference: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into visit_visitor(visit_id, nomis_person_id, visit_contact) " +
      "select id, :visitorId, :visitContact from visit where reference = :visitReference",
    nativeQuery = true,
  )
  fun createVisitVisitor(
    visitReference: String,
    visitorId: Int,
    visitContact: Boolean,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into visit_support(visit_id, description) " +
      "select id, :description from visit where reference = :visitReference",
    nativeQuery = true,
  )
  fun createVisitSupport(
    visitReference: String,
    description: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into visit_notes(visit_id, type, text) " +
      "select id, :visitNoteType, :description from visit where reference = :visitReference",
    nativeQuery = true,
  )
  fun createVisitNote(
    visitReference: String,
    visitNoteType: VisitNoteType,
    description: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into visit_contact(visit_id, contact_name, contact_phone) " +
      "select id, :contactName, :contactPhone from visit where reference = :visitReference",
    nativeQuery = true,
  )
  fun createVisitContact(
    visitReference: String,
    contactName: String,
    contactPhone: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into application_visitor(application_id, nomis_person_id, visit_contact) " +
      "select id, :visitorId, :visitContact from application where reference = :applicationReference",
    nativeQuery = true,
  )
  fun createApplicationVisitors(
    applicationReference: String,
    visitorId: Int,
    visitContact: Boolean,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into application_support(application_id, description) " +
      "select id, :description from application where reference = :applicationReference",
    nativeQuery = true,
  )
  fun createApplicationSupport(
    applicationReference: String,
    description: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into application_contact(application_id, contact_name, contact_phone) " +
      "select id, :contactName, :contactPhone from application where reference = :applicationReference",
    nativeQuery = true,
  )
  fun createApplicationContact(
    applicationReference: String,
    contactName: String,
    contactPhone: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into application(prison_id, prisoner_id, session_slot_id, reserved_slot, reference, visit_type, restriction, completed, created_by, create_timestamp, modify_timestamp, user_type) " +
      "values (:prisonId, :prisonerId, :sessionSlotId, :reservedSlot, :reference, :visitType, :restriction, :completed, :createdBy, :createdTimestamp, :createdTimestamp, :userType)",
    nativeQuery = true,
  )
  fun createApplication(
    prisonId: Int,
    prisonerId: String,
    sessionSlotId: Int,
    reservedSlot: Boolean,
    reference: String,
    visitType: String,
    restriction: String,
    completed: Boolean,
    createdBy: String,
    createdTimestamp: Timestamp,
    userType: String,
  )

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "insert into visit_notification_event(booking_reference, type, reference) " +
      "values (:visitReference, :type, :reference)",
    nativeQuery = true,
  )
  fun createVisitNotification(
    type: String,
    reference: String,
    visitReference: String,
  )

  @Query(
    "SELECT visit_status from visit where reference = :reference",
    nativeQuery = true,
  )
  fun getVisitStatus(reference: String): String

  @Query(
    "SELECT p.code FROM prison p JOIN visit v ON v.prison_id = p.id WHERE v.reference = :reference",
    nativeQuery = true,
  )
  fun getVisitPrisonCode(reference: String): String

  @Query(
    "SELECT s.id FROM session_slot s WHERE s.reference = :reference",
    nativeQuery = true,
  )
  fun getSessionSlotId(reference: String): Int

  @Query(
    "SELECT modify_timestamp from application where reference = :reference",
    nativeQuery = true,
  )
  fun getApplicationModifiedTimestamp(reference: String): Timestamp

  @Query(
    "select prison_id from session_template where reference = :reference",
    nativeQuery = true,
  )
  fun getPrisonIdFromSessionTemplate(reference: String): Int

  @Query(
    "SELECT count(*) > 0 from visit where reference = :reference",
    nativeQuery = true,
  )
  fun hasVisitWithReference(reference: String): Boolean

  @Query(
    "SELECT count(*) > 0 from application where reference = :reference",
    nativeQuery = true,
  )
  fun hasApplicationWithReference(reference: String): Boolean

  @Query(
    "SELECT count(*) > 0 from visit_notification_event where booking_reference = :bookingReference",
    nativeQuery = true,
  )
  fun hasVisitNotificationsByBookingReference(bookingReference: String): Boolean

  @Query(
    "SELECT count(*) > 0 from visit_visitor where visit_id = :visitId",
    nativeQuery = true,
  )
  fun hasVisitVisitor(visitId: Long): Boolean

  @Query(
    "SELECT count(*) > 0 from visit_support where  visit_id = :visitId",
    nativeQuery = true,
  )
  fun hasVisitSupport(visitId: Long): Boolean

  @Query(
    "SELECT count(*) > 0 from visit_notes where visit_id = :visitId",
    nativeQuery = true,
  )
  fun hasVisitNotes(visitId: Long): Boolean

  @Query(
    "SELECT count(*) > 0 from visit_contact where  visit_id = :visitId",
    nativeQuery = true,
  )
  fun hasVisitContact(visitId: Long): Boolean

  @Query(
    "SELECT count(*) > 0 from application_visitor where application_id = :applicationId",
    nativeQuery = true,
  )
  fun hasApplicationVisitor(applicationId: Long): Boolean

  @Query(
    "SELECT count(*) > 0 from application_support where application_id = :applicationId",
    nativeQuery = true,
  )
  fun hasApplicationSupport(applicationId: Long): Boolean

  @Query(
    "SELECT count(*) > 0 from application_contact where application_id = :applicationId",
    nativeQuery = true,
  )
  fun hasApplicationContact(applicationId: Long): Boolean

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from visit_notification_event",
    nativeQuery = true,
  )
  fun truncateVisitNotificationEvent()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from visit_visitor",
    nativeQuery = true,
  )
  fun truncateVisitVisitor()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from visit_support",
    nativeQuery = true,
  )
  fun truncateVisitSupport()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from visit_notes",
    nativeQuery = true,
  )
  fun truncateVisitNotes()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from visit_contact",
    nativeQuery = true,
  )
  fun truncateVisitContact()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from visit",
    nativeQuery = true,
  )
  fun truncateVisit()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from application_visitor",
    nativeQuery = true,
  )
  fun truncateApplicationVisitors()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from application_support",
    nativeQuery = true,
  )
  fun truncateApplicationSupport()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from application_contact",
    nativeQuery = true,
  )
  fun truncateApplicationContact()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from application",
    nativeQuery = true,
  )
  fun truncateApplication()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from session_to_location_group",
    nativeQuery = true,
  )
  fun truncateSessionToLocationGroup()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from session_slot",
    nativeQuery = true,
  )
  fun truncateSessionSlot()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from session_template",
    nativeQuery = true,
  )
  fun truncateSessionTemplate()

  @Transactional(propagation = REQUIRES_NEW)
  @Modifying
  @Query(
    "delete from prison",
    nativeQuery = true,
  )
  fun truncatePrison()

  @Query(
    "SELECT id from visit where reference = :visitReference",
    nativeQuery = true,
  )
  fun getVisitIdByReference(visitReference: String): Long

  @Query(
    "SELECT id from application where reference = :applicationReference",
    nativeQuery = true,
  )
  fun getApplicationIdByReference(applicationReference: String): Long
}
