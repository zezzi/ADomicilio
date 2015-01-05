package delivery.food.eg.com.adomicilio.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import delivery.food.eg.com.adomicilio.R;
import delivery.food.eg.com.adomicilio.Utility;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract;

/**
 * Created by zezzi on 1/2/15.
 */
public class ADomicilioSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = ADomicilioSyncAdapter.class.getSimpleName();

    private final String URL_DOMAIN_JSON="http://adomiciliogua.herokuapp.com/";
    private final String URL_COUNTRY=URL_DOMAIN_JSON+"countries.json";
    private final String URL_PLACE=URL_DOMAIN_JSON+"places.json";
    private final String URL_MENU=URL_DOMAIN_JSON+"menus.json";
    private final String URL_CATEGORY=URL_DOMAIN_JSON+"categories.json";
    private final int COUNTRYURL=1;
    private final int PLACEURL=2;
    private final int MENUURL=3;
    private final int CATEGORYURL=4;

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;


    // These are the names of the JSON objects that need to be extracted.
    // Country information
    final String CNTRY_LIST="country";
    final String CNTRY_SETTING = "country_setting";
    final String CNTRY_NAME = "city_name";
    final String CNTRY_ID = "id";


    // Place information.
        /*
        * country_id date places_id short_desc name image_url cost_delivery pay_methods min twitter facebook time_of_delivery
        * */
    final String WHT_LIST="place";
    final String WHT_COUNTRYID = "country_id";
    final String WHT_DATE = "date";
    final String WHT_PLACEID = "places_id";
    final String WHT_DESC = "short_desc";
    final String WHT_NAME = "name";
    final String WHT_IMGURL = "image_url";
    final String WHT_DELIVERY = "cost_delivery";
    final String WHT_PAYMETHODS = "pay_methods";
    final String WHT_MIN = "min";
    final String WHT_TWITTER = "twitter";
    final String WHT_FACEBOOK = "facebook";
    final String WHT_TIME_DELIVERY = "time_of_delivery";

    //MENU
        /*
        * place_id date menu_id short_desc name image_url category_id price
        * */
    final String MNU_LIST="menu";
    final String MNU_PLACEID = "place_id";
    final String MNU_DATE = "date";
    final String MNU_MENUID = "menu_id";
    final String MNU_DESC = "short_desc";
    final String MNU_NAME = "name";
    final String MNU_IMGURL = "image_url";
    final String MNU_CATEGORY = "category_id";
    final String MNU_PRICE = "price";

    /*CATEGORY*/
        /*
        * place_id category_id short_desc name image_url
        * */
    final String CATE_LIST="category";
    final String CATE_PLACEID = "place_id";
    final String CATE_CATEGORYID = "category_id";
    final String CATE_DESC = "short_desc";
    final String CATE_NAME = "name";
    final String CATE_IMGURL = "image_url";


    public ADomicilioSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String locationQuery = Utility.getPreferredLocation(getContext());

        String URL_GENERAL="";
        try {
        Integer urlToGet=1;
        for (int i=1;i<=4;i++){
        urlToGet=i;

        switch (urlToGet){
            case COUNTRYURL:
                URL_GENERAL=URL_COUNTRY;
                break;
            case PLACEURL:
                URL_GENERAL=URL_PLACE;
                break;
            case MENUURL:
                URL_GENERAL=URL_MENU;
                break;
            case CATEGORYURL:
                URL_GENERAL=URL_CATEGORY;
                break;
        }
        //String locationQuery = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String categoryJsonStr = null;
        String menuJsonStr=null;
        String placeJsonStr=null;
        String countryJsonStr=null;

        String format = "json";
        String units = "metric";
        int numDays = 14;

        try {


            Uri builtUri = Uri.parse(URL_GENERAL).buildUpon().build();
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                //return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                //return null;
            }
            switch (urlToGet){
                case COUNTRYURL:
                    countryJsonStr = buffer.toString();
                    break;
                case PLACEURL:
                    placeJsonStr = buffer.toString();
                    break;
                case MENUURL:
                    menuJsonStr = buffer.toString();
                    break;
                case CATEGORYURL:
                    categoryJsonStr = buffer.toString();
                    break;
            }


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            //return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            switch (urlToGet){
                case COUNTRYURL:
                    getDataFromJson(countryJsonStr, COUNTRYURL, "");
                    break;
                case PLACEURL:
                    getDataFromJson(placeJsonStr, PLACEURL, "");
                    break;
                case MENUURL:
                    getDataFromJson(menuJsonStr, MENUURL, "");
                    break;
                case CATEGORYURL:
                    getDataFromJson(categoryJsonStr, CATEGORYURL, "");
                    break;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    return;
        // This will only happen if there was an error getting or parsing the forecast.



    }

    private void getDataFromJson(String infoJsonStr, int typeUrl,
                                 String argsSetting)
            throws JSONException {
        JSONArray countryArray =new JSONArray();
        JSONArray placeArray=new JSONArray() ;
        JSONArray menuArray=new JSONArray();
        JSONArray categoryArray=new JSONArray();
        Vector<ContentValues> cVVectorFinal;

       /* JSONArray backendJson = new JSONArray(infoJsonStr);
        JSONObject test = backendJson.getJSONObject(0);
        JSONObject test2=test.getJSONObject(CNTRY_LIST);
        String Prueba =test2.getString(CNTRY_SETTING);*/
        switch (typeUrl){
            case COUNTRYURL:
                countryArray = new JSONArray(infoJsonStr);
                Vector<ContentValues> cVVector = new Vector<ContentValues>(countryArray.length());
                cVVector=jsonCountry(countryArray);
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);

                    getContext().getContentResolver().bulkInsert(ADomicilioContract.CountryEntry.CONTENT_URI, cvArray);
                }
                break;
            case PLACEURL:
                placeArray= new JSONArray(infoJsonStr);
                Vector<ContentValues> cVVector2 = new Vector<ContentValues>(placeArray.length());
                cVVector2=jsonPlace(placeArray);
                if (cVVector2.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector2.size()];
                    cVVector2.toArray(cvArray);

                    getContext().getContentResolver().bulkInsert(ADomicilioContract.PlaceEntry.CONTENT_URI, cvArray);
                }
                break;
            case MENUURL:
                menuArray=new JSONArray(infoJsonStr);
                Vector<ContentValues> cVVector3 = new Vector<ContentValues>(menuArray.length());
                cVVector3=jsonMenu(menuArray);
                if (cVVector3.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector3.size()];
                    cVVector3.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(ADomicilioContract.MenuEntry.CONTENT_URI, cvArray);
                }
                break;
            case CATEGORYURL:
                categoryArray=new JSONArray(infoJsonStr);
                Vector<ContentValues> cVVector4 = new Vector<ContentValues>(categoryArray.length());
                cVVector4=jsonCategory(categoryArray);
                if (cVVector4.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector4.size()];
                    cVVector4.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(ADomicilioContract.CategoryEntry.CONTENT_URI, cvArray);
                }
                break;
            default:
                break;
        }
    }

    private  Vector<ContentValues> jsonCountry(JSONArray data) throws JSONException{
        Vector<ContentValues> cVVector = new Vector<ContentValues>(data.length());
        String countrySetting="";
        String nameCountry="";

        for (int i = 0; i < data.length(); i++) {
            JSONObject parent=data.getJSONObject(i);
            JSONObject countryDomicilio = parent.getJSONObject(CNTRY_LIST);
            countrySetting = countryDomicilio.getString(CNTRY_SETTING);
            nameCountry = countryDomicilio.getString(CNTRY_NAME);
            ContentValues CountryValues = new ContentValues();
            CountryValues.put(ADomicilioContract.CountryEntry.COLUMN_COUNTRY_SETTING, countrySetting);
            CountryValues.put(ADomicilioContract.CountryEntry.COLUMN_COUNTRY_NAME, nameCountry);
            cVVector.add(CountryValues);
        }
        Log.d(LOG_TAG, "FetchCountryTask Complete. " + cVVector.size() + " Inserted");
        return cVVector;
    }

    private  Vector<ContentValues> jsonPlace(JSONArray data) throws JSONException{
        Vector<ContentValues> cVVector = new Vector<ContentValues>(data.length());
        /*
         * WHT_COUNTRYID
         WHT_DATE
         WHT_PLACEID
         WHT_DESC
         WHT_NAME
         WHT_IMGURL
         WHT_DELIVERY
         WHT_PAYMETHODS
         WHT_MIN
         WHT_TWITTER
         WHT_FACEBOOK
         WHT_TIME_DELIVERY*/
        String placeConunry="";
        String placeDate="";
        int placeId=0;
        String placeDesc="";
        String placeName="";
        String placeIMG="";
        String placeDelivery="";
        String placePaymethods="";
        String placeMin="";
        String placeTwitter="";
        String placeFacebook="";
        String placeTimeOfDelivery="";

        for (int i = 0; i < data.length(); i++) {
            JSONObject parent=data.getJSONObject(i);
            JSONObject placeDomicilio = parent.getJSONObject(WHT_LIST);
            placeConunry = placeDomicilio.getString(WHT_COUNTRYID);
            placeDate = placeDomicilio.getString(WHT_DATE);
            placeId = placeDomicilio.getInt(WHT_PLACEID);
            placeDesc = placeDomicilio.getString(WHT_DESC);
            placeName = placeDomicilio.getString(WHT_NAME);
            placeIMG = placeDomicilio.getString(WHT_IMGURL);
            placeDelivery = placeDomicilio.getString(WHT_DELIVERY);
            placePaymethods = placeDomicilio.getString(WHT_PAYMETHODS);
            placeMin = placeDomicilio.getString(WHT_MIN);
            placeTwitter = placeDomicilio.getString(WHT_TWITTER);
            placeFacebook = placeDomicilio.getString(WHT_FACEBOOK);
            placeTimeOfDelivery = placeDomicilio.getString(WHT_TIME_DELIVERY);

            ContentValues CountryValues = new ContentValues();
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_LOC_KEY, placeConunry);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_DATETEXT, placeDate);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_PLACE_ID, placeId);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_SHORT_DESC, placeDesc);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_NAME, placeName);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_IMAGE_URL, placeIMG);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_COST_DELIVERY, placeDelivery);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_PAY_METHOD, placePaymethods);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_MIN_ORDER, placeMin);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_TWITTER, placeTwitter);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_FACEBOOK, placeFacebook);
            CountryValues.put(ADomicilioContract.PlaceEntry.COLUMN_TIMEOFDELIVERY, placeTimeOfDelivery);
            cVVector.add(CountryValues);
        }
        Log.d(LOG_TAG, "FetchPlaceTask Complete. " + cVVector.size() + " Inserted");
        return cVVector;
    }

    private  Vector<ContentValues> jsonMenu(JSONArray data) throws JSONException{
        Vector<ContentValues> cVVector = new Vector<ContentValues>(data.length());
        /*
        * MNU_PLACEID
          MNU_DATE
          MNU_MENUID
          MNU_DESC
          MNU_NAME
          MNU_IMGURL
          MNU_CATEGORY
          MNU_PRICE
        * */

        int menuPlaceId=0;
        String menuDate="";
        int menuId=0;
        String menuDesc="";
        String menuName="";
        String menuImgrl="";
        String menuCategory="";
        String menuPrice="";
        for (int i = 0; i < data.length(); i++) {
            JSONObject parent=data.getJSONObject(i);
            JSONObject menuDomicilio = parent.getJSONObject(MNU_LIST);
            menuPlaceId = menuDomicilio.getInt(MNU_PLACEID);
            menuDate = menuDomicilio.getString(MNU_DATE);
            menuId = menuDomicilio.getInt(MNU_MENUID);
            menuDesc = menuDomicilio.getString(MNU_DESC);
            menuName = menuDomicilio.getString(MNU_NAME);
            menuImgrl = menuDomicilio.getString(MNU_IMGURL);
            menuCategory = menuDomicilio.getString(MNU_CATEGORY);
            menuPrice = menuDomicilio.getString(MNU_PRICE);

            ContentValues CountryValues = new ContentValues();
            CountryValues.put(ADomicilioContract.MenuEntry.COLUMN_COUN_KEY, menuPlaceId);
            CountryValues.put(ADomicilioContract.MenuEntry.COLUMN_DATETEXT, menuDate);
            CountryValues.put(ADomicilioContract.MenuEntry.COLUMN_MENU_ID, menuId);
            CountryValues.put(ADomicilioContract.MenuEntry.COLUMN_SHORT_DESC, menuDesc);
            CountryValues.put(ADomicilioContract.MenuEntry.COLUMN_NAME, menuName);
            CountryValues.put(ADomicilioContract.MenuEntry.COLUMN_IMAGE_URL, menuImgrl);
            CountryValues.put(ADomicilioContract.MenuEntry.COLUMN_CAT_KEY, menuCategory);
            CountryValues.put(ADomicilioContract.MenuEntry.COLUMN_PRICE, menuPrice);
            cVVector.add(CountryValues);
        }
        Log.d(LOG_TAG, "FetchMenuTask Complete. " + cVVector.size() + " Inserted");
        return cVVector;
    }

    private  Vector<ContentValues> jsonCategory(JSONArray data) throws JSONException{
        Vector<ContentValues> cVVector = new Vector<ContentValues>(data.length());
        /*
        * CATE_PLACEID
          CATE_CATEGORYID
          CATE_DESC
          CATE_DESC
          CATE_IMGURL
        * */
        int categoryPlaceId=0;
        int categoryId=0;
        String categoryDescrip="";
        String categoryName="";
        String categoryURL="";

        for (int i = 0; i < data.length(); i++) {
            JSONObject parent=data.getJSONObject(i);
            JSONObject categoryDomicilio = parent.getJSONObject(CATE_LIST);
            categoryPlaceId = categoryDomicilio.getInt(CATE_PLACEID);
            categoryId = categoryDomicilio.getInt(CATE_CATEGORYID);
            categoryDescrip= categoryDomicilio.getString(CATE_DESC);
            categoryName= categoryDomicilio.getString(CATE_DESC);
            categoryURL= categoryDomicilio.getString(CATE_IMGURL);

            ContentValues CountryValues = new ContentValues();
            CountryValues.put(ADomicilioContract.CategoryEntry.COLUMN_COUN_KEY, categoryPlaceId);
            CountryValues.put(ADomicilioContract.CategoryEntry.COLUMN_CATEGORY_ID, categoryId);
            CountryValues.put(ADomicilioContract.CategoryEntry.COLUMN_SHORT_DESC, categoryDescrip);
            CountryValues.put(ADomicilioContract.CategoryEntry.COLUMN_NAME, categoryName);
            CountryValues.put(ADomicilioContract.CategoryEntry.COLUMN_IMAGE_URL,categoryURL);
            cVVector.add(CountryValues);
        }
        Log.d(LOG_TAG, "FetchCategoryTask Complete. " + cVVector.size() + " Inserted");
        return cVVector;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        ADomicilioSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
