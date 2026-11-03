# Sprint 2 Test Results

## 1. Scope

This report records pre-submission verification for the functionality delivered in Sprint 1 and Sprint 2. The local verification was performed on 11 October 2026 against commit `77428b0`. The only change made after the verification was this test report.

The verified areas were:

- registration, authentication, sessions and user status management;
- seeker and provider profiles;
- service creation, removal, browsing, searching and filtering;
- booking creation, start/end-time validation, history and status changes;
- reviews and rating validation;
- in-app conversations, messages, read status and access control;
- administrator user management;
- repository persistence and Spring application startup;
- frontend static checks, production build and Docker deployment smoke tests.

## 2. Test Environment

| Item | Version or configuration |
| --- | --- |
| Operating system | macOS |
| Java | Temurin OpenJDK 17.0.14 |
| Backend | Spring Boot 3.3.2, Maven Wrapper, JUnit 5 and Mockito |
| Node.js | 26.8.2 |
| npm | 11.19.1 |
| Database | MySQL 8.4 in an isolated Docker test container |
| Docker | 29.6.2 |
| Test database | `skillconnect_test` on port `13306`; separate from the development database |

## 3. Unit and Integration Test Results

The backend suite compiled 72 application source files and 18 test source files. All 145 tests passed.

| Functional area | Test classes | Tests | Result |
| --- | --- | ---: | --- |
| Authentication, users and administration | `AuthServiceTest`, `AuthControllerTest`, `UserServiceTest`, `AdminUserControllerTest`, `UserStatusPersistenceTest` | 26 | PASS |
| Seekers, providers and service listings | `SeekerServiceTest`, `SeekerPageControllerTest`, `ProviderServiceTest`, `ServiceListingServiceTest` | 42 | PASS |
| Bookings | `BookingServiceTest` | 17 | PASS |
| Reviews and ratings | `ReviewServiceTest` | 6 | PASS |
| Conversations and messages | `ConversationServiceTest`, `ConversationControllerTest`, `MessageServiceTest`, `MessageControllerTest` | 50 | PASS |
| Repository integration | `RepositoryIntegrationTest` | 3 | PASS |
| Spring application context | `WebappApplicationTests` | 1 | PASS |
| **Total** | **17 test classes** | **145** | **PASS** |

Maven summary:

```text
Tests run: 145, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 4. Validation Test Results

| Validation or rule | Evidence | Result |
| --- | --- | --- |
| Registration requires valid fields, rejects a blank name, prevents duplicate email addresses and prevents public registration as an administrator | `AuthServiceTest` | PASS |
| Passwords are hashed and invalid credentials are rejected | `AuthServiceTest`, `AuthControllerTest` | PASS |
| Inactive users cannot sign in and user status changes are persisted | `AuthServiceTest`, `AuthControllerTest`, `UserServiceTest`, `UserStatusPersistenceTest` | PASS |
| A seeker can only read and update their own profile | `SeekerPageControllerTest`, `SeekerServiceTest` | PASS |
| Profile name and biography length limits are enforced | `SeekerServiceTest`, `ProviderServiceTest` | PASS |
| Service listings reject missing titles, invalid prices and unauthorised deletion | `ProviderServiceTest`, `ServiceListingServiceTest` | PASS |
| Booking end date/time is required and must be later than the start date/time | `BookingServiceTest` | PASS |
| Only the correct provider can decline or complete a booking, and only valid state transitions are accepted | `BookingServiceTest` | PASS |
| Reviews are limited to completed bookings, the booking's seeker, one review per booking and ratings within the allowed range | `ReviewServiceTest` | PASS |
| Conversations can only be started with valid seeker/provider participants | `ConversationServiceTest`, `ConversationControllerTest` | PASS |
| Empty messages are rejected and only conversation participants can send, read or mark messages as read | `MessageServiceTest`, `MessageControllerTest` | PASS |

## 5. Acceptance Test Results

These results are supported by the named automated tests or by the Docker smoke test performed on the running application.

| Acceptance scenario | Verification method | Result |
| --- | --- | --- |
| A user can register as a seeker or provider, sign in, maintain a session and sign out | Authentication service and controller tests | PASS |
| A seeker can view/update their profile and find services using keyword and combined filters | Seeker and service-listing tests | PASS |
| A provider can view/update their profile and add or remove their own service listings | Provider service tests | PASS |
| A seeker can create a booking with a valid start and end time, and booking history is returned for seekers and providers | Booking service and repository integration tests | PASS |
| A provider can decline a pending booking with a reason and complete an accepted booking | Booking service and repository integration tests | PASS |
| A seeker can review a completed booking and invalid or duplicate reviews are rejected | Review service tests | PASS |
| A seeker can start a conversation with a provider; both participants can send, retrieve and mark messages as read | Conversation and message service/controller tests | PASS |
| An administrator can list users and update the status of non-administrator accounts | User service, controller and persistence tests | PASS |
| The complete application starts through Docker Compose and the MySQL and frontend containers become healthy | `docker compose ps` | PASS |
| The frontend and the public service API are reachable through the Nginx reverse proxy | HTTP smoke checks for `/` and `/api/services` | PASS |
