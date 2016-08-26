package com.ryletech.supaorders.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.pierry.simpletoast.SimpleToast;
import com.pixplicity.easyprefs.library.Prefs;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.adapter.CartAdapter;
import com.ryletech.supaorders.database.ProductsDBHelper;

import static com.ryletech.supaorders.util.AppConfig.FULL_NAME;
import static com.ryletech.supaorders.util.AppConfig.GENDER;
import static com.ryletech.supaorders.util.AppConfig.ID_NUMBER;
import static com.ryletech.supaorders.util.AppConfig.LOCATION;
import static com.ryletech.supaorders.util.AppConfig.PHONE_NUMBER;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout cartCoordinatorLayout;
    private RecyclerView cartRecyclerView;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout cartSwipeRefreshLayout;
    private ProductsDBHelper productsDBHelper;
    private ImageView cartEmptyLayout;
    private LinearLayout llCartCheckout;
    private Button clearCart, checkout;

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
                //show dialog to capture details
                MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                        .title("Enter your details:")
                        .customView(R.layout.dialog_user_account, true)
                        .positiveText("Order")
                        .negativeText("Cancel")
                        .negativeColor(Color.RED)
                        .canceledOnTouchOutside(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                if (dialog.isShowing()) {

                                    EditText txtIdNumber, txtFullName, txtPhoneNumber, txtGender, txtLocation;
                                    String idNumber, fullName, phoneNumber, gender, location;

                                    View view = dialog.getCustomView();

                                    if (view != null) {

                                        txtIdNumber = (EditText) view.findViewById(R.id.idNumber);
                                        txtFullName = (EditText) view.findViewById(R.id.fullName);
                                        txtPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
                                        txtGender = (EditText) view.findViewById(R.id.gender);
                                        txtLocation = (EditText) view.findViewById(R.id.location);

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

//                                        show ordering dialog

//                                        upload those details to the database

//                                        clear the cart
                                        productsDBHelper.clearCart();

                                        SimpleToast.ok(getBaseContext(), "Order placed successfully");
                                    }
                                }
                            }
                        });

                MaterialDialog dialog = builder.build();
                dialog.show();

                if (dialog.isShowing()) {

                    EditText txtIdNumber, txtFullName, txtPhoneNumber, txtGender, txtLocation;
                    String idNumber, fullName, phoneNumber, gender, location;

                    View view = dialog.getCustomView();

                    if (view != null) {

                        txtIdNumber = (EditText) view.findViewById(R.id.idNumber);
                        txtFullName = (EditText) view.findViewById(R.id.fullName);
                        txtPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
                        txtGender = (EditText) view.findViewById(R.id.gender);
                        txtLocation = (EditText) view.findViewById(R.id.location);

                        if (!Prefs.getString(ID_NUMBER, "").equals("")) {


                            idNumber = txtIdNumber.getText().toString();
                            fullName = txtFullName.getText().toString();
                            phoneNumber = txtPhoneNumber.getText().toString();
                            gender = txtGender.getText().toString();
                            location = txtLocation.getText().toString();
                        }
                    } else {

                    }
                }


                break;
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
}
