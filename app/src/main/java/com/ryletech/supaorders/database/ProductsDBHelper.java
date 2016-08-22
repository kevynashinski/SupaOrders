package com.ryletech.supaorders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ryletech.supaorders.model.Product;

import java.util.ArrayList;

/**
 * Created by sydney on 8/14/2016.
 */

public class ProductsDBHelper extends SQLiteOpenHelper {

    public ProductsDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addToCart(Product product){

    }

    public ArrayList<Product> getCartProducts(){
        ArrayList<Product> products=new ArrayList<>();
        

        return products;
    }
}
