# Learning-Dashboard
Learning Dashboard Using Jetpack compose 

1. Architecture
I used MVVM with a repository layer, which is a clean and common structure for Android. ViewModels expose immutable StateFlow UI state and never reference Android views, so they are easy to unit test. The repository is the single source of truth, so the UI doesn't need to know whether data comes from the network or from Room. Room as the single source of truth means every screen reads from one place, which avoids inconsistent state. Dependencies are passed through a container, so swapping the mock API for Retrofit only changes one line.

2. Offline Support
Room stores courses and lessons. The repository writes API results into Room in one transaction. The UI observes Room through Flows, so it always renders local data first and updates when the network responds. Refresh uses @Upsert for courses and IGNORE for lessons so that a refresh never overwrites the user's progress. Lesson completion is a local write, so it works offline. Limitation: local changes are not pushed to the server, so this is not a full sync engine.

3. Security
Access and refresh tokens should not go into Room, plain SharedPreferences, or logs. I would store them with Jetpack DataStore encrypted by Tink, or EncryptedSharedPreferences, which uses a key held in the Android Keystore. The access token should be short-lived and the refresh token should be rotated on each use. All traffic should use HTTPS, with certificate pinning for high-risk builds. Tokens should be cleared on logout and when the server reports the session as revoked.

4. Scale (1 million users, hundreds of courses)

Paginate the course list with Paging 3 and cache pages in Room, so the app never loads the full catalog at once.
Sync lesson progress to the server with conflict handling, using timestamps or last-write-wins per lesson, and retry failed writes with WorkManager.
Replace manual DI with Hilt and split the app into feature modules so teams can work independently and build times stay manageable.
Cache thumbnails and images with Coil, and set cache size limits.
Add analytics and crash reporting, and use remote config for feature flags and API versioning.
