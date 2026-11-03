# Sprint 1 Planning

## Date
24 August 2026

## Sprint Goal
Develop the first working version of SkillConnect's core features.

## Committed User Stories

| User Story | Estimate | Assigned Member |
|---|---:|---|
| Register as a user #2 | TBD | Henry |
| Decline a booking request #23 | TBD | Member |
| View account details #7 | TBD | Callum |
| View incoming booking requests #21 | TBD | Member |
| Request a booking #20 | TBD | Member |
| Admin view users #10 | TBD | Yihan |
| Accept a booking request #22 | TBD | Member |
| User Login #4 | TBD | Henry |
| Seeker profile #5 | TBD | Callum |
| Update account details #9 | TBD | Member |
| Create a provider profile #11 | TBD | Member |
| Delete a service listing #13 | TBD | Member |
| Browse service listings #14 | TBD | Callum |
| Search services by keyword #15 | TBD | Callum |
| Filter services by category #16 | TBD | Callum |
| Filter services by location #17 | TBD | Callum |
| Filter services by price range #18 | TBD | Callum |
| Admin Account Ban Restrictions #33 | TBD | Yihan |
| Update account details #9 | TBD | TingRui |
| Create a provider profile #11 | TBD | TingRui |
| Delete a service listing #13 | TBD | TingRui |


## Task Breakdown

### Register as a User #2
- Create user registration form UI
- Implement registration form validation
- Implement user registration backend
- Implement duplicate email validation
- Test user registration flow

### Decline a Booking Request #23
- Add a decline action for pending booking requests
- Verify that the request belongs to the logged-in provider and is still pending
- Update the booking request status to declined
- Save an optional decline reason
- Display the declined status and reason to the seeker
- Test successful, invalid, and unauthorised decline attempts

### View Account Details #7
- Create backend API to retrieve current user details
- Build account details page in React
- Add access control for account information
- Test account details and access restrictions

### View Incoming Booking Requests #21
- Implement an API to retrieve requests for the logged-in provider
- Build the incoming booking requests page
- Display the seeker, booking date and time, and current status
- Handle empty and failed request states
- Test provider access and booking request display

### Request a Booking #20
- Build the booking request form page
- Display the selected service information
- Validate the required booking details
- Implement the backend API to create a pending booking request
- Test successful and invalid booking requests

### Admin View Users #10
- Build the admin user management page
- Implement an admin-only API to retrieve all users
- Display user account information and account status
- Test admin and non-admin access

### Accept a Booking Request #22
- Add an accept action for pending booking requests
- Verify that the request belongs to the logged-in provider and is still pending
- Update the booking request status to accepted
- Display the accepted status to the seeker
- Test successful, invalid, and unauthorised acceptance attempts

### User Login #4
- Create login page UI
- Implement user authentication
- Implement login error handling
- Implement post-login access and redirect
- Test user login flow

### Seeker Profile #5
- Build the seeker profile page and edit form
- Implement APIs to retrieve and update the seeker profile
- Validate profile updates before saving
- Restrict profile access to the authorised user
- Test profile viewing and editing

### Update Account Details #9
- Create update account details form UI
- Implement account details update backend/API
- Implement account details validation
- Display update success/error messages
- Test account details update flow

### Create a Provider Profile #11
- Build the provider profile form
- Restrict profile creation to authorised providers
- Validate the provider profile fields
- Implement the backend API to save the provider profile
- Display the saved provider profile and submission errors
- Test successful and invalid profile creation

### Delete a Service Listing #13
- Add delete option
- Create delete confirmation
- Handle cancellation
- Prevent unauthorized deletion
- Remove listing from search results
- Test listing deletion

### Browse Service Listings #14
- Create backend function to return available service listings
- Build the service listing page and connect it to the backend
- Display loading, error, and empty-list states
- Test available service listing display

### Search Services by Keyword #15
- Create the keyword search input
- Implement the backend keyword search for service skills
- Connect the search input to the backend query
- Display matching services and a no-results state
- Test valid, empty, and no-result searches

### Filter Services by Category #16
- Create category filter UI
- Implement category filtering logic
- Handle and test category filter results

### Filter Services by Location #17
- Create location filter UI
- Implement location filtering logic
- Handle and test location filter results

### Filter Services by Price Range #18
- Create price range filter UI
- Implement price range filtering logic
- Test price range filtering

### Admin Account Ban Restrictions #33
- Add user selection and ban/unban controls to the user management page
- Implement an admin-only API to update account ban status
- Persist and display the current ban status
- Enforce restrictions for banned accounts
- Test authorised, invalid, and unauthorised ban operations

### Update Account Details #9
- Build in-place profile editing UI
- Show confirmation message after successful save
- Reject invalid input before saving

### Create a provider profile #11
- Design provider profile page layout
- Add missing fields to the provider account model
- Restrict profile creation and access to PRovider role only

### Delete a Service Listing #13
- Add delete option to each service card
- Create delte confirmation dialog
- Handle cancelation

## Notes

The team agreed to complete these stories during Sprint 1.
Team deciced start with ERD for database design by using Miro.
Tasks are tracked through the GitHub Projects board.
Instead of Mysql, we use H2Database to cover database function in Sprint1.
