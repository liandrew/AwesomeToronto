package ca.liandrew.awesometoronto.firebase;

import com.firebase.client.Firebase;

/**
 * Created by liandrew on 2016-05-06.
 */
public class FirebaseFactory {
    private static final String APP_NAME = "https://<YOUR-FIREBASE-APP>.firebaseio.com/";
    private static final Object lock = new Object();
    private static FirebaseFactory instance;
    private FirebaseDatabase firebaseDatabase;

    public static FirebaseFactory getInstance() {
        synchronized (lock) {
            if (instance == null)
                instance = new FirebaseFactory();

            return instance;
        }
    }

    private FirebaseFactory(){
        this.firebaseDatabase = new FirebaseDatabase(APP_NAME);
    }

    public static FirebaseDatabase getFirebaseDatabase(){
        return getInstance().firebaseDatabase;
    }

}
