# Custom Camera

This application has - 
- Firebase integration for authentication and real time Database storage for email and name
- A custom camera which is set as a background service once the user starts the app
- This camera doesn't provide a preview, it autoclicks and updates the UI from a broadcast receiver 
- The image is stored in gallery and files with set tags
- Images with a range of Evs are auto clicked and the best one is sent to the backend/database
- Thus, it automates the user to click multple pictures and sends the appropriate picture without any hassle to backend


To start with the application, clone this project and enter **./gradlew assembleDebug** in CLI
This will build your apk inside gradle like this **app/build/outputs/apk/app-debug.apk**
You can then use this apk to run on emulator or device of your choice.


Prerequisites-
- Android Studio with latest JDK and Android SDK
- Android emulator/device setup
- Gradle
- Firebase
