package com.ryletech.supaorders.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ryletech.supaorders.model.Product;

import java.util.ArrayList;

import static com.ryletech.supaorders.util.AppConfig.TAG;

/**
 * Created by sydney on 8/14/2016.
 */

public class ProductsDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Products.db";

    // Contacts table name
    private static final String TABLE_PRODUCTS = "productS";

    // Contacts Table Columns names
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_PRODUCT_PRICE = "product_price";

    public ProductsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "create table " + TABLE_PRODUCTS + " ("
                + KEY_PRODUCT_ID + " text,"
                + KEY_PRODUCT_NAME + " text,"
                + KEY_PRODUCT_PRICE + " text"
                + ")";

        sqLiteDatabase.execSQL(sql);

        Log.i(TAG, "onCreate: Table " + TABLE_PRODUCTS + " Created Successfully!!!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "drop table if exists " + TABLE_PRODUCTS;

        sqLiteDatabase.execSQL(sql);

        Log.e(TAG, "onUpgrade: Table " + TABLE_PRODUCTS + " Dropped");

        onCreate(sqLiteDatabase);
    }

    public void addProductToCart(Product product) {
        SQLiteDatabase db = getWritableDatabase();

        Log.i(TAG, "addToCart: Ready to add a record to database <<" + product.toString() + ">>");

        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_ID, product.getProductId());
        values.put(KEY_PRODUCT_NAME, product.getProductName());
        values.put(KEY_PRODUCT_PRICE, product.getProductPrice());

        long insertedRow = db.insert(TABLE_PRODUCTS, null, values);
        if (insertedRow > 0) {
            Log.i(TAG, "addToCart: Product added successfully <<" + product.toString() + ">>");
        } else {
            Log.e(TAG, "addToCart: Error while saving a record");
        }
        db.close();
    }

    public ArrayList<Product> getCartProducts() {
        ArrayList<Product> products = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i(TAG, "getProducts: Reading all the Products from the database");

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProductId(cursor.getString(0));
                product.setProductName(cursor.getString(1));
                product.setProductPrice(cursor.getString(2));

                Log.i(TAG, "getProducts: Data Query= " + product.toString());

                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Log.i(TAG, "getProducts: Number of products Retrieved= " + products.size());

        return products;
    }

    public Integer cartItemsCount() {

        String countQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void removeProductFromCar(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db.delete(TABLE_PRODUCTS, KEY_PRODUCT_ID + " = ?", new String[]{String.valueOf(product.getProductId())}) > 0) {
            Log.i(TAG, "removeProductFromCar: 1 Product removed from cart");
        } else {
            Log.e(TAG, "removeProductFromCar: No product removed");
        }
        db.close();
    }

//    add a method to bulk remove products form db

}
