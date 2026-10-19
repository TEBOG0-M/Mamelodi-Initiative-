# Mamelodi Initiative — student project

## Website
The website is in `website/`. It uses plain HTML, CSS and JavaScript so it can be opened locally without a frontend build step. Open `website/index.html` in a browser or use Visual Studio Code with a local preview extension.

### Current status
- Responsive shared header, mobile navigation and footer.
- Home, mission, programme listing and four programme detail pages.
- News listing and sample article page.
- Volunteer, contact and donation-interest forms. These forms intentionally display a notice instead of pretending to send messages. A backend must be connected before real enquiries can be received.
- Login, registration, password reset and account screens are prepared as interface pages. Firebase is **not configured** in this copy: no real account creation, login, Google sign-in or password reset works until the project Firebase configuration and auth logic are added.
- Draft privacy notice. Review and approve before public use.

### Firebase setup required
Use the same Firebase project as the mobile-app developer. In Firebase Console, register the website as a Web App, enable Email/Password and Google providers as required, and configure authorised domains. Add the web app's config to a dedicated JavaScript module and implement Firebase Authentication methods before using auth pages. Never commit private server credentials. Firebase web config is not a password, but it should still be managed consistently with the project.

### Important before publishing
Replace sample news, confirm programme details, add verified official contact details and donation instructions, and review the privacy notice. No payment processing is included.

## API and mobile app
The existing `api/` and `mobile-app/` folders have been preserved and were not changed as part of this website pass.
