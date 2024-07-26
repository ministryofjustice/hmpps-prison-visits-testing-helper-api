package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
      " WHERE id not IN (" +
      "    SELECT a.session_slot_id FROM application a group by a.session_slot_id UNION SELECT v.session_slot_id FROM visit v group by v.session_slot_id " +
      " ) ",
    nativeQuery = true,
  )
  fun deleteUnused(): Int

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
    "insert into session_slot(reference, session_template_reference, prison_id, slot_date, slot_start, slot_end)" +
      " select :reference, st.reference, p.id, :slotDate, :slotStart, :slotEnd from session_template st left join prison p  " +
      " ON st.prison_id = p.id " +
      " where st.active = true " +
      " and (st.valid_to_date IS NULL OR st.valid_to_date >= now()) " +
      " and (st.start_time = :startTime) " +
      " and (st.end_time = :endTime) " +
      " and p.code = :prisonCode " +
      " and st.day_of_week = :dayOfWeek",
    nativeQuery = true,
  )
  fun createSessionSlot(
    reference: String,
    startTime: LocalTime,
    endTime: LocalTime,
    slotDate: LocalDate,
    slotStart: LocalDateTime,
    slotEnd: LocalDateTime,
    prisonCode: String,
    dayOfWeek: String,
  ): Int

  @Query(
    " select ss.id from session_slot ss " +
      " left join session_template st ON ss.session_template_reference = st.reference " +
      " left join prison p  ON st.prison_id = p.id " +
      " where st.active = true " +
      " and (st.valid_to_date IS NULL OR st.valid_to_date >= now()) " +
      " and (st.start_time = :startTime) " +
      " and (st.end_time = :endTime) " +
      " and (ss.slot_date = :slotDate) " +
      " and p.code = :prisonCode " +
      " and st.day_of_week = :dayOfWeek",
    nativeQuery = true,
  )
  fun selectSessionSlot(
    startTime: LocalTime,
    endTime: LocalTime,
    slotDate: LocalDate,
    prisonCode: String,
    dayOfWeek: String,
  ): Long?
}
