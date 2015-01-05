package delivery.food.eg.com.adomicilio;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.Date;
import java.util.Vector;

import delivery.food.eg.com.adomicilio.data.ADomicilioContract;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.CountryEntry;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.PlaceEntry;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.MenuEntry;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.CategoryEntry;

/**
 * Created by zezzi on 12/26/14.
 */
public class FetchInfoTask extends AsyncTask<Integer, Void, Void> {

    private final String LOG_TAG = FetchInfoTask.class.getSimpleName();
    private final Context mContext;
    private final String URL_DOMAIN_JSON="http://adomiciliogua.herokuapp.com/";
    private final String URL_COUNTRY=URL_DOMAIN_JSON+"countries.json";
    private final String URL_PLACE=URL_DOMAIN_JSON+"places.json";
    private final String URL_MENU=URL_DOMAIN_JSON+"menus.json";
    private final String URL_CATEGORY=URL_DOMAIN_JSON+"categories.json";
    private final int COUNTRYURL=1;
    private final int PLACEURL=2;
    private final int MENUURL=3;
    private final int CATEGORYURL=4;


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



    public FetchInfoTask(Context context) {
        mContext = context;
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
                    mContext.getContentResolver().bulkInsert(CountryEntry.CONTENT_URI, cvArray);
                }
                break;
            case PLACEURL:
                placeArray= new JSONArray(infoJsonStr);
                Vector<ContentValues> cVVector2 = new Vector<ContentValues>(placeArray.length());
                cVVector2=jsonPlace(placeArray);
                if (cVVector2.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector2.size()];
                    cVVector2.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(PlaceEntry.CONTENT_URI, cvArray);
                }
                break;
            case MENUURL:
                menuArray=new JSONArray(infoJsonStr);
                Vector<ContentValues> cVVector3 = new Vector<ContentValues>(menuArray.length());
                cVVector3=jsonMenu(menuArray);
                if (cVVector3.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector3.size()];
                    cVVector3.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(MenuEntry.CONTENT_URI, cvArray);
                }
                break;
            case CATEGORYURL:
                categoryArray=new JSONArray(infoJsonStr);
                Vector<ContentValues> cVVector4 = new Vector<ContentValues>(categoryArray.length());
                cVVector4=jsonCategory(categoryArray);
                if (cVVector4.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector4.size()];
                    cVVector4.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(CategoryEntry.CONTENT_URI, cvArray);
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
                CountryValues.put(CountryEntry.COLUMN_COUNTRY_SETTING, countrySetting);
                CountryValues.put(CountryEntry.COLUMN_COUNTRY_NAME, nameCountry);
                cVVector.add(CountryValues);
            }
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
            CountryValues.put(PlaceEntry.COLUMN_LOC_KEY, placeConunry);
            CountryValues.put(PlaceEntry.COLUMN_DATETEXT, placeDate);
            CountryValues.put(PlaceEntry.COLUMN_PLACE_ID, placeId);
            CountryValues.put(PlaceEntry.COLUMN_SHORT_DESC, placeDesc);
            CountryValues.put(PlaceEntry.COLUMN_NAME, placeName);
            CountryValues.put(PlaceEntry.COLUMN_IMAGE_URL, placeIMG);
            CountryValues.put(PlaceEntry.COLUMN_COST_DELIVERY, placeDelivery);
            CountryValues.put(PlaceEntry.COLUMN_PAY_METHOD, placePaymethods);
            CountryValues.put(PlaceEntry.COLUMN_MIN_ORDER, placeMin);
            CountryValues.put(PlaceEntry.COLUMN_TWITTER, placeTwitter);
            CountryValues.put(PlaceEntry.COLUMN_FACEBOOK, placeFacebook);
            CountryValues.put(PlaceEntry.COLUMN_TIMEOFDELIVERY, placeTimeOfDelivery);
            cVVector.add(CountryValues);
        }
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
            CountryValues.put(MenuEntry.COLUMN_COUN_KEY, menuPlaceId);
            CountryValues.put(MenuEntry.COLUMN_DATETEXT, menuDate);
            CountryValues.put(MenuEntry.COLUMN_MENU_ID, menuId);
            CountryValues.put(MenuEntry.COLUMN_SHORT_DESC, menuDesc);
            CountryValues.put(MenuEntry.COLUMN_NAME, menuName);
            CountryValues.put(MenuEntry.COLUMN_IMAGE_URL, menuImgrl);
            CountryValues.put(MenuEntry.COLUMN_CAT_KEY, menuCategory);
            CountryValues.put(MenuEntry.COLUMN_PRICE, menuPrice);
            cVVector.add(CountryValues);
        }
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
            CountryValues.put(CategoryEntry.COLUMN_COUN_KEY, categoryPlaceId);
            CountryValues.put(CategoryEntry.COLUMN_CATEGORY_ID, categoryId);
            CountryValues.put(CategoryEntry.COLUMN_SHORT_DESC, categoryDescrip);
            CountryValues.put(CategoryEntry.COLUMN_NAME, categoryName);
            CountryValues.put(CategoryEntry.COLUMN_IMAGE_URL,categoryURL);
            cVVector.add(CountryValues);
        }
        return cVVector;
    }


    @Override
    protected Void doInBackground(Integer... params) {
        String URL_GENERAL="";
        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        Integer urlToGet=params[0];
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
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
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
            return null;
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
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }
}
