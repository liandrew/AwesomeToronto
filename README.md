# AwesomeToronto

This is a sample application that lets users browse tourist attractions in Toronto.  It features a map that lists Bixi Bike locations along with the number of available bikes at each station. 

This sample demonstrates

* user authentication using [Firebase](https://www.firebase.com/)
* using the navigation drawer layout
* listing tourist attractions with RecyclerView
* loading a list of attractions from a plist file using [Android PList parser](https://github.com/tenaciousRas/android-plist-parser)
* handle loading large images using [Picasso](http://square.github.io/picasso/)
* populating map fragment with markers using [Bixi Bike JSON data](http://www.bikesharetoronto.com/stations/json)
* persist favorite attractions between app launches using [SugarORM](http://satyan.github.io/sugar/)

# Getting Started

1. [Download Android Studio](https://developer.android.com/studio/index.html) and import the project. 

2. Create a [Firebase account](https://www.firebase.com/login/)

3. In `FirebaseFactory.java` update `APP_NAME` line with your Firebase app URL

    ```java
    private static final String APP_NAME = "https://<YOUR-FIREBASE-APP>.firebaseio.com/";
    ```

3. Get your Google Maps [API key for Android Maps](https://developers.google.com/maps/documentation/android-api/)

4. Add your key in 'res/values/google_maps_api.xml'

5. Finally, build project and then run using an emulator supporting API 23



