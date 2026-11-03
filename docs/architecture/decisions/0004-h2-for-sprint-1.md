# 0004 — Use H2 for Sprint 1 Development

**Date:** 2026-08-24
**Status:** Accepted for Sprint 1

## Context

[ADR 0002](0002-database.md) selected MySQL as the intended persistent database. During Sprint 1 planning, the team chose H2 to establish and exercise the relational model without first setting up a separate MySQL server. Database access uses `JdbcTemplate` and SQL queries rather than an ORM.

## Options considered

- Set up MySQL immediately for development and testing.
- Use H2 in memory for Sprint 1, then plan a migration to persistent storage.

## Decision

Use H2 in memory for Sprint 1 development and tests. Define the tables in SQL and access them through repository classes using `JdbcTemplate` and explicit queries. Keep MySQL as the original target for persistent storage, but do not describe it as already implemented. Revisit the database choice and migration before deployment.

## Consequences

- Developers can run the application without a separate database installation.
- The SQL schema and repository queries must be maintained explicitly; model changes do not automatically change database tables.
- Data is lost when the backend stops; H2 must not be treated as production persistence.
- H2 and MySQL can differ in SQL behaviour, so persistence and schema behaviour must be checked again when migrating.

This decision records the explicit H2 substitution in the [Sprint 1 planning notes](../../sprint-1/meeting-minutes/sprint1-planning.md) and the data-loss observation in the [Sprint 1 retrospective](../../sprint-1/meeting-minutes/retro-meeting).
