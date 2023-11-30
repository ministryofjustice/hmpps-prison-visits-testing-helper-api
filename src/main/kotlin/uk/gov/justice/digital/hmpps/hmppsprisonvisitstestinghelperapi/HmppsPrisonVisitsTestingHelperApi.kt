package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@SecurityScheme(name = "bearerAuth", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
@EnableScheduling
@EnableCaching
open class HmppsPrisonVisitsTestingHelperApi

fun main(args: Array<String>) {
  runApplication<HmppsPrisonVisitsTestingHelperApi>(*args)
}
