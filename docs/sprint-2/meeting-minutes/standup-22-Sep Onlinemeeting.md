## Date
22 September 2026 - online meeting

## Attendees
- Zitong
- Henry
- Callum
- Yihan
- Ray

## Updates

## What is done
Discuss how Notification System going to be implemented, all the detail in file "0005-extension-feature"
Add additioanl user story for core feature.

## What is next
1 member do Docker and CI, 1 replace H2 to Mysql and old backend code improvement, 2 continue doing extension feature which is Notification System, 1 keep improve and code user story on sprint2 and fix old bug.

## Summary
Clarify what need to be done for extention feature, and add more user story for this.

## Blocker 1：
The team has selected a booking notification system as the proposed enhancement. However, the required delivery channel has not yet been confirmed.

The current registration process accepts an email address but does not verify that the address exists or that the user owns the mailbox. Although email format validation can reject incorrectly formatted addresses, it cannot guarantee successful email delivery.

If email delivery is mandatory, the project may also require an email verification workflow, including verification tokens, expiry handling, confirmation links and resend behaviour. This would significantly increase the implementation and testing scope.

A persistent in-app notification inbox would reliably deliver notifications to authenticated users, but tutor confirmation is required to determine whether this satisfies the enhancement requirement.

## Blocker 2：
The team is considering the Public REST API for listing services as the Sprint 2 enhancement. However, the required functional and non-functional scope has not yet been confirmed.

The existing application already supports internal service listing retrieval, searching and filtering. A basic public API could reuse some existing repository and service logic, so the team needs confirmation that the proposed external read-only API represents sufficient additional scope.

It is also unclear whether the public API is expected to include production-like capabilities such as authentication, rate limiting, pagination, documentation and versioning.

Our proposed minimum scope is a read-only API that allows external applications
to:

- Retrieve a paginated list of active service listings
- Retrieve an individual service listing by ID
- Search and filter listings by keyword, category, location and price
- Receive consistent JSON responses and error messages

Before implementation, could you please confirm whether this is sufficient to
be considered a meaningful enhancement?

Could you also confirm whether the following capabilities are required:

1. API authentication, such as an API key
2. Rate limiting
3. Pagination
4. OpenAPI/Swagger documentation
5. API versioning, such as `/api/public/v1`
6. CORS support for external client applications
7. Write operations, or whether a read-only API is sufficient