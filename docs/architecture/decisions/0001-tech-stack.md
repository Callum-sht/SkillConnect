# 0001 — Tech Stack
**Date:** 10-08-2026
**Status:** Accepted

## Context
The project requires a suitable technology stack to develop a web application with a separate frontend and backend. The team needs technologies that support efficient development, maintainability, and integration with a relational database.

## Options Considered
- Option A: React + TypeScript frontend with Java Spring Boot backend
- Option B: Thymeleaf frontend with Java Spring Boot backend


## Decision
We will use React with TypeScript for the frontend and Java Spring Boot for the backend.

## Rationale
React provides a flexible component-based approach for building the user interface, while TypeScript improves code reliability through type checking. Spring Boot provides a structured framework for backend development and supports REST APIs, security, and database integration. This combination also allows the frontend and backend to be developed separately.

## Consequences
- Frontend and backend can be developed and tested independently.
- REST APIs will be required for communication between React and Spring Boot.
- The team needs to manage CORS and API configuration.
- Team members need knowledge of both TypeScript/React and Java/Spring Boot.