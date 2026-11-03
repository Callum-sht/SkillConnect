# Sprint Planning Notes

**Date of the Meeting:** 18/09/2026 
**Project:** SkillConnect
**Team:** G07-04

## Attendance

| Role | Attended | Absent |
|---|---|---|
| Scrum Master | yes |  |
| Product Owner |  |  |
| Development Team | yes |  |
| Customers (optional) | yes |  |

## Sprint Details

| Sprint Duration | Sprint Number | Start Day | End Day | Total Working Days | Expected Capacity |
|---|---|---|---|---|---|---|
| 3 weeks | 2 | 21/09/2026 | 11/10/2026 | 15 days | 100% |  |

## 1. Team's Goal and Vision for This Sprint

The goal of this sprint is to complete the remaining core features of SkillConnect and prepare the application for deployment. The team will implement a minimal authentication solution with Spring Boot Security, add public service and provider information, strengthen administrative listing management, and package the application with Docker.
The sprint backlog includes the following unfinished features:
- Minimal user authentication using Spring Boot Security, including secure login, password protection, and role-based access for seekers, providers, and administrators.
- Booking history for both seekers and providers.
- Reviews and ratings, allowing seekers to leave a rating and comment after a completed booking.
- Public provider-level and service-listing-level ratings.
- A service detail view containing complete service and provider information.
- Administrative tools to view and manage all service listings and remove inappropriate listings.
- Notifications for booking confirmations and reminders.
- Docker configuration for consistent application deployment.
By the end of the sprint, the potentially shippable product should support the main user journey from authentication and service discovery through booking completion and review submission. Seekers and providers should be able to view their booking histories, users should be able to view detailed service and provider information, and administrators should be able to manage service listings. The application should also provide basic booking notifications and run through Docker.
These items remain incomplete from the previous sprint. The team will carry them into this sprint, divide them into smaller implementation tasks, confirm the frontend and backend API contracts before development, and provide regular progress updates rather than waiting until team meetings. Integration and verification will be completed alongside development so unfinished work can be identified earlier.

## 2. Scope of the Sprint and Estimation

Minimal Spring Boot Security authentication, 5

Booking history for seekers and providers, 8

Reviews and ratings, 3

Service detail view, 8

Administrator listing management, 3

Booking confirmations and reminders, 11

Docker deployment, 11

