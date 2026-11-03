# SkillConnect User Guide

SkillConnect is a local services marketplace that connects people looking for help (**Seekers**) with people offering services (**Providers**). **Administrators** moderate users and service listings.

## 1. Run the application

### Prerequisites

- Docker Desktop with Docker Compose
- Host ports `3000`, `8080`, and `3307` available

### Start SkillConnect

From the repository root:

1. Create the local environment file:

   ```bash
   cp .env.example .env
   ```

2. Open `.env` and replace the example values for `MYSQL_PASSWORD` and `MYSQL_ROOT_PASSWORD` with secure local passwords. These values configure the Docker MySQL and backend containers; credentials from a separately installed MySQL server are not used automatically.

3. Build and start the complete application:

   ```bash
   docker compose up --build -d
   ```

4. Confirm that the frontend, backend, and MySQL services are healthy:

   ```bash
   docker compose ps
   ```

5. Open <http://localhost:3000>.

The frontend is available on port `3000`, the backend API on port `8080`, and Docker MySQL on host port `3307`.

Useful commands:

```bash
# Follow backend logs
docker compose logs -f backend

# Stop the application and keep its database data
docker compose down
```

The `mysql_data` volume preserves database data between normal restarts. Running `docker compose down -v` also deletes this volume and all locally stored accounts, services, bookings, reviews, and messages.

## 2. Accounts and sign-in

1. Select **Create an account**.
2. Enter a full name, unique email address, and password.
3. Choose **Find services** to register as a Seeker or **Offer services** to register as a Provider.
4. Sign in with the registered email and password. SkillConnect redirects the account to the appropriate dashboard.
5. Use the account menu to open the profile, bookings, and **Messages** pages or to sign out.

Administrator accounts cannot be created through public registration. An account must already have the `ADMIN` role before it can use the administration pages.

## 3. Features and usage

### Public features

- Browse the home page, featured services, popular categories, and the application workflow.
- Open a service to view its category, description, location, hourly rate, provider, average rating, and reviews.
- Select the provider's name to view their public profile, availability, portfolio, location, and active services.

### Seeker features

1. **Find services:** Open the Seeker dashboard. Search by service, provider, or category, then filter by category, location, or maximum hourly price.
2. **View service and provider details:** Select a service title to open its details and reviews. Select the provider name to open the provider profile.
3. **Request a booking:** Select **Request booking**, choose a service, start date and time, and end date and time. The end must be after the start. Submit the form to create a `PENDING` request.
4. **Track bookings:** Open **My bookings** to view the service, provider, scheduled time, status, and any decline reason.
5. **Leave a review:** For a `COMPLETED` booking, select **Leave a review**, choose one to five stars, optionally enter a comment, and submit. Each completed booking can be reviewed once, and the new rating contributes to the service's average rating.
6. **Manage the profile:** Open **Profile**, select **Edit**, update the profile information, and save.
7. **Contact a provider:** From a service details page, select **Message Provider**. This creates or opens the conversation with that provider. Select **Messages** from the account menu to return to existing conversations.

### Provider features

1. **Manage the provider profile:** Update the name, bio, availability, portfolio links, and location.
2. **Manage services:** Add a service with a title, category, location, description, and hourly rate. Select the new-category option when the required category is not listed. Existing services can be viewed or deleted.
3. **Handle booking requests:** Open **Booking requests**. Accept a pending request or decline it with an optional reason. Mark an accepted booking as completed after delivering the service.
4. **View booking history:** Open **Bookings** to review current and previous bookings and their statuses.
5. **View seeker details:** Select a seeker's name from a booking request to view the profile and booking history shared with that Provider.
6. **Reply to messages:** Select **Messages** from the provider navigation or account menu, then open the relevant conversation to read and reply to a Seeker.

### In-app messaging

1. A Seeker opens a service details page and selects **Message Provider**. SkillConnect creates a new conversation or reopens the existing conversation with that Provider.
2. The Seeker or Provider selects **Messages** from the navigation or account menu to open the conversation list.
3. The list shows the other participant, latest message, latest activity time, and an unread-message count.
4. Select a conversation, enter text in the message box, and select **Send**. Empty messages cannot be sent.
5. New messages and conversation summaries refresh automatically. Received messages are marked as read when the conversation is viewed.
6. Only the Seeker and Provider participating in a conversation can open it or access its messages.

### Administrator features

1. Sign in with an account that has the `ADMIN` role.
2. Open **User Management** to view registered users, suspend active Seeker or Provider accounts, or reactivate suspended accounts. Administrator accounts cannot be suspended through this page.
3. Open **Service Listings** to view all listings, remove an active listing, or restore a removed listing.

## 4. Booking lifecycle

PENDING --> ACCEPTED --> COMPLETED --> Seeker review
    |
    +------> DECLINED

The Seeker submits a booking with a start and end time. The Provider accepts or declines it. An accepted booking can be marked as completed, after which the Seeker can submit one review.

## 5. Known limitations

- SkillConnect is configured for local demonstration; production hosting, monitoring, backups, and production security configuration are outside the current scope.
- Public registration supports Seeker and Provider accounts only. Administrator accounts must be provisioned separately.
- Password reset and email verification are not implemented.
- Online payments, refunds, invoices, and payment-provider integration are not included.
- Reviews update the rating of the individual service listing; an overall Provider rating is not automatically calculated from all of the Provider's service reviews.
- Messaging supports one-to-one text conversations only; group conversations and file or image attachments are not supported.
- Message updates use short-interval polling rather than a persistent WebSocket connection.
- Providers can add and delete service listings but cannot edit an existing listing in place.
- Automatically repeated booking schedules, such as weekly or monthly bookings, are not supported. Automatic availability checking and external calendar integration are also outside the current scope.
- Running `docker compose down -v` permanently deletes the local Docker database volume.
