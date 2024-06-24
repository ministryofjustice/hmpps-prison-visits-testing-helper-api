package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.service.VisitService

const val PING_CONTROLLER_PATH: String = "/ping"

@RestController
class HealthController {

  @Autowired
  lateinit var visitService: VisitService

  @GetMapping("$PING_CONTROLLER_PATH/web")
  @Operation(
    summary = "Dummy rest endpoint",
    description = "Get a response",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Connected to Web",
      ),
    ],
  )
  fun ping(): String {
    return "Connected to Web"
  }

  @GetMapping("$PING_CONTROLLER_PATH/db")
  @Operation(
    summary = "Test db connection",
    description = "This test connects to DB to see if it's setup correctly",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Connected to DB",
      ),
    ],
  )
  fun pingDB(): String {
    visitService.isVisitBooked("haveIGotConnectionToADB")
    return "Connected to DB"
  }
}
