package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

const val CONTROLLER_PATH: String = "/test"

@RestController
class TestingHelperController {

  @GetMapping(CONTROLLER_PATH)
  @Operation(
    summary = "Dummy rest endpoint",
    description = "Get a response",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Worked",
      ),
    ],
  )
  fun processRequest(): String {
    return "hello"
  }
}
