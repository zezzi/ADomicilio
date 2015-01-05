package delivery.food.eg.com.adomicilio.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by zezzi on 1/2/15.
 */
public class ADomicilioSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static ADomicilioSyncAdapter sDomicilioSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("DomicilioService", "onCreate - DomicilioSyncService");
        synchronized (sSyncAdapterLock) {
            if (sDomicilioSyncAdapter == null) {
                sDomicilioSyncAdapter = new ADomicilioSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sDomicilioSyncAdapter.getSyncAdapterBinder();
    }
}
