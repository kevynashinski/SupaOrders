package com.ryletech.supaorders.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.pierry.simpletoast.SimpleToast;
import com.ryletech.supaorders.AppController;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.adapter.ProductAdapter;
import com.ryletech.supaorders.model.Category;
import com.ryletech.supaorders.model.Product;
import com.ryletech.supaorders.util.InternetConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ryletech.supaorders.util.AppConfig.*;

public class ProductsActivity extends AppCompatActivity {

    ArrayList<Product> products = new ArrayList<>();
    private CoordinatorLayout productsCoordinatorLayout;
    private RecyclerView productsRecyclerView;
    ProgressDialog progressDialog;
    private SwipeRefreshLayout productsSwipeRefreshLayout;
    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        assignViews();

        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        productsRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        productsRecyclerView.addOnItemTouchListener(new CategoryAdapter.RecyclerTouchListener(getBaseContext(), productsRecyclerView, this));

//        Get the details of the passed supermarket
        category = getIntent().getParcelableExtra(INTENT_CATEGORIESACTIVITY_PRODUCTSACTIVITY_DATA);
        if (category != null) {

            setTitle(category.getCategoryName());
            loadCategories(category.getCategoryId());
        }

        productsSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.CYAN, Color.MAGENTA, Color.BLUE);
        productsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (category != null)
                    loadCategories(category.getCategoryId());
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void assignViews() {
        productsCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.productsCoordinatorLayout);
        productsRecyclerView = (RecyclerView) findViewById(R.id.productsRecyclerView);
        productsSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.productsSwipeRefreshLayout);
    }

    private void loadCategories(final String categoryId) {
//        Post the supermarket id and receive the categories

        if (new InternetConnection(getBaseContext()).isInternetAvailable()) {
//    show progress dialog
            showRefreshing(true);

            Log.i(TAG, "loadProducts: The category id to be posted is= " + categoryId);

            StringRequest request = new StringRequest(Request.Method.POST, PRODUCTS_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String result) {
                    showRefreshing(false);

                    SimpleToast.ok(getBaseContext(), "Products loaded successfully!!!");

                    Log.i(TAG, "onResponse: Products Result= " + result);
                    try {
                        parseProductsResult(new JSONObject(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: Error while converting string to json =" + e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showRefreshing(false);

                    SimpleToast.error(getBaseContext(), "Server Error ");
                    Log.e(TAG, "onErrorResponse: Error= " + error);
                    Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(CATEGORY_ID, categoryId);
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(request);
        } else {
//    show Wireless Settings
//            SimpleToast.warning(getBaseContext(),"Internet Connection Error");
            showWirelessSettings();
        }
    }

    private void parseProductsResult(JSONObject result) {
        String productId, productName, productPrice;

        products.clear();

        Log.i(TAG, "parseProductsResult: Loading Categories ...");
        try {
            JSONArray categoriesArray = result.getJSONArray("results");

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject productsData = categoriesArray.getJSONObject(i);

                productId = productsData.getString(PRODUCT_ID);
                productName = productsData.getString(PRODUCT_NAME);
                productPrice = productsData.getString(PRODUCT_PRICE);

                Log.i(TAG, "parseProductsResult: product_id= " + productId);
                Log.i(TAG, "parseProductsResult: product_name= " + productName);
                Log.i(TAG, "parseProductsResult: product_price= " + productPrice);

                Product product = new Product();
                product.setProductId(productId);
                product.setProductName(productName);
                product.setProductPrice(productPrice);

                products.add(product);
            }
            Log.i(TAG, "parseProductsResult: Categories loaded= " + products.size());

//            pass result to adapter

            productsRecyclerView.setAdapter(new ProductAdapter(products, getBaseContext()));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseProductsResult: Error fetching the categories " + e.getMessage());
        }
    }

    private void showRefreshing(boolean refreshing) {
        if (refreshing) {
            productsSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    productsSwipeRefreshLayout.setRefreshing(true);
                }
            });
        } else {
            productsSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showWirelessSettings() {
        Snackbar snackbar = Snackbar
                .make(productsCoordinatorLayout, "Wifi & Data Disabled!", Snackbar.LENGTH_LONG)
                .setAction("Enable", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
// Changing message text color
        snackbar.setActionTextColor(Color.RED);
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                close this activity and return to previous one if any
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}

