package delivery.food.eg.com.adomicilio;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import delivery.food.eg.com.adomicilio.data.ADomicilioContract;

/**
 * Created by zezzi on 1/4/15.
 */
public class CartDetail extends ActionBarActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = CartDetail.class.getSimpleName();
    private static final int CART_LOADER = 2;
    private ListView menuRestaurant;
    private TextView sum;
    private String mDateStr;
    private CartAdapter mMenuAdapter;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
       // public void onItemSelected(String placeid);
    }

    private static final String[] MENU_COLUMNS = {

            ADomicilioContract.MenuEntry.TABLE_NAME + "." + ADomicilioContract.MenuEntry._ID,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." +  ADomicilioContract.MenuEntry.COLUMN_SHORT_DESC,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." + ADomicilioContract.MenuEntry.COLUMN_NAME,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." + ADomicilioContract.MenuEntry.COLUMN_IMAGE_URL,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." + ADomicilioContract.MenuEntry.COLUMN_CAT_KEY,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." + ADomicilioContract.MenuEntry.COLUMN_PRICE,
            ADomicilioContract.MenuEntry.TABLE_NAME+"."+ADomicilioContract.MenuEntry.COLUMN_COUN_KEY
    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sendcart) {
            MyApplication.getAppContext().getContentResolver().delete(ADomicilioContract.CartEntry.CONTENT_URI,
                    ADomicilioContract.CartEntry.COLUMN_PLACE_ID + " == ?",
                    new String[] {mDateStr});
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_cart);
        menuRestaurant=(ListView)findViewById(R.id.cartListView);
        mMenuAdapter = new CartAdapter(MyApplication.getAppContext(), null, 0);
        menuRestaurant.setAdapter(mMenuAdapter);
        sum=(TextView)findViewById(R.id.totalduesum);
        Intent intent = getIntent();
        mDateStr = intent.getStringExtra("IDPLACE");
        getSupportLoaderManager().initLoader(CART_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("IDPASADO"," "+ mDateStr);
        Uri item =ADomicilioContract.CartEntry.buildCartRestaurant(mDateStr);
        CursorLoader test=new CursorLoader(
                MyApplication.getAppContext(),
                item,
                MENU_COLUMNS,
                null,
                null,
                null
        );
        return test;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMenuAdapter.swapCursor(data);
        double sumOfData=0;
        while (data.moveToNext()) {
            sumOfData=sumOfData+Double.parseDouble(data.getString(DetailFragment.COLUMN_PRICE).replace("$",""));

        }
        sum.setText("$"+sumOfData);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMenuAdapter.swapCursor(null);
    }
}
