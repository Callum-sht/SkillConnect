# 0002 — Database
**Date:** 10-08-2026
**Status:** Accepted

## Context
The system needs persistent storage for user accounts, profiles, listings, bookings, reviews, and other structured data.

## Options Considered
- Option A: MySQL
- Option B: PostgreSQL
- Option C: MongoDB

## Decision
We will use MySQL as the relational database for the system.

## Rationale
MySQL is suitable for structured and relational data and integrates well with Spring Boot. It is widely used, well documented, and familiar to the team.

## Consequences
- Data can be organised using relational tables and relationships.
- Spring JDBC and `JdbcTemplate` can be used to run SQL queries against the relational database.
- Database schemas and relationships need to be designed and maintained.

## Implementation note (2026-09-20)

MySQL is the original persistent-database target, not the current application datasource. Sprint 1 used in-memory H2 with `JdbcTemplate` and explicit SQL instead; see [ADR 0004](0004-h2-for-sprint-1.md). Migration to persistent storage remains future work.
