package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class VisitRepository {

  @PersistenceContext
  private lateinit var entityManager: EntityManager

  fun isVisitBooked(bookingReference: String): Boolean {
    val sql = "SELECT COUNT(*) > 0 From visit Where reference = ? AND visit_status = 'BOOKED' "

    val query = entityManager.createNativeQuery(sql)
    query.setParameter(1, bookingReference)
    return query.singleResult as Boolean
  }
}
