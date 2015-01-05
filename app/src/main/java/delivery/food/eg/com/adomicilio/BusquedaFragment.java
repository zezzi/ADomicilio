package delivery.food.eg.com.adomicilio;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.Date;

import delivery.food.eg.com.adomicilio.data.ADomicilioContract;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.PlaceEntry;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.CountryEntry;
import delivery.food.eg.com.adomicilio.sync.ADomicilioSyncAdapter;


public class BusquedaFragment extends Fragment implements LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    private PlaceAdapter mPlaceAdapter;
    public static final String LOG_TAG = BusquedaFragment.class.getSimpleName();
    private String mLocation;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;
    private static final String SELECTED_KEY = "selected_position";
    private static final int PLACE_LOADER = 0;


    private static final String[] PLACE_COLUMNS = {

            PlaceEntry.TABLE_NAME + "." + PlaceEntry._ID,
            PlaceEntry.COLUMN_LOC_KEY,
            PlaceEntry.COLUMN_SHORT_DESC,
            PlaceEntry.COLUMN_NAME,
            PlaceEntry.COLUMN_IMAGE_URL,
            PlaceEntry.COLUMN_MIN_ORDER,
            PlaceEntry.COLUMN_TIMEOFDELIVERY,

    };

    public static final int COLUMN_PLACE_ID = 0;
    public static final int COLUMN_LOC_KEY = 1;
    public static final int COLUMN_SHORT_DESC = 2;
    public static final int COLUMN_NAME = 3;
    public static final int COLUMN_IMAGE_URL = 4;
    public static final int COLUMN_MIN_ORDER = 5;
    public static final int COLUMN_TIMEOFDELIVERY = 6;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String placeid);
    }

    public BusquedaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.busquedafragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public void setUseTodayLayout(boolean usePrincipalLayout) {
        usePrincipalLayout = usePrincipalLayout;
        if (mPlaceAdapter != null) {
            mPlaceAdapter.setUseTodayLayout(usePrincipalLayout);
        }
    }

    private void updateWeather() {
        ADomicilioSyncAdapter.syncImmediately(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPlaceAdapter = new PlaceAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_busqueda, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_restaurants);
        mListView.setAdapter(mPlaceAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mPlaceAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback)getActivity())
                            .onItemSelected(cursor.getString(COLUMN_PLACE_ID));
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mPlaceAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLACE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }




    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(PLACE_LOADER, null, this);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID + " ASC";
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri placeForLocationUri = PlaceEntry.buildPlaceCountry(
                mLocation);
        return new CursorLoader(
                getActivity(),
                placeForLocationUri,
                PLACE_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPlaceAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPlaceAdapter.swapCursor(null);
    }





}
