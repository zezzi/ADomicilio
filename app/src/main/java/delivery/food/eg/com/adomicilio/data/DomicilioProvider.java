package delivery.food.eg.com.adomicilio.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import delivery.food.eg.com.adomicilio.MainActivity;


/**
 * Created by zezzi on 12/22/14.
 */
public class DomicilioProvider extends ContentProvider {


    private final String LOG_TAG = DomicilioProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ADomicilioDbHelper mOpenHelper;

    private static final int PLACE = 100;
    private static final int PLACE_WITH_COUNTRY = 101;
    private static final int PLACE_ID = 102;
    private static final int COUNTRY = 300;
    private static final int COUNTRY_ID = 301;
    private static final int MENU = 500;
    private static final int MENU_WITH_PLACE = 501;
    private static final int MENU_ID = 502;
    private static final int CATEGORY = 700;
    private static final int CATEGORY_WITH_PLACE = 701;
    private static final int CATEGORY_ID = 702;
    private static final int CART=800;
    private static final int CART_ID=801;
    private static final int CART_BY_RESTAURANT=802;

    private static SQLiteQueryBuilder sPlaceByCountrySettingQueryBuilder;
    private static SQLiteQueryBuilder sMenuByPlaceSettingQueryBuilder;
    private static SQLiteQueryBuilder sCategoryByPlaceSettingQueryBuilder;
    private static SQLiteQueryBuilder sMenuinCartBuilder;

    static{
        sPlaceByCountrySettingQueryBuilder = new SQLiteQueryBuilder();
        sPlaceByCountrySettingQueryBuilder.setTables(
                ADomicilioContract.PlaceEntry.TABLE_NAME + " INNER JOIN " +
                        ADomicilioContract.CountryEntry.TABLE_NAME +
                        " ON " + ADomicilioContract.PlaceEntry.TABLE_NAME +
                        "." + ADomicilioContract.PlaceEntry.COLUMN_LOC_KEY +
                        " = " + ADomicilioContract.CountryEntry.TABLE_NAME +
                        "." + ADomicilioContract.CountryEntry._ID);

        sMenuByPlaceSettingQueryBuilder=new SQLiteQueryBuilder();
        sMenuByPlaceSettingQueryBuilder.setTables(
                ADomicilioContract.MenuEntry.TABLE_NAME + " INNER JOIN " +
                        ADomicilioContract.PlaceEntry.TABLE_NAME +
                        " ON " + ADomicilioContract.MenuEntry.TABLE_NAME +
                        "." + ADomicilioContract.MenuEntry.COLUMN_COUN_KEY +
                        " = " + ADomicilioContract.PlaceEntry.TABLE_NAME +
                        "." + ADomicilioContract.PlaceEntry._ID);

        sCategoryByPlaceSettingQueryBuilder=new SQLiteQueryBuilder();
        sCategoryByPlaceSettingQueryBuilder.setTables(
                ADomicilioContract.CategoryEntry.TABLE_NAME + " INNER JOIN " +
                        ADomicilioContract.PlaceEntry.TABLE_NAME +
                        " ON " + ADomicilioContract.CategoryEntry.TABLE_NAME +
                        "." + ADomicilioContract.CategoryEntry.COLUMN_COUN_KEY +
                        " = " + ADomicilioContract.PlaceEntry.TABLE_NAME +
                        "." + ADomicilioContract.PlaceEntry._ID);

        sMenuinCartBuilder= new SQLiteQueryBuilder();
        sMenuinCartBuilder.setTables(
                ADomicilioContract.CartEntry.TABLE_NAME + " INNER JOIN " +
                        ADomicilioContract.MenuEntry.TABLE_NAME +
                        " ON " + ADomicilioContract.CartEntry.TABLE_NAME +
                        "." + ADomicilioContract.CartEntry.COLUMN_MENU +
                        " = " + ADomicilioContract.MenuEntry.TABLE_NAME +
                        "." + ADomicilioContract.MenuEntry._ID);



    }

    private static final String sPlaceSettingSelection =
            ADomicilioContract.PlaceEntry.TABLE_NAME+
                    "." + ADomicilioContract.PlaceEntry._ID + " = ? ";

    private static final String sCountrySettingSelection =
            ADomicilioContract.CountryEntry.TABLE_NAME+
                    "." + ADomicilioContract.CountryEntry.COLUMN_COUNTRY_SETTING + " = ? ";


    private static final String sLocationSettingAndIdSelection =
            ADomicilioContract.CountryEntry.TABLE_NAME +
                    "." + ADomicilioContract.CountryEntry.COLUMN_COUNTRY_SETTING + " = ? AND " +
                    ADomicilioContract.PlaceEntry.TABLE_NAME+"."+ ADomicilioContract.PlaceEntry._ID + " = ? ";


    private static final String sMenuSelection =
            ADomicilioContract.MenuEntry.TABLE_NAME+
                    "." + ADomicilioContract.MenuEntry.COLUMN_COUN_KEY + " = ? ";


    private static final String sCartSelection =
            ADomicilioContract.CartEntry.TABLE_NAME+
                    "." + ADomicilioContract.CartEntry.COLUMN_PLACE_ID + " = ? ";


    @Override
    public boolean onCreate() {
        mOpenHelper = new ADomicilioDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Log.d(LOG_TAG," Query "+ uri.toString()+ "  URI MATCHER "+ sUriMatcher.match(uri));

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "place/*"
            case PLACE_WITH_COUNTRY:
            {
                retCursor = getPlaceByCountrySetting(uri, projection, sortOrder);
                break;
            }
            // "place/#"
            case PLACE_ID: {
                retCursor = getPlaceByCountryIdSetting(uri, projection, sortOrder);
                break;
            }
            // "place"
            case PLACE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "country/#"
            case COUNTRY_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.CountryEntry.TABLE_NAME,
                        projection,
                        ADomicilioContract.CountryEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "country"
            case COUNTRY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.CountryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            //Menu
            case MENU_WITH_PLACE:
            {
                Log.d(LOG_TAG,"GOt to menu with place");
                retCursor = getMenuByPlaceSetting(uri, projection, sortOrder);
                break;
            }
            // "place/#"
            case MENU_ID: {
                Log.d(LOG_TAG,"GOt to menu with id");
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.MenuEntry.TABLE_NAME,
                        projection,
                        ADomicilioContract.MenuEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "place"
            case MENU: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.MenuEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CATEGORY_WITH_PLACE:
            {
                retCursor = getCategoryByPlaceSetting(uri, projection, sortOrder);
                break;
            }
            // "place/#"
            case CATEGORY_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.CategoryEntry.TABLE_NAME,
                        projection,
                        ADomicilioContract.CategoryEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "place"
            case CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "place"
            case CART: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.CartEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CART_BY_RESTAURANT:
            {
                retCursor = getCartByPlaceSetting(uri, projection, sortOrder);
                break;
            }
            // "place/#"
            case CART_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ADomicilioContract.CartEntry.TABLE_NAME,
                        projection,
                        ADomicilioContract.CartEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getCartByPlaceSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = ADomicilioContract.CartEntry.getPlacefromCartUri(uri);
        return sMenuinCartBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sCartSelection,
                new String[]{locationSetting},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPlaceByCountryIdSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = ADomicilioContract.PlaceEntry.getLocationSettingFromUri(uri);
        String idFromUrl = ADomicilioContract.PlaceEntry.getIdFromUri(uri);
       return sPlaceByCountrySettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
               sLocationSettingAndIdSelection,
                new String[]{locationSetting, idFromUrl},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getCategoryByPlaceSetting(Uri uri, String[] projection, String sortOrder) {
        /* select * from Categories where where Categories.COLUMN_COUN_KEY*/
        String placeSetting=ADomicilioContract.CategoryEntry.getPlaceSettingFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection=sPlaceSettingSelection;
        selectionArgs = new String[]{placeSetting};
        return sCategoryByPlaceSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getPlaceByCountrySetting(Uri uri, String[] projection, String sortOrder) {
        String countrySetting = ADomicilioContract.PlaceEntry.getCountrySettingFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = sCountrySettingSelection;
        selectionArgs = new String[]{countrySetting};
        return sPlaceByCountrySettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMenuByPlaceSetting(Uri uri, String[] projection, String sortOrder) {
        String placeSetting=ADomicilioContract.MenuEntry.getPlaceSettingFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection=sMenuSelection;
        Log.d(LOG_TAG," MenuByPlace "+sMenuByPlaceSettingQueryBuilder.toString());
        Log.d(LOG_TAG," Menu selection "+sMenuSelection);
        selectionArgs = new String[]{placeSetting};
        return sMenuByPlaceSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );


    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        Log.d(LOG_TAG, "GetType "+uri.toString()+ " maych "+ match);
        switch (match) {

            case PLACE_WITH_COUNTRY:
                return ADomicilioContract.PlaceEntry.CONTENT_TYPE;
            case PLACE_ID:
                return ADomicilioContract.PlaceEntry.CONTENT_ITEM_TYPE;
            case PLACE:
                return ADomicilioContract.PlaceEntry.CONTENT_TYPE;


            case COUNTRY:
                return ADomicilioContract.CountryEntry.CONTENT_TYPE;
            case COUNTRY_ID:
                return ADomicilioContract.CountryEntry.CONTENT_ITEM_TYPE;


            case MENU_WITH_PLACE:
                return ADomicilioContract.MenuEntry.CONTENT_TYPE;
            case MENU_ID:
                return ADomicilioContract.MenuEntry.CONTENT_ITEM_TYPE;
            case MENU:
                return ADomicilioContract.MenuEntry.CONTENT_TYPE;


            case CATEGORY_WITH_PLACE:
                return ADomicilioContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_ID:
                return ADomicilioContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return ADomicilioContract.CategoryEntry.CONTENT_TYPE;

            case CART_BY_RESTAURANT:
                return ADomicilioContract.CartEntry.CONTENT_TYPE;
            case CART_ID:
                return ADomicilioContract.CartEntry.CONTENT_ITEM_TYPE;
            case CART:
                return ADomicilioContract.CartEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLACE: {
                long _id = db.insert(ADomicilioContract.PlaceEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ADomicilioContract.PlaceEntry.buildPlaceUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MENU: {
                long _id = db.insert(ADomicilioContract.MenuEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ADomicilioContract.MenuEntry.buildMenuUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY: {
                long _id = db.insert(ADomicilioContract.CategoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ADomicilioContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COUNTRY: {
                long _id = db.insert(ADomicilioContract.CountryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ADomicilioContract.CountryEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CART: {
                long _id = db.insert(ADomicilioContract.CartEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ADomicilioContract.CartEntry.buildCartUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case PLACE:
                rowsDeleted = db.delete(
                        ADomicilioContract.PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MENU:
                rowsDeleted = db.delete(
                        ADomicilioContract.MenuEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
                rowsDeleted = db.delete(
                        ADomicilioContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COUNTRY:
                rowsDeleted = db.delete(
                        ADomicilioContract.CountryEntry.TABLE_NAME, selection, selectionArgs);
            case CART:
                rowsDeleted = db.delete(
                        ADomicilioContract.CartEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PLACE:
                rowsUpdated = db.update(ADomicilioContract.PlaceEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MENU:
                rowsUpdated = db.update(ADomicilioContract.MenuEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CATEGORY:
                rowsUpdated = db.update(ADomicilioContract.CategoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case COUNTRY:
                rowsUpdated = db.update(ADomicilioContract.CountryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
            case CART:
                rowsUpdated = db.update(ADomicilioContract.CartEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ADomicilioContract.PlaceEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case COUNTRY:
                db.beginTransaction();
                int returnCount2 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ADomicilioContract.CountryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount2++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount2;
            case MENU:
                db.beginTransaction();
                int returnCount3 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ADomicilioContract.MenuEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount3++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount3;
            case CATEGORY:
                db.beginTransaction();
                int returnCount4 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ADomicilioContract.CategoryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount4++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount4;
            case CART:
                db.beginTransaction();
                int returnCount5 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ADomicilioContract.CartEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount5++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount5;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ADomicilioContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ADomicilioContract.PATH_PLACE, PLACE);
        matcher.addURI(authority, ADomicilioContract.PATH_PLACE + "/*", PLACE_WITH_COUNTRY);
        matcher.addURI(authority, ADomicilioContract.PATH_PLACE + "/*/#", PLACE_ID);

        matcher.addURI(authority, ADomicilioContract.PATH_COUNTRY, COUNTRY);
        matcher.addURI(authority, ADomicilioContract.PATH_COUNTRY + "/#", COUNTRY_ID);

        matcher.addURI(authority, ADomicilioContract.PATH_MENU, MENU);
        matcher.addURI(authority, ADomicilioContract.PATH_MENU + "/#/#", MENU_WITH_PLACE);
        matcher.addURI(authority, ADomicilioContract.PATH_MENU + "/#", MENU_ID);

        matcher.addURI(authority, ADomicilioContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, ADomicilioContract.PATH_CATEGORY + "/#", CATEGORY_WITH_PLACE);
        matcher.addURI(authority, ADomicilioContract.PATH_CATEGORY + "/#", CATEGORY_ID);


        matcher.addURI(authority,ADomicilioContract.PATH_CART,CART);
        matcher.addURI(authority, ADomicilioContract.PATH_CART + "/#/#", CART_BY_RESTAURANT);
        matcher.addURI(authority, ADomicilioContract.PATH_CART + "/#", CART_ID);

        return matcher;
    }
}
