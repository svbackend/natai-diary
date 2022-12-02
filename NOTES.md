### Nov 28, 2022

Need to decide upon layout for diary web app. I can use general one, but I could also add custom one for this specific set of pages.

General Layout pros & cons:

Pros:

* Less confusion for users when switching between Stories & Diary pages
* Faster loading time (no need to re-render layout)
* Less code to maintain

Cons:

* Less flexibility - I won't be able to add sticky footer with action button to add new note for example

I don't even know why I'm working on diary if I didn't finish with user flow yet.

---

Decided to switch to auth flow first.
Added registration page and not-verified page (user is redirected to this page after registration).
I would say progress is decent, but I'm feeling like shit today =(

### Nov 27, 2022

Pretty decent progress with landing page, login flow and global state.
Just noticed that today is 1 month since I started to make public notes about progress of this project.
I hope I will continue to work on it consistently. I'm not sure if it's possible to launch MVP this year, but we will see.
For now, I'm going to focus on finishing web version of the app as well as platform for managing content (Stories & Blog posts).

I do really love the way it all works together (symfony backend + openapi + generation of react-query hooks based on docs and nextjs with tailwind)

Next steps:
- Landing page - fix text, maybe add icons for features
- Login flow - add "forgot password", add registration

### Nov 24, 2022

Changed my mind about the generation of react-query hooks,
the fetcher that doing api calls actually not changing after each regeneration of schema, so I'm free to modify it.
Tested everything, and it works really well, I just had to change operationId in openApi for each endpoint to be more readable.

Apart from that I copy-pasted some tailwind templates (so we have main layout, text field, error alert and couple more).
I do really like NextJs. It helps me to build fast (relatively), but it will be even faster when I'll have some ready reusable components.

### Nov 23, 2022

Playing with OpenApi codegen. I like how it generates react-query "use" hooks for me, it's really convenient,
although I'm concerned how I'm going to handle issue when I need to regenerate the schema (when api docs are changed).

I'm also concerned about how I'm going to add authentication to the api calls if "fetcher" is generated, so I cannot modify it.

After some experiments I decided to use codegen only for generating types while react-query hooks will be written manually.

### Nov 20, 2022

Added Logout screen. Improved syncing process.

### Nov 19, 2022

Fixed bugs described in Nov 18 note.

### Nov 18, 2022

Multiuser setup - now works. Decided to filter out notes in ViewModel instead of on database level.

Bugs:
1. Sync - note updated (redundant PUT request sent) even though it's not changed since last time
2. When new note added without authentication - and then user proceeds to sign in - the note that was created not showing up until app restart

### Nov 17, 2022

Somehow was really lazy/unmotivated and tired last couple of days. Didn't even realize that I took 4 days break =(

Today added a small feature so when user logging in - we assign all notes that were without owner to them.

On backend now situation when user already exists handled. Need to do something about it on frontend/android as well.

Next step:

Multiuser setup - fix get notes and test everything.

### Nov 13, 2022

Email verification improvements for UI. Added email verification letter template. Looks OK.

### Nov 10, 2022

Fixed issue with user login (user info wasn't loaded)

Next steps:

- After registration - show screen that user needs to verify email (in progress)
- Handle situation when user with such email already registered.
- Email verification letter template

---

Screen that tells user to verify email kind of works, looks not so good but lets keep it for now

Next steps:

- Handle situation when user with such email already registered.
- Email verification letter template

### Nov 9, 2022

Added "accept terms and conditions checkbox" + created screen for this page.

Next steps:

- fix issue with login after registration - looks like UI not updated accordingly
- After registration - show screen that user needs to verify email (in progress)
- Handle situation when user with such email already registered.
- Email verification letter template

### Nov 8, 2022

Added email verification on backend. Fixed issue with splash screen and overall user data loading.

Next steps:

- On registration screen add "Accept terms" checkbox. + prepare screen with terms.
- After registration - show screen that user needs to verify email
- Email verification letter template


### Nov 7, 2022

Login functionality now works. I still need to display user data somewhere and overall improve the UI. But the login functionality is there.

I do really like how it looks now.

Next steps:

- Fix issue when backend doesn't work splash screen not disappearing
- Finish with registration (test it properly) maybe send email and add screen for user to know that email not verified yet
- Add account page to manage user data (login/registration buttons if not logged in)

### Nov 6, 2022

Today wanted to test login screen on mobile, but needed to setup tunneling via ngrok,
because localhost not accessible from my phone while emulator just can't boot up on my laptop =(
Also I was playing around with generation of http client for android out of OpenApi schema,
it kind of works but I don't see many benefits of doing that, I would prefer to generate kotlin classes for responses
but to use my own http client.

Some progress with login functionality as well.
Logic is following:
1. check whether user already stored in local db  
2. if not - create new user and store it in local db
3. if yes - update user's api token and set this user as currently logged in

### Nov 5, 2022

Feeling not good today, but managed to do login screen on android app and added API Token authenticator on backend.
Because cookies are "same-site" and working only with browser/js, while API Token can be used in mobile apps.
Reading a lot about E2E encryption, trying to wrap my head around it, especially it's hard to understand what to do when new client logging in
and how to share private keys between clients when they are not stored on server.

Next steps:
1. Add link to "Create Account" on login screen
2. Send api request with login/password and store user in db
3. Store api token in preferences

### Oct 31, 2022

Feeling lazy today, will just break down the steps for next task for the future:

1. Create login screen (with email and password) + don't forget to add link to registration screen
2. Handle validation errors
3. Store token & probably user data in the database? Restrict to one user per phone.

### Oct 30, 2022

I think I'm done with backend rewrite (kotlin->php migration), actually it took me not so long.

Also I decided to remove Auth0 and use my own auth implementation, so I have more control over every aspect of it.

Today I installed Anrdoid Studio on my laptop, but unfortunately it's too old to handle heavy android emulation, so I will need to use my phone to test application every time.

So the next steps would be:

1. Upgrade everything what's possible on android application - in progress. Need to fix UI, after update layout looks broken
2. Rewrite auth/registration process to use my own endpoints
3. Rewrite sync endpoints
4. Finish with Insights/Tags tracking screen
5. Test everything properly
6. Release Natai Diary v1.0

New Layout notes - remove left drawer, on bottom bar replace current icons by:

* Home (notes screen)
* Insights (analytics screen)
* Settings


### Oct 29, 2022

UpdateNoteAction - done.
Tests and docs added as well.
It took me an hour to do this endpoint, idk but it seems a bit slow.
I don't want to spend 4 hours on a CRUD endpoints, but on the other hand it does contain tests and proper OpenAPI docs.
And most of the time I spent on fixing validation which is not something that I need to do often.



### Oct 28, 2022

Today I added more tests to DeleteNoteAction and actually that helped me to fix a couple of bugs,
improved OpenAPI docs for this endpoint as well.

Next steps:

- UpdateNoteAction + tests + docs
- SyncNotesAction + tests + docs
- Would be nice to add possibility to authenticate through OpenAPI interface, but not a priority.

### Oct 27, 2022

Last couple of weeks I'm migrating backend from Kotlin (Ktor) to PHP (Symfony) because php ecosystem way better in my opinion, a lot of tools to use that speed up the process, while in Kotlin even such simple things as autowiring with DI are not so easy to implement. Autogeneration of sql migrations also not Kotlin's strongest side, so I decided to move to PHP.

So far I have implemented:
- User registration, login, logout (+ tests and docs)
- Find all notes, create new note, delete note (+ tests and docs)

Next steps:
- Add more tests for Delete Note endpoint + fix docs
- Update note endpoint
- Sync notes with Android app + test it properly
