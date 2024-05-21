# HMPPS Prison Visits Testing Helper API

[![CircleCI](https://circleci.com/gh/ministryofjustice/hmpps-prison-visits-testing-helper-api/tree/main.svg?style=shield)](https://app.circleci.com/pipelines/github/ministryofjustice/hmpps-prison-visits-testing-helper-api)

This is a Spring Boot application, written in Kotlin. It is a testing helper API for automated tests. Used by [Visits UI](https://github.com/ministryofjustice/hmpps-vsip-ui-tests).


## Building

To build the project (without tests):
```
./gradlew clean build -x test
```

## Testing

Run:
```
./gradlew test 
```

## Running

Create a Spring Boot run configuration with active profile of 'dev'. Run the service in your chosen IDE.

Ports

| Service                                | Port   |  
|----------------------------------------|--------|
| hmpps-prison-visits-testing-helper-api | 8080   |


### Auth token retrieval

To create a Token via curl (local):
```
curl --location --request POST "https://sign-in-dev.hmpps.service.justice.gov.uk/auth/oauth/token?grant_type=client_credentials" --header "Authorization: Basic $(echo -n {Client}:{ClientSecret} | base64)"
```

or via postman collection using the following authorisation urls:
```
Grant type: Client Credentials
Access Token URL: https://sign-in-dev.hmpps.service.justice.gov.uk/auth/oauth/token
Client ID: <get from kubernetes secrets for dev namespace>
Client Secret: <get from kubernetes secrets for dev namespace>
Client Authentication: "Send as Basic Auth Header"
```

Call info endpoint:
```
$ curl 'http://localhost:8083/info' -i -X GET
```

## Swagger v3
Prison Visits Testing Helper API
```
http://localhost:8080/swagger-ui/index.html
```

Export Spec
```
http://localhost:8080/v3/api-docs?group=full-api
```

## Common gradle tasks

To list project dependencies, run:

```
./gradlew dependencies
``` 

To check for dependency updates, run:
```
./gradlew dependencyUpdates --warning-mode all
```

To run an OWASP dependency check, run:
```
./gradlew clean dependencyCheckAnalyze --info
```

To upgrade the gradle wrapper version, run:
```
./gradlew wrapper --gradle-version=<VERSION>
```

To automatically update project dependencies, run:
```
./gradlew useLatestVersions
```

#### Ktlint Gradle Tasks

To run Ktlint check:
```
./gradlew ktlintCheck
```

To run Ktlint format:
```
./gradlew ktlintFormat
```

To apply ktlint styles to intellij
```
./gradlew ktlintApplyToIdea
```

To register pre-commit check to run Ktlint format:
```
./gradlew ktlintApplyToIdea addKtlintFormatGitPreCommitHook 
```

...or to register pre-commit check to only run Ktlint check:
```
./gradlew ktlintApplyToIdea addKtlintCheckGitPreCommitHook
```

#### Using the Testing Helper API locally or against dev to create prison visit events
Although the testing helper API is primarily used by hmpps-vsip-ui-tests it can also be used as a quick way to create SQS events for local testing.

For local envs - 
1. Get orchestration service and visit scheduler running locally.
2. Get the hmpps-prison-visits-testing-helper-api running locally (might need to change port if port clashes with the orchestration service).
3. Get the local queue name using the below command.
```
aws --endpoint-url=http://localhost:4566 sqs list-queues | grep sqs_hmpps_prison_visits_event_queue
```

4. Update the hmpps.sqs.queues.prisonvisitsevents.queue.url value to match the above queue name (if not the same).
5. Connect POSTMAN to your local hmpps-prison-visits-testing-helper-api and create an AuthToken using Client credentials
6. Create requests using the below sample requests for each event type.

For dev / staging env - 
1. Connect POSTMAN to your dev / staging hmpps-prison-visits-testing-helper-api and create an AuthToken using Client credentials 
2. Create requests using the below sample requests for each event type.

#### Sample requests
{{visit_testing_helper_api_url}}/test/prisoner/non-association
```
{
    "prisonerCode": "<PrisonerCode1>",
    "nonAssociationPrisonerCode": "<PrisonerCode2>"
}
```

{{visit_testing_helper_api_url}}/test/prisoner/released
```
{
    "prisonCode": "<PrisonCode>",
    "prisonerCode": "<PrisonerCode>"
}
```

{{visit_testing_helper_api_url}}/test/prisoner/received
```
{
    "prisonCode": "<PrisonCode>",
    "prisonerCode": "<PrisonerCode>"
}
```
