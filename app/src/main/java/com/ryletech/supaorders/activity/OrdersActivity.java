package com.ryletech.supaorders.activity;

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
import com.pixplicity.easyprefs.library.Prefs;
import com.ryletech.supaorders.AppController;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.adapter.CategoryAdapter;
import com.ryletech.supaorders.adapter.OrderAdapter;
import com.ryletech.supaorders.model.Order;
import com.ryletech.supaorders.util.InternetConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ryletech.supaorders.util.AppConfig.ID_NUMBER;
import static com.ryletech.supaorders.util.AppConfig.ORDERED_DATE;
import static com.ryletech.supaorders.util.AppConfig.ORDERS_URL;
import static com.ryletech.supaorders.util.AppConfig.ORDER_ID;
import static com.ryletech.supaorders.util.AppConfig.TAG;

public class OrdersActivity extends AppCompatActivity implements CategoryAdapter.ClickListener {

    ArrayList<Order> orders = new ArrayList<>();
    TextView emptyOrder;
    private CoordinatorLayout ordersCoordinatorLayout;
    private RecyclerView ordersRecyclerView;
    private SwipeRefreshLayout ordersSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        assignViews();

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        ordersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ordersRecyclerView.addOnItemTouchListener(new OrderAdapter.RecyclerTouchListener(getBaseContext(), ordersRecyclerView, this));

//        Get the idNumber stored
        loadOrders();

        ordersSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.CYAN, Color.MAGENTA, Color.BLUE);
        ordersSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrders();
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
        ordersCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.ordersCoordinatorLayout);
        ordersRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
        ordersSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ordersSwipeRefreshLayout);
    }

    private void loadOrders() {
//        Post the supermarket id and receive the categories

        if (!Prefs.getString(ID_NUMBER, "").equals("")) {

            final String idNumber = Prefs.getString(ID_NUMBER, "");

            if (new InternetConnection(getBaseContext()).isInternetAvailable()) {
//    show progress dialog
                showRefreshing(true);

                Log.i(TAG, "loadProducts: The id Number to be posted is= " + idNumber);

                StringRequest request = new StringRequest(Request.Method.POST, ORDERS_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        showRefreshing(false);

                        SimpleToast.ok(getBaseContext(), "Orders loaded successfully!!!");

                        Log.i(TAG, "onResponse: Orders Result= " + result);
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
                        params.put(ID_NUMBER, idNumber);
                        return params;
                    }
                };

                AppController.getInstance().addToRequestQueue(request);
            } else {
//    show Wireless Settings
//            SimpleToast.warning(getBaseContext(),"Internet Connection Error");
                showWirelessSettings();
            }
        } else {
            SimpleToast.muted(getBaseContext(), "Add an Account first");

            emptyOrder.setVisibility(View.VISIBLE);
        }
    }

    private void parseProductsResult(JSONObject result) {
        String orderId, orderedDate;

        orders.clear();

        Log.i(TAG, "parseProductsResult: Loading Categories ...");
        try {
            JSONArray categoriesArray = result.getJSONArray("results");

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject productsData = categoriesArray.getJSONObject(i);

                orderId = productsData.getString(ORDER_ID);
                orderedDate = productsData.getString(ORDERED_DATE);

                Log.i(TAG, "parseProductsResult: order_id= " + orderId);
                Log.i(TAG, "parseProductsResult: ordered_date " + orderedDate);

                Order order = new Order();
                order.setOrderId(orderId);
                order.setOrderedDate(orderedDate);

                orders.add(order);
            }
            Log.i(TAG, "parseProductsResult: Orders loaded= " + orders.size());

//            pass result to adapter
            ordersRecyclerView.setAdapter(new OrderAdapter(orders, getBaseContext()));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseProductsResult: Error fetching the categories " + e.getMessage());
        }
    }

    private void showRefreshing(boolean refreshing) {
        if (refreshing) {
            ordersSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    ordersSwipeRefreshLayout.setRefreshing(true);
                }
            });
        } else {
            ordersSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showWirelessSettings() {
        Snackbar snackbar = Snackbar
                .make(ordersCoordinatorLayout, "Wifi & Data Disabled!", Snackbar.LENGTH_LONG)
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
//            case R.id.cart:
//                startActivity(new Intent(ProductsActivity.this, CartActivity.class));
//                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {

        String orderId = orders.get(position).getOrderId();
//        upload this order id to get all the items that were ordered that time

        SimpleToast.info(getBaseContext(), "Ready to load the products of this order");
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
