package delivery.food.eg.com.adomicilio.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zezzi on 12/17/14.
 */
public class ADomicilioContract {


    public static final String CONTENT_AUTHORITY = "delivery.food.eg.com.adomicilio";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PLACE = "place";
    public static final String PATH_COUNTRY = "country";
    public static final String PATH_MENU = "menu";
    public static final String PATH_CATEGORY="category";
    public static final String PATH_CART="cart";

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";




    /* Inner class that defines the table contents of the location table */
    public static final class CountryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COUNTRY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRY;

        // Table name
        public static final String TABLE_NAME = "country";

        // The location setting string is what will be sent to the api
        // as the country query.
        public static final String COLUMN_COUNTRY_SETTING = "country_setting";

        // Human readable location string, provided by the API.
            public static final String COLUMN_COUNTRY_NAME = "city_name";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    /* Inner class that defines the table contents of the place table */
    public static final class PlaceEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;

        public static final String TABLE_NAME = "places";
        // Column with the foreign key into the county table.
        public static final String COLUMN_LOC_KEY = "country_id";
        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_PLACE_ID = "places_id";
        // Short description
        public static final String COLUMN_SHORT_DESC = "short_desc";
        //name of the restaurant
        public static final String COLUMN_NAME="name";
        //url of the image
        public static final String COLUMN_IMAGE_URL="image_url";
        //cost delivery
        public static final String COLUMN_COST_DELIVERY="cost_delivery";
        //paying methods available
        public static final String COLUMN_PAY_METHOD="pay_methods";
        // Min delivery order
        public static final String COLUMN_MIN_ORDER = "min";
        //socialnnetwork twitter
        public static final String COLUMN_TWITTER = "twitter";
        //socialnetwork facebook
        public static final String COLUMN_FACEBOOK="facebook";

        public static final String COLUMN_TIMEOFDELIVERY="time_of_delivery";


        public static Uri buildPlaceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }



        public static Uri buildPlaceCountry(String CountrySetting) {
            return CONTENT_URI.buildUpon().appendPath(CountrySetting).build();
        }

        public static String getCountrySettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri getPlaceIdSettingFromUri(String locationSetting, String id) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(id).build();
        }
    }



    /* Inner class that defines the table contents of the menu table */
    public static final class MenuEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MENU).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MENU;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MENU;

        public static final String TABLE_NAME = "menu";
        // Column with the foreign key into the place table.
        public static final String COLUMN_COUN_KEY = "place_id";
        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        // place id as returned by API, to identify the icon to be used
        public static final String COLUMN_MENU_ID = "menu_id";
        // Short description
        public static final String COLUMN_SHORT_DESC = "short_desc";
        //name of the restaurant
        public static final String COLUMN_NAME="name";
        //url of the image
        public static final String COLUMN_IMAGE_URL="image_url";
        //category belongs to
        public static final String COLUMN_CAT_KEY="category_id";

        public static final String COLUMN_PRICE="price";

        public static Uri buildMenuUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMenuRestaurant(String menuRestaurant) {
            return CONTENT_URI.buildUpon().appendPath(menuRestaurant).appendPath(menuRestaurant).build();
        }

        public static String getPlaceSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static final String TABLE_NAME = "category";
        // Column with the foreign key into the place table.
        public static final String COLUMN_COUN_KEY = "place_id";
        // place id as returned by API, to identify the icon to be used
        public static final String COLUMN_CATEGORY_ID = "category_id";
        // Short description
        public static final String COLUMN_SHORT_DESC = "short_desc";
        //name of the restaurant
        public static final String COLUMN_NAME="name";
        //url of the image
        public static final String COLUMN_IMAGE_URL="image_url";


        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCaetgoryRestaurant(String categoryRestaurant) {
            return CONTENT_URI.buildUpon().appendPath(categoryRestaurant).build();
        }

        public static String getPlaceSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


    public static final class CartEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CART).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CART;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CART;

        public static final String TABLE_NAME = "cart";

        // Short description
        public static final String COLUMN_MENU = "menu_id";
        //name of the restaurant
        public static final String COLUMN_PLACE_ID="place_id";

        public static final String COLUMN_AMOUNT="amount";

        public static Uri buildCartUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCartRestaurant(String idRestaurant) {
            return CONTENT_URI.buildUpon().appendPath(idRestaurant).appendPath(idRestaurant).build();
        }

        public static String getPlacefromCartUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }



}
