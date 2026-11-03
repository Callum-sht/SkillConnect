# 0003 — Frontend Rendering Approach
**Date:** 10-08-2026
**Status:** Accepted

## Context
The system needs a frontend rendering approach that supports an interactive user interface and works well with the React frontend and Spring Boot backend.

## Options Considered
- Option A: Client-Side Rendering (CSR) with React
- Option C: Server-side templates with Thymeleaf

## Decision
We will use Client-Side Rendering (CSR) with React and TypeScript.

## Rationale
CSR works well with React and allows the frontend to update pages dynamically without requiring the server to render each page. It also supports a clear separation between the React frontend and Spring Boot backend through REST APIs.

## Consequences
- The frontend can provide a more interactive user experience.
- React will handle UI rendering in the browser.
- Spring Boot will mainly provide REST APIs and backend services.
- The frontend and backend need to communicate through HTTP requests.