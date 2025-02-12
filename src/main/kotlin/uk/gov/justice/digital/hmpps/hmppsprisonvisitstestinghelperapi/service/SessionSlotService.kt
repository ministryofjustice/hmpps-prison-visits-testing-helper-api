package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.repository.SessionSlotRepository
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.util.ReferenceGenerator
import java.time.LocalDate
import java.time.LocalTime

@Service
@Transactional
class SessionSlotService(
  private val sessionSlotRepository: SessionSlotRepository,
  private val referenceGenerator: ReferenceGenerator,
) {

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  private fun getSessionSlotId(
    sessionStart: LocalTime,
    sessionEnd: LocalTime,
    sessionDate: LocalDate,
    prisonCode: String,
  ): Long? = sessionSlotRepository.selectSessionSlot(
    startTime = sessionStart,
    endTime = sessionEnd,
    slotDate = sessionDate,
    prisonCode = prisonCode,
    dayOfWeek = sessionDate.dayOfWeek.name,
  )

  private fun createSessionSlot(
    sessionStart: LocalTime,
    sessionEnd: LocalTime,
    sessionDate: LocalDate,
    prisonCode: String,
  ) {
    sessionSlotRepository.createSessionSlot(
      reference = referenceGenerator.generateReference(),
      startTime = sessionStart,
      endTime = sessionEnd,
      slotDate = sessionDate,
      slotStart = sessionDate.atTime(sessionStart),
      slotEnd = sessionDate.atTime(sessionEnd),
      prisonCode = prisonCode,
      dayOfWeek = sessionDate.dayOfWeek.name,
    )
  }

  fun getSessionSlot(
    sessionStart: LocalTime,
    sessionEnd: LocalTime,
    sessionDate: LocalDate,
    prisonCode: String,
  ): Long? {
    val sessionSlotId = getSessionSlotId(sessionStart, sessionEnd, sessionDate, prisonCode)

    if (sessionSlotId == null) {
      logger.debug(
        "session slot for prison - {}, slotDate - {}, start time - {}, end tme - {} does not exist, creating one",
        prisonCode,
        sessionDate,
        sessionStart,
        sessionEnd,
      )

      createSessionSlot(sessionStart, sessionEnd, sessionDate, prisonCode)
    }

    return getSessionSlotId(sessionStart, sessionEnd, sessionDate, prisonCode)
  }
}
