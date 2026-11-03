# Sprint 2 Review

## Date

08 Oct 2026

## Attendees

- Zitong
- Henry
- Callum
- Yihan
- Ray

## Sprint Goal

Complete the remaining core SkillConnect features, migrate the application from H2 to MySQL, establish the CI pipeline, begin containerising the application, and implement the approved enhancement feature.

## Completed vs Planned

| Planned Item | Result | Evidence / Notes |
|---|---|---|
| Service listing details page | Completed | Users can open a service listing and view its full details. |
| Booking history feature | Completed | Seekers and providers can view their relevant booking history. |
| Booking system page | Partially completed | A pull request has been created, but the feature is not considered complete until it is reviewed, merged, and tested. |
| CI pipeline | Completed | The pipeline runs backend tests, frontend linting, and the frontend production build. It was also verified locally using `act`. |
| MySQL migration | Partially completed | The MySQL driver and configuration have been added, but database connection and MySQL integration-test issues still need to be resolved. |
| Docker and Docker Compose | In progress | Container configuration is being developed and has not yet met the Definition of Done. |
| Extension feature | In progress | The feature requires further implementation, testing, demonstration, and ADR documentation. |
| Enabler and additional user stories | Completed | Stories for CI, Docker, database migration, the extension, and remaining Sprint 1 work were added to the backlog. |
| Remaining Sprint 1 core features | In progress | Some incomplete core features were reassigned to Sprint 2 and are still being implemented. |

## Demo Notes

During the Sprint Review, the team demonstrated:

1. The completed service listing details page.
2. The booking history functionality for seekers and providers.
3. The CI pipeline, including:
   - Backend Maven tests
   - Frontend lint checks
   - Frontend production build
   - Local CI execution using `act`
4. The current MySQL configuration and database persistence, where available.
5. The current progress of the booking system page.

Features that were still in progress were shown only as progress updates and were not presented as completed work

## Definition of Done Review

Completed PBIs were checked against the team’s Definition of Done:

- Implementation completed
- Acceptance criteria satisfied
- Code reviewed and merged
- Automated tests passed
- Frontend lint and build passed
- Documentation updated
- Feature ready for demonstration

The booking system page, Dockerisation, MySQL testing, and extension feature have not yet satisfied every Definition of Done requirement.

## Review Outcome

The Sprint delivered several important core features and established the CI pipeline. However, some planned infrastructure and enhancement work remains incomplete. The team will prioritise the booking workflow, MySQL integration tests, Dockerisation, and the approved extension feature before the final delivery. Commit every funtional code done by Tommorow.