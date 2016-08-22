package com.ryletech.supaorders.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ryletech.supaorders.model.SuperMarket;

import java.util.ArrayList;

import static com.ryletech.supaorders.util.AppConfig.TAG;

/**
 * Created by sydney on 6/28/2016.
 */
public class SupermarketsDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SupermarketManager";

    // Contacts table name
    private static final String TABLE_SUPERMARKET = "supermarket";

    // Contacts Table Columns names
    private static final String KEY_ID = "supermarket_id";
    private static final String KEY_PLACE_ID = "place_id";
    private static final String KEY_PLACE_NAME = "place_name";
    private static final String KEY_REFERENCE = "reference";
    private static final String KEY_ICON = "icon";
    private static final String KEY_VICINITY = "vicinity";
    private static final String KEY_LATITUDE = "lat";
    private static final String KEY_LONGITUDE = "lng";

    public SupermarketsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "create table " + TABLE_SUPERMARKET + " ("
                + KEY_ID + " text,"
                + KEY_PLACE_ID + " text,"
                + KEY_PLACE_NAME + " text,"
                + KEY_REFERENCE + " text,"
                + KEY_ICON + " text,"
                + KEY_VICINITY + " text,"
                + KEY_LATITUDE + " text,"
                + KEY_LONGITUDE + " text"
                + ")";

        sqLiteDatabase.execSQL(sql);

        Log.i(TAG, "onCreate: Table "+TABLE_SUPERMARKET+" Created Successfully!!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        String sql = "drop table if exists " + TABLE_SUPERMARKET;

        sqLiteDatabase.execSQL(sql);

        Log.e(TAG, "onUpgrade: Table "+TABLE_SUPERMARKET+" Dropped" );

        onCreate(sqLiteDatabase);
    }

    public void addSupermarket(SuperMarket superMarket) {
        SQLiteDatabase db = getWritableDatabase();

        Log.i(TAG, "addSupermarket: Ready to add a record to database <<"+ superMarket.toString() + ">>");

        ContentValues values = new ContentValues();
        values.put(KEY_ID, superMarket.getSupermarketId());
        values.put(KEY_PLACE_ID, superMarket.getPlaceId());
        values.put(KEY_PLACE_NAME, superMarket.getPlaceName());
        values.put(KEY_REFERENCE, superMarket.getReference());
        values.put(KEY_ICON, superMarket.getIcon());
        values.put(KEY_VICINITY, superMarket.getVicinity());
        values.put(KEY_LATITUDE, superMarket.getLatitude());
        values.put(KEY_LONGITUDE, superMarket.getLongitude());

        long insertedRow=db.insert(TABLE_SUPERMARKET, null, values);
        if ( insertedRow> 0) {
            Log.i(TAG, "addSupermarket: Record added successfully <<" + superMarket.toString() + ">>");
        } else {
            Log.e(TAG, "addSupermarket: Error while saving a record");
        }
        db.close();
    }

    public ArrayList<SuperMarket> getSupermarkets() {

        ArrayList<SuperMarket> superMarkets = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SUPERMARKET;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i(TAG, "getSupermarkets: Reading all the supermarkets from the database");

        if (cursor.moveToFirst()) {
            do {
                SuperMarket superMarket = new SuperMarket();
                superMarket.setSupermarketId(cursor.getString(0));
                superMarket.setPlaceId(cursor.getString(1));
                superMarket.setPlaceName(cursor.getString(2));
                superMarket.setReference(cursor.getString(3));
                superMarket.setIcon(cursor.getString(4));
                superMarket.setVicinity(cursor.getString(5));
                superMarket.setLatitude(cursor.getDouble(6));
                superMarket.setLongitude(cursor.getDouble(7));

                Log.i(TAG, "getSupermarkets: Data Query= "+superMarket.toString());

                superMarkets.add(superMarket);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Log.i(TAG, "getSupermarkets: Number of Supermarkets Retrieved= "+superMarkets.size());

        return superMarkets;
    }

    public void deleteSupermarkets(){
        SQLiteDatabase db = this.getWritableDatabase();

        onUpgrade(db,DATABASE_VERSION,2);
    }
}
