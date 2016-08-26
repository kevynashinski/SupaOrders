package com.ryletech.supaorders.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.pierry.simpletoast.SimpleToast;
import com.pixplicity.easyprefs.library.Prefs;
import com.ryletech.supaorders.AppController;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.adapter.CartAdapter;
import com.ryletech.supaorders.database.ProductsDBHelper;
import com.ryletech.supaorders.model.Product;
import com.ryletech.supaorders.util.AppConfig;
import com.ryletech.supaorders.util.InternetConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ryletech.supaorders.util.AppConfig.FULL_NAME;
import static com.ryletech.supaorders.util.AppConfig.GENDER;
import static com.ryletech.supaorders.util.AppConfig.ID_NUMBER;
import static com.ryletech.supaorders.util.AppConfig.INTENT_SUPERMARKETSACTIVITY_CATEGORIESACTIVITY_DATA;
import static com.ryletech.supaorders.util.AppConfig.LOCATION;
import static com.ryletech.supaorders.util.AppConfig.ORDERS_JSON;
import static com.ryletech.supaorders.util.AppConfig.ORDERS_URL;
import static com.ryletech.supaorders.util.AppConfig.PHONE_NUMBER;
import static com.ryletech.supaorders.util.AppConfig.SUPERMARKET_ID;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout cartCoordinatorLayout;
    private RecyclerView cartRecyclerView;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout cartSwipeRefreshLayout;
    private ProductsDBHelper productsDBHelper;
    private ImageView cartEmptyLayout;
    private LinearLayout llCartCheckout;
    private Button clearCart, checkout;
    private ArrayList<Product> products = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        assignViews();

        productsDBHelper = new ProductsDBHelper(getBaseContext());

        loadProducts();

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        cartRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        productsRecyclerView.addOnItemTouchListener(new CategoryAdapter.RecyclerTouchListener(getBaseContext(), categoriesRecyclerView, this));

        cartSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProducts();
            }
        });

        clearCart.setOnClickListener(this);
        checkout.setOnClickListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void loadProducts() {

        if (productsDBHelper.isItemThere()) {

            cartRecyclerView.setAdapter(new CartAdapter(productsDBHelper.getCartProducts(), getBaseContext()));
            if (llCartCheckout.getVisibility() == View.GONE) {
                llCartCheckout.setVisibility(View.VISIBLE);
            }
        } else {
            cartEmptyLayout.setVisibility(View.VISIBLE);
            llCartCheckout.setVisibility(View.GONE);
        }
    }

    private void assignViews() {
        cartCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.cartCoordinatorLayout);
        cartRecyclerView = (RecyclerView) findViewById(R.id.cartProductsRecyclerView);
        cartSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.cartSwipeRefreshLayout);
        cartEmptyLayout = (ImageView) findViewById(R.id.cartEmptyLayout);
        llCartCheckout = (LinearLayout) findViewById(R.id.llCartCheckout);
        clearCart = (Button) findViewById(R.id.clearCart);
        checkout = (Button) findViewById(R.id.checkout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearCart:

                break;
            case R.id.checkout:

                if (Prefs.getString(ID_NUMBER, "").equals("")) {
                    SimpleToast.muted(getBaseContext(), "Create a delivery account first");
                    startActivity(new Intent(CartActivity.this, UserAccountActivity.class));
                }

                //show dialog to capture details
                MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                        .title("Confirm your details:")
                        .customView(R.layout.dialog_user_account, true)
                        .positiveText("Order")
                        .neutralText("Continue Shopping")
                        .negativeText("Cancel")
                        .negativeColor(Color.RED)
                        .canceledOnTouchOutside(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                if (dialog.isShowing()) {

                                    EditText txtIdNumber, txtFullName, txtPhoneNumber, txtGender, txtLocation;
                                    String idNumber, fullName, phoneNumber, gender, location;
                                    Button dialogEditDetails;

                                    View view = dialog.getCustomView();

                                    if (view != null) {

                                        txtIdNumber = (EditText) view.findViewById(R.id.dialogIdNumber);
                                        txtFullName = (EditText) view.findViewById(R.id.dialogFullName);
                                        txtPhoneNumber = (EditText) view.findViewById(R.id.dialogPhoneNumber);
                                        txtGender = (EditText) view.findViewById(R.id.dialogGender);
                                        txtLocation = (EditText) view.findViewById(R.id.dialogLocation);
                                        dialogEditDetails = (Button) view.findViewById(R.id.dialogEditDetails);

                                        dialogEditDetails.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(CartActivity.this, UserAccountActivity.class));
                                            }
                                        });
//set user's account to preferences
                                        idNumber = txtIdNumber.getText().toString();
                                        fullName = txtFullName.getText().toString();
                                        phoneNumber = txtPhoneNumber.getText().toString();
                                        gender = txtGender.getText().toString();
                                        location = txtLocation.getText().toString();

                                        Prefs.putString(ID_NUMBER, idNumber);
                                        Prefs.putString(FULL_NAME, fullName);
                                        Prefs.putString(PHONE_NUMBER, phoneNumber);
                                        Prefs.putString(GENDER, gender);
                                        Prefs.putString(LOCATION, location);

//                                        build json data


//                                        upload those details to the database
//submitOrder();

//                                        clear the cart
                                        productsDBHelper.clearCart();

                                        SimpleToast.ok(getBaseContext(), "Order placed successfully");
                                    }
                                }
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                Intent intent = new Intent(CartActivity.this, CategoriesActivity.class);
                                intent.putExtra(INTENT_SUPERMARKETSACTIVITY_CATEGORIESACTIVITY_DATA, Prefs.getString(SUPERMARKET_ID, ""));
                                startActivity(intent);
                            }
                        });


                MaterialDialog dialog = builder.build();
                dialog.show();

                break;
        }
    }

    private void submitOrder(final String jsonData) {
        if (new InternetConnection(getBaseContext()).isInternetAvailable()) {

            showProgressDialog(true);

            StringRequest request = new StringRequest(Request.Method.POST, ORDERS_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.i(AppConfig.TAG, "onResponse: Response= " + response);
                    showProgressDialog(false);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgressDialog(false);
                    Log.e(AppConfig.TAG, "onErrorResponse: Error: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
//                        params.put(SUPERMARKET_ID, Prefs.getString(SUPERMARKET_ID,""));
                    params.put(ID_NUMBER, Prefs.getString(ID_NUMBER, ""));
                    params.put(ORDERS_JSON, jsonData);
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(request);
        } else {
            showWirelessSettings();
        }
    }

    private void showProgressDialog(boolean status) {
        if (status) {
            progressDialog = new ProgressDialog(CartActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Registering...");
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
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

    private void showWirelessSettings() {
        Snackbar snackbar = Snackbar
                .make(cartCoordinatorLayout, "Wifi & Data Disabled!", Snackbar.LENGTH_LONG)
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
}
