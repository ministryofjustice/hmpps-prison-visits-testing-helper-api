package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.util

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReferenceGenerator {
  fun generateReference(): String {
    return UUID.randomUUID().toString().substring(9, 23)
  }
}
