package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Note: As we connect to the visit-scheduler database directly, we have to put a fake entity in the JpaRepository interface.
 * That is why we define a class SessionTemplateEntity and give that to the JpaRepository<SessionTemplateEntity, Long>.
 * We then write our own SQL statements to query what we need.
 **/

@Entity
class SessionTemplateEntity {
  @Id
  private var id: Long = 0
}

@Repository
interface SessionTemplateRepository : JpaRepository<SessionTemplateEntity, Long> {

  @Modifying(flushAutomatically = true)
  @Query(
    "UPDATE session_template SET active = false WHERE id IN ( " +
      "SELECT st.id  FROM session_template st " +
      "LEFT JOIN prison p on p.id = st.prison_id " +
      "WHERE st.day_of_week = :dayOfWeek " +
      " AND st.start_time = :slotStartTime " +
      " AND st.end_time = :slotEndTime " +
      " AND st.valid_from_date <= :validFrom " +
      " AND (st.valid_to_date is null OR st.valid_to_date >= :validTo) " +
      " AND st.active = true " +
      " AND p.code = :prisonerCode ) ",
    nativeQuery = true,
  )
  fun deActiveSessionTemplatesForSlot(
    prisonerCode: String,
    validFrom: LocalDate,
    validTo: LocalDate,
    dayOfWeek: String,
    slotStartTime: LocalTime,
    slotEndTime: LocalTime,
  ): Int

  @Modifying(flushAutomatically = true)
  @Query(
    "UPDATE session_template SET active = true WHERE id IN ( " +
      "SELECT st.id  FROM session_template st " +
      "LEFT JOIN prison p on p.id = st.prison_id " +
      "WHERE st.day_of_week = :dayOfWeek " +
      " AND st.start_time = :slotStartTime " +
      " AND st.end_time = :slotEndTime " +
      " AND st.valid_from_date <= :validFrom " +
      " AND (st.valid_to_date is null OR st.valid_to_date >= :validTo) " +
      " AND st.active = false " +
      " AND p.code = :prisonerCode ) ",
    nativeQuery = true,
  )
  fun activateSessionTemplatesForSlot(
    prisonerCode: String,
    validFrom: LocalDate,
    validTo: LocalDate?,
    dayOfWeek: String,
    slotStartTime: LocalTime,
    slotEndTime: LocalTime,
  ): Int

  interface SessionTemplateInfo {
    val dayOfWeek: DayOfWeek
    val startTime: LocalTime
    val endTime: LocalTime
    val validFromDate: LocalDate
    val validToDate: LocalDate?
    val prisonCode: String
  }

  @Query(
    " SELECT st.day_of_week AS dayOfWeek," +
      "st.start_time AS startTime," +
      "st.end_time AS endTime," +
      "st.valid_from_date AS validFromDate," +
      "st.valid_to_date AS validToDate," +
      "p.code AS prisonCode " +
      " FROM session_template st " +
      " LEFT JOIN prison p on p.id = st.prison_id " +
      " WHERE st.reference = :sessionTemplateReference ",
    nativeQuery = true,
  )
  fun getSessionTemplateDetails(sessionTemplateReference: String): SessionTemplateInfo?

  @Modifying(flushAutomatically = true)
  @Query("UPDATE session_template SET active = false WHERE reference = :sessionTemplateReference", nativeQuery = true)
  fun deActivateSessionTemplate(sessionTemplateReference: String): Int

  @Modifying(flushAutomatically = true)
  @Query("UPDATE session_template SET active = true WHERE reference = :sessionTemplateReference", nativeQuery = true)
  fun activateSessionTemplate(sessionTemplateReference: String): Int

  @Query(
    "SELECT slg.reference FROM session_template st " +
      " LEFT JOIN session_to_location_group stlg ON stlg.session_template_id = st.id " +
      " LEFT JOIN session_location_group slg ON slg.id = stlg.group_id " +
      " WHERE st.reference = :sessionTemplateReference",
    nativeQuery = true,
  )
  fun getLocationGroup(sessionTemplateReference: String): String?

  @Query(
    "SELECT sig.reference FROM session_template st " +
      " LEFT JOIN session_to_incentive_group stig ON stig.session_template_id = st.id " +
      " LEFT JOIN session_incentive_group sig ON sig.id = stig.session_incentive_group_id " +
      " WHERE st.reference = :sessionTemplateReference",
    nativeQuery = true,
  )
  fun getIncentiveGroup(sessionTemplateReference: String): String?

  @Query(
    "SELECT scg.reference FROM session_template st " +
      " LEFT JOIN session_to_category_group stcg ON stcg.session_template_id = st.id " +
      " LEFT JOIN session_category_group scg ON scg.id = stcg.session_category_group_id " +
      " WHERE st.reference = :sessionTemplateReference",
    nativeQuery = true,
  )
  fun getCategoryGroup(sessionTemplateReference: String): String?
}
