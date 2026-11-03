## 1. System Overview

SkillConnect connects local service providers with seekers. Providers, seekers, and admins use a React and TypeScript frontend in the browser. A Java Spring Boot backend supplies REST APIs and uses `JdbcTemplate` and SQL queries with an in-memory H2 database. This database access does not use an ORM. MySQL was the original persistent-database target, but it is not the current datasource; see [ADR 0004](decisions/0004-h2-for-sprint-1.md).

## 2. High-Level Architecture Diagram

Provider / Seeker / Admin
          ↓
React + TypeScript in the browser
          ↓ HTTP /api (JSON)
Spring Boot Controller + DTO
          ↓
Service → Repository (JdbcTemplate + SQL)
                    ↓
             H2 in-memory database

## 3. Component Descriptions

- **Web browser / React frontend:** Displays pages and collects user input. The seeker dashboard currently uses demo service data, although a backend `/api/services` endpoint exists.
- **Controller and DTO:** Receive HTTP requests, validate access where implemented, and return API responses. Login stores the user's ID and role in an HTTP session; logout invalidates it.
- **Service:** Handles business logic for authentication, profiles, listings, bookings, and user management.
- **Repository and model:** Repository classes use `JdbcTemplate` to execute SQL queries and map rows to Java objects. The model represents users, accounts, listings, bookings, and reviews; it is not responsible for ORM persistence.
- **H2 database:** Holds the current development data. Tables are defined by SQL rather than generated from model classes. It is in memory, so data is lost when the backend stops. Persistent database migration remains future work.

## 4. Communication

The browser sends HTTP requests to Spring Boot endpoints under `/api`. During local development, Vite proxies these requests to `localhost:8080`. Controllers call services; services call repositories, which use `JdbcTemplate` to run SQL against H2 and map the results to Java objects. Responses return to the browser as JSON. Requests for seeker and provider profiles use the login session to check the requested user ID and role. The seeker profile is connected to this API, but some other frontend flows, including dashboard listings and provider booking requests, still use mock data. The current session-based login is not a completed Spring Security integration.
