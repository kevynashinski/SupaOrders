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
import com.ryletech.supaorders.adapter.CategoryAdapter;
import com.ryletech.supaorders.model.Category;
import com.ryletech.supaorders.model.SuperMarket;
import com.ryletech.supaorders.util.InternetConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ryletech.supaorders.util.AppConfig.CATEGORIES_URL;
import static com.ryletech.supaorders.util.AppConfig.CATEGORY_DESCRIPTION;
import static com.ryletech.supaorders.util.AppConfig.CATEGORY_ICON;
import static com.ryletech.supaorders.util.AppConfig.CATEGORY_ID;
import static com.ryletech.supaorders.util.AppConfig.CATEGORY_NAME;
import static com.ryletech.supaorders.util.AppConfig.INTENT_CATEGORIESACTIVITY_PRODUCTSACTIVITY_DATA;
import static com.ryletech.supaorders.util.AppConfig.INTENT_SUPERMARKETSACTIVITY_CATEGORIESACTIVITY_DATA;
import static com.ryletech.supaorders.util.AppConfig.SUPERMARKET_ID;
import static com.ryletech.supaorders.util.AppConfig.TAG;

public class CategoriesActivity extends AppCompatActivity implements CategoryAdapter.ClickListener {

    ArrayList<Category> categories = new ArrayList<>();
    ProgressDialog progressDialog;
    SuperMarket superMarket;
    private CoordinatorLayout categoriesCoordinatorLayout;
    private RecyclerView categoriesRecyclerView;
    private SwipeRefreshLayout categoriesSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        assignViews();

        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        categoriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoriesRecyclerView.addOnItemTouchListener(new CategoryAdapter.RecyclerTouchListener(getBaseContext(), categoriesRecyclerView, this));

//        Get the details of the passed supermarket
        superMarket = getIntent().getParcelableExtra(INTENT_SUPERMARKETSACTIVITY_CATEGORIESACTIVITY_DATA);
        if(superMarket!=null) {

            setTitle("Categories: "+superMarket.getPlaceName());
            loadCategories(superMarket.getSupermarketId());
        }

        categoriesSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.CYAN, Color.MAGENTA, Color.BLUE);
        categoriesSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadCategories(superMarket.getSupermarketId());
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
        categoriesCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.categoriesCoordinatorLayout);
        categoriesRecyclerView = (RecyclerView) findViewById(R.id.categoriesRecyclerView);
        categoriesSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.categoriesSwipeRefreshLayout);
    }

    @Override
    public void onClick(View view, int position) {
       Intent intent=new Intent(CategoriesActivity.this,ProductsActivity.class);
        intent.putExtra(INTENT_CATEGORIESACTIVITY_PRODUCTSACTIVITY_DATA,categories.get(position));
        startActivity(intent);
    }

    @Override
    public void onLongClick(View view, int position) {
    }

    private void loadCategories(final String supermarket_id) {
//        Post the supermarket id and receive the categories

        if (new InternetConnection(getBaseContext()).isInternetAvailable()) {
//    show progress dialog
            showRefreshing(true);

            Log.i(TAG, "loadCategories: The supermarket id to be posted is= " +supermarket_id);

            StringRequest request=new StringRequest(Request.Method.POST, CATEGORIES_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String result) {
                    showRefreshing(false);

                    SimpleToast.ok(getBaseContext(),"Categories loaded successfully!!!");

                    Log.i(TAG, "onResponse: Categories Result= " + result);
                    try {
                        parseCategoriesResult(new JSONObject(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: Error while converting string to json ="+e.getMessage() );
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showRefreshing(false);

                    SimpleToast.error(getBaseContext(),"Server Error ");
                    Log.e(TAG, "onErrorResponse: Error= " + error);
                    Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(SUPERMARKET_ID, supermarket_id);
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

    private void parseCategoriesResult(JSONObject result) {
        String category_id, category_name, category_description, category_icon;

        categories.clear();

        Log.i(TAG, "parseCategoriesResult: Loading Categories ...");
        try {
            JSONArray categoriesArray = result.getJSONArray("results");

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject categoryData = categoriesArray.getJSONObject(i);

                category_id = categoryData.getString(CATEGORY_ID);
                category_name = categoryData.getString(CATEGORY_NAME);
                category_description = categoryData.getString(CATEGORY_DESCRIPTION);
                category_icon = categoryData.getString(CATEGORY_ICON);

                Log.i(TAG, "parseCategoriesResult: category_id= " + category_id);
                Log.i(TAG, "parseCategoriesResult: category_name= " + category_name);
                Log.i(TAG, "parseCategoriesResult: category_description= " + category_description);
                Log.i(TAG, "parseCategoriesResult: category_icon= " + category_icon);

                Category category = new Category();
                category.setCategoryId(category_id);
                category.setCategoryName(category_name);
                category.setCategoryDescription(category_description);
                category.setCategoryIcon(category_icon);

                categories.add(category);
            }
            Log.i(TAG, "parseCategoriesResult: Categories loaded= " + categories.size());

//            pass result to adapter

            categoriesRecyclerView.setAdapter(new CategoryAdapter(categories, getBaseContext()));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseCategoriesResult: Error fetching the categories " + e.getMessage());
        }
    }

    private void showRefreshing(boolean refreshing) {
        if (refreshing) {
            categoriesSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    categoriesSwipeRefreshLayout.setRefreshing(true);
                }
            });
        } else {
            categoriesSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showWirelessSettings() {
        Snackbar snackbar = Snackbar
                .make(categoriesCoordinatorLayout, "Wifi & Data Disabled!", Snackbar.LENGTH_LONG)
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
