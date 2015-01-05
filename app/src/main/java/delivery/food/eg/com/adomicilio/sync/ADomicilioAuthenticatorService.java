package delivery.food.eg.com.adomicilio.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by zezzi on 1/2/15.
 */
public class ADomicilioAuthenticatorService  extends Service {
    // Instance field that stores the authenticator object
    private ADomicilioAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new ADomicilioAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}