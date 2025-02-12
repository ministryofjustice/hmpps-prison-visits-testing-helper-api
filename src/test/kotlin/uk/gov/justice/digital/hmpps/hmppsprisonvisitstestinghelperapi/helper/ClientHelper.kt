package uk.gov.justice.digital.hmpps.hmppsprisonvisitstestinghelperapi.helper

import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import org.springframework.web.reactive.function.BodyInserters

fun callDelete(
  webTestClient: WebTestClient,
  url: String,
  authHttpHeaders: (HttpHeaders) -> Unit,
): ResponseSpec = webTestClient.delete().uri(url)
  .headers(authHttpHeaders)
  .exchange()

fun callPut(
  bodyValue: Any? = null,
  webTestClient: WebTestClient,
  url: String,
  authHttpHeaders: (HttpHeaders) -> Unit,
): ResponseSpec = if (bodyValue == null) {
  webTestClient.put().uri(url)
    .headers(authHttpHeaders)
    .exchange()
} else {
  webTestClient.put().uri(url)
    .headers(authHttpHeaders)
    .body(BodyInserters.fromValue(bodyValue))
    .exchange()
}

fun callGet(
  webTestClient: WebTestClient,
  url: String,
  authHttpHeaders: (HttpHeaders) -> Unit,
): ResponseSpec = webTestClient.get().uri(url)
  .headers(authHttpHeaders)
  .exchange()
