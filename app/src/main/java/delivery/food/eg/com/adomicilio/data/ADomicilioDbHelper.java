package delivery.food.eg.com.adomicilio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import delivery.food.eg.com.adomicilio.data.ADomicilioContract.CountryEntry;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.PlaceEntry;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.MenuEntry;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.CategoryEntry;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract.CartEntry;



/**
 * Created by zezzi on 12/17/14.
 */
public class ADomicilioDbHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "adomicilio.db";

    public ADomicilioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_COUNTRY_TABLE = "CREATE TABLE " + CountryEntry.TABLE_NAME + " (" +
                CountryEntry._ID + " INTEGER PRIMARY KEY," +
                CountryEntry.COLUMN_COUNTRY_SETTING + " TEXT UNIQUE NOT NULL, " +
                CountryEntry.COLUMN_COUNTRY_NAME + " TEXT NOT NULL, " +
                "UNIQUE (" + CountryEntry.COLUMN_COUNTRY_SETTING +") ON CONFLICT IGNORE"+
                " );";

        final String SQL_CREATE_PLACE_TABLE = "CREATE TABLE " + PlaceEntry.TABLE_NAME + " (" +
                PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                PlaceEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                PlaceEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_PLACE_ID + " INTEGER NOT NULL," +

                PlaceEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +

                PlaceEntry.COLUMN_COST_DELIVERY + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_PAY_METHOD + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_MIN_ORDER + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_TWITTER + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_FACEBOOK+" TEXT NOT NULL, "+
                PlaceEntry.COLUMN_TIMEOFDELIVERY+" TEXT NOT NULL, "+
                "UNIQUE (" + PlaceEntry.COLUMN_PLACE_ID +") ON CONFLICT IGNORE"+
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + PlaceEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                CountryEntry.TABLE_NAME + " (" + CountryEntry._ID + ") " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                "  );";

        final String SQL_CREATE_MENU_TABLE = "CREATE TABLE " + MenuEntry.TABLE_NAME + " (" +
                MenuEntry._ID + " INTEGER PRIMARY KEY," +
                MenuEntry.COLUMN_COUN_KEY + " INTEGER NOT NULL, " +
                MenuEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                MenuEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                MenuEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MenuEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                MenuEntry.COLUMN_MENU_ID + " INTEGER NOT NULL," +
                MenuEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                MenuEntry.COLUMN_CAT_KEY + " INTEGER NOT NULL," +
                " UNIQUE (" + MenuEntry.COLUMN_MENU_ID +") ON CONFLICT IGNORE"+
                " );";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                CategoryEntry.COLUMN_COUN_KEY + " INTEGER NOT NULL, " +
                CategoryEntry.COLUMN_CATEGORY_ID + " INTEGER NOT NULL, " +
                CategoryEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                CategoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                CategoryEntry.COLUMN_IMAGE_URL+" TEXT NOT NULL, " +
                " UNIQUE (" + CategoryEntry.COLUMN_CATEGORY_ID +") ON CONFLICT IGNORE"+
                " );";

        final String SQL_CREATE_CART_TABLE = "CREATE TABLE " + CartEntry.TABLE_NAME + " (" +
                CartEntry._ID + " INTEGER PRIMARY KEY," +
                CartEntry.COLUMN_MENU + " INTEGER NOT NULL, " +
                CartEntry.COLUMN_PLACE_ID + " INTEGER NOT NULL, " +
                CartEntry.COLUMN_AMOUNT + " INTEGER NOT NULL " +

                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_COUNTRY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLACE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MENU_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CART_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CountryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlaceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MenuEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CartEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
