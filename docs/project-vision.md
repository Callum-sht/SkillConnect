# Project Vision - [Project Name]

## Project Option
[ SkillConnect ]

## Problem Statement
Our websit people who can provide skills and services with people who need them in their local community. 
Many service providers, such as tutors, photographers, programmers, music teachers, and designers want to delieve their skill but they hardly find the platform. 
And Also, service seekers may struggle to compare available providers based on price, location, availability, and reviews. 
SkillConnect aims to provide one central platform where providers can advertise their services and seekers can search, book, and review them.


## User Roles
| Role | Description |
|---|---|
| [Provider] | A user who can set up their profile, lists skills or services, set the price of sev1, and receives reviews. |
| [Seeker] | A user who searches and browses services, views provider information, submits booking requests, and leaves reviews after completed bookings. |
| [Admin] | A system administrator who manages users and service listings, deactivates accounts, and removes inappropriate content. |

## Core Features
* User Registration and Login: Allow Providers and Seekers to create accounts and securely log in to the system.
* User Profiles: Allow Providers to manage their biography, skills, rates, availability, and portfolio, while Seekers can view their own booking history and reviews they have submitted.
* Booking Management: Allow Seekers to request bookings for a selected date and time, and allow Providers to accept or decline requests.
* Booking History: Allow Providers and Seekers to view their previous and current bookings.
* Reviews and Ratings: Allow Seekers to leave a rating and comment after a completed booking.
* Services listing Management: Allow Providers to manage their skill and services, they can also create, edit, view, and delete service listings.
* Administration: Allow administrators to view, manage and block users and service listings.
* Service Details: Show full listing and provider information to seekers.
* Public Ratings: Display provider-level and listing-level ratings.
* Booking Notifications: Send basic confirmations and reminders.

These are product goals, not all completed features. Sprint 1 established the frontend and backend foundations, but some pages still use mock data. Booking history, reviews and ratings, full service details, listing moderation, notifications, and Docker deployment remain planned for Sprint 2. Login and registration exist, but Spring Security integration is not yet complete.

## Non-Functional Requirements
* The system should be easy to understand and navigate.
* User accounts should be protected with secure login.
* Pages should load within a reasonable time.
* System should process the input validation.
* The application should work correctly on common modern web browsers, including Google Chrome, Microsoft Edge, Firefox, and Safari.
* Core features should have automated tests and a reproducible deployment process before release.

## Assumptions and Constraints
Assumptions
* Bookings will be marked as completed manually; it won't verified automatically.
* he information that provider upload is accurate in their profiles and products.
* The platform serves only local region.
* Providers and seekers arrange the actual service delivery.
 
Out of scope
* Identify verification, background, or qualification validation of providers.
* Dispute resolution or cancellation fee handling between users
* Analise reporting, dashboards, or data export
 
Technical constraints
* Relational database required to support the entity relationships
* Application runs on a single serve
* Sprint 1 uses an in-memory H2 database; data is lost when the backend stops. MySQL remains the original persistent-database target, not the current datasource. See [ADR 0004](architecture/decisions/0004-h2-for-sprint-1.md).
* Database tables and queries are written in SQL and accessed through Spring `JdbcTemplate`, without ORM-based persistence.

## Dependencies
Java
Spring Boot
React.js
Tailwind
Git and GitHub
JUnit
Maven
Mysql (use on Sprint 2)
H2 (current development database)

## Change Log
| Date | Change | Author |
|---|---|---|
| 2026-07-31 | Initial version | YiHan Li |
| 2026-08-03 | 2nd version | Hantao Shen |
| 2026-09-20 | Added Sprint 1 status, remaining features, and current H2 database note | Project team |
