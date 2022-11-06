### Nov 5, 2022



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
