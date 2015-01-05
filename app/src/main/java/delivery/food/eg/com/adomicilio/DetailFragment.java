package delivery.food.eg.com.adomicilio;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import delivery.food.eg.com.adomicilio.data.ADomicilioContract;

/**
 * Created by zezzi on 12/30/14.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String PLACE_SHARE_HASHTAG = " #ADomicilio";
    ImageLoader imageLoader;

    private static final String LOCATION_KEY = "location";
    private MenuAdapter mMenuAdapter;
    private ShareActionProvider mShareActionProvider;
    private String mLocation;
    private String mForecast;
    private String mDateStr;
    private TextView detailpay;
    private TextView detailminimum;
    private TextView detaildeliverycost;
    private TextView namerestaurant;
    private TextView detailtimetodeliver;
    private NetworkImageView logoRestaurant;
    private ListView menuRestaurant;



    private static final int DETAIL_LOADER = 1;

    private static final String[] PLACE_COLUMNS = {

            ADomicilioContract.PlaceEntry.TABLE_NAME + "." + ADomicilioContract.PlaceEntry._ID,
            ADomicilioContract.PlaceEntry.COLUMN_LOC_KEY,
            ADomicilioContract.PlaceEntry.COLUMN_SHORT_DESC,
            ADomicilioContract.PlaceEntry.COLUMN_NAME,
            ADomicilioContract.PlaceEntry.COLUMN_IMAGE_URL,
            ADomicilioContract.PlaceEntry.COLUMN_MIN_ORDER,
            ADomicilioContract.PlaceEntry.COLUMN_TIMEOFDELIVERY,
            ADomicilioContract.PlaceEntry.COLUMN_DATETEXT,
            ADomicilioContract.PlaceEntry.COLUMN_COST_DELIVERY,
            ADomicilioContract.PlaceEntry.COLUMN_PAY_METHOD,
            ADomicilioContract.PlaceEntry.COLUMN_TWITTER,
            ADomicilioContract.PlaceEntry.COLUMN_FACEBOOK,
            ADomicilioContract.PlaceEntry.COLUMN_PLACE_ID,
            ADomicilioContract.CountryEntry.COLUMN_COUNTRY_SETTING
    };

    private static final String[] MENU_COLUMNS = {

            ADomicilioContract.MenuEntry.TABLE_NAME + "." + ADomicilioContract.MenuEntry._ID,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." +  ADomicilioContract.MenuEntry.COLUMN_SHORT_DESC,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." + ADomicilioContract.MenuEntry.COLUMN_NAME,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." + ADomicilioContract.MenuEntry.COLUMN_IMAGE_URL,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." + ADomicilioContract.MenuEntry.COLUMN_CAT_KEY,
            ADomicilioContract.MenuEntry.TABLE_NAME+ "." + ADomicilioContract.MenuEntry.COLUMN_PRICE,
            ADomicilioContract.MenuEntry.TABLE_NAME+"."+ADomicilioContract.MenuEntry.COLUMN_COUN_KEY
    };

    public static final int COLUMN_MENU_ID = 0;
    public static final int COLUMN_SHORTDESC = 1;
    public static final int COLUMN_NAMEMENU = 2;
    public static final int COLUMN_IMAGEURL = 3;
    public static final int COLUMN_CAT_KEY = 4;
    public static final int COLUMN_PRICE = 5;
    public static final int COLUMN_COUNT_KEY=6;


    public DetailFragment() {
        setHasOptionsMenu(true);
        VolleyHelper.init(MyApplication.getAppContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.IDPLACE) &&
                mLocation != null &&
                !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mDateStr = arguments.getString(DetailActivity.IDPLACE);
        }

        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        detailpay=(TextView) rootView.findViewById(R.id.detailpay);
        detailminimum=(TextView) rootView.findViewById(R.id.detailminimum);
        detaildeliverycost=(TextView) rootView.findViewById(R.id.detaildeliverycost);
        namerestaurant=(TextView) rootView.findViewById(R.id.namerestaurant);
        detailtimetodeliver=(TextView) rootView.findViewById(R.id.detailtimetodeliver);
        logoRestaurant=(NetworkImageView) rootView.findViewById(R.id.logoRestaurant);
        menuRestaurant=(ListView)rootView.findViewById(R.id.menuListView);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailmenu, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            Intent seeCart=new Intent(MyApplication.getAppContext(),CartDetail.class);
            seeCart.putExtra("IDPLACE",mDateStr);
            startActivity(seeCart);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.IDPLACE)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    private Intent createShareForecastIntent() {

        Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(),
                R.drawable.icon);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        Uri uriF = null;
        try {
            File f = File.createTempFile("adomicilio", ".jpg", Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS));
            f.deleteOnExit();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(stream.toByteArray());
            fo.close();

            uriF = Uri.fromFile(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriF);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        //shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + PLACE_SHARE_HASHTAG);
        return shareIntent;
    }




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mLocation = Utility.getPreferredLocation(getActivity());
        Log.d("IDPASADO",mDateStr);
        Uri placeForLocationUri = ADomicilioContract.PlaceEntry.getPlaceIdSettingFromUri(mLocation,mDateStr);
        Log.d("URL",placeForLocationUri.toString());

        CursorLoader test=new CursorLoader(
                getActivity(),
                placeForLocationUri,
                PLACE_COLUMNS,
                null,
                null,
                null
        );
        return test;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            String paymehtods=data.getString(data.getColumnIndex(ADomicilioContract.PlaceEntry.COLUMN_PAY_METHOD));
            detailpay.setText(":  "+paymehtods);
            String minimum=data.getString(data.getColumnIndex(ADomicilioContract.PlaceEntry.COLUMN_MIN_ORDER));
            detailminimum.setText(":  "+minimum);
            String deliveryCost=data.getString(data.getColumnIndex(ADomicilioContract.PlaceEntry.COLUMN_COST_DELIVERY));
            detaildeliverycost.setText(":  "+deliveryCost);
            String nameRestaurant=data.getString(data.getColumnIndex(ADomicilioContract.PlaceEntry.COLUMN_NAME));
            namerestaurant.setText(nameRestaurant);
            String timeTodeliver=data.getString(data.getColumnIndex(ADomicilioContract.PlaceEntry.COLUMN_TIMEOFDELIVERY));
            detailtimetodeliver.setText(":  "+timeTodeliver);
            mForecast=nameRestaurant;
            String urlToLoad=data.getString(data.getColumnIndex(ADomicilioContract.PlaceEntry.COLUMN_IMAGE_URL)); ;
            imageLoader=VolleyHelper.getImageLoader();
            logoRestaurant.setImageUrl(urlToLoad, imageLoader);


                Log.d(LOG_TAG, " Uri " + ADomicilioContract.MenuEntry.buildMenuRestaurant(mDateStr));
                Cursor item = getActivity().getApplicationContext().getContentResolver().query(ADomicilioContract.MenuEntry.buildMenuRestaurant(mDateStr), MENU_COLUMNS, null, null, null);
                mMenuAdapter = new MenuAdapter(getActivity(), item, true);
                Log.d(LOG_TAG, " Count " + mMenuAdapter.getCount());
                menuRestaurant.setAdapter(mMenuAdapter);


            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
