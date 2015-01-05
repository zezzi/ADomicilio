package delivery.food.eg.com.adomicilio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import delivery.food.eg.com.adomicilio.data.ADomicilioDbHelper;
import delivery.food.eg.com.adomicilio.sync.ADomicilioSyncAdapter;


public class MainActivity2 extends ActionBarActivity implements BusquedaFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    Context mContext;
    private final int COUNTRYURL=1;
    private final int PLACEURL=2;
    private final int MENUURL=3;
    private final int CATEGORYURL=4;

    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        setContentView(R.layout.activity_list_restaurant);
        if (findViewById(R.id.place_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.place_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        BusquedaFragment busquedaFragment =  ((BusquedaFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_place));
        busquedaFragment.setUseTodayLayout(!mTwoPane);
        ADomicilioSyncAdapter.initializeSyncAdapter(this);

        SQLiteDatabase db = new ADomicilioDbHelper(
                mContext).getWritableDatabase();
        db.close();
        //exportDatabse("adomicilio.db");



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void exportDatabse(String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+getPackageName()+"//databases//"+databaseName+"";
                String backupDBPath = "backupdomicilio7.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String placeid) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString(DetailActivity.IDPLACE, placeid);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.place_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.IDPLACE, placeid);
            startActivity(intent);
        }
    }
}
