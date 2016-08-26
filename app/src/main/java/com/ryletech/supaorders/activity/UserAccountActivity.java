package com.ryletech.supaorders.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.pierry.simpletoast.SimpleToast;
import com.pixplicity.easyprefs.library.Prefs;
import com.ryletech.supaorders.AppController;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.util.AppConfig;
import com.ryletech.supaorders.util.InternetConnection;

import java.util.HashMap;
import java.util.Map;

import static com.ryletech.supaorders.R.id.fullName;
import static com.ryletech.supaorders.R.id.idNumber;
import static com.ryletech.supaorders.util.AppConfig.FULL_NAME;
import static com.ryletech.supaorders.util.AppConfig.GENDER;
import static com.ryletech.supaorders.util.AppConfig.ID_NUMBER;
import static com.ryletech.supaorders.util.AppConfig.LOCATION;
import static com.ryletech.supaorders.util.AppConfig.PHONE_NUMBER;
import static com.ryletech.supaorders.util.AppConfig.USER_ACCOUNTS_URL;

public class UserAccountActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;
    Button clearAccount, saveDetails;
    EditText txtIdNumber, txtFullname, txtPhoneNumber, txtGender, txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        assignViews();

        clearAccount.setOnClickListener(this);
        saveDetails.setOnClickListener(this);

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
        clearAccount = (Button) findViewById(R.id.clearAccount);
        saveDetails = (Button) findViewById(R.id.saveDetails);
        txtIdNumber = (EditText) findViewById(idNumber);
        txtFullname = (EditText) findViewById(fullName);
        txtPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        txtGender = (EditText) findViewById(R.id.gender);
        txtLocation = (EditText) findViewById(R.id.location);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearAccount:
                clearFields();
                break;
            case R.id.saveDetails:

                if (!Prefs.getString(ID_NUMBER, "").equals("")) {
                    txtIdNumber.setText(Prefs.getString(ID_NUMBER, ""));
                    txtFullname.setText(Prefs.getString(FULL_NAME, ""));
                    txtGender.setText(Prefs.getString(GENDER, ""));
                    txtLocation.setText(Prefs.getString(LOCATION, ""));

                    saveDetails.setText("Confirm Details");
                }

                final String idNumber, fullName, phoneNumber, gender, location;

                idNumber = txtIdNumber.getText().toString();
                fullName = txtFullname.getText().toString();
                phoneNumber = txtPhoneNumber.getText().toString();
                gender = txtGender.getText().toString();
                location = txtLocation.getText().toString();

//                save the details in preferences
                Prefs.putString(ID_NUMBER, idNumber);
                Prefs.putString(FULL_NAME, fullName);
                Prefs.putString(PHONE_NUMBER, phoneNumber);
                Prefs.putString(GENDER, gender);
                Prefs.putString(LOCATION, location);

                if (new InternetConnection(getBaseContext()).isInternetAvailable()) {

                    showProgressDialog(true);
//                upload the details
                    StringRequest request = new StringRequest(Request.Method.POST, USER_ACCOUNTS_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            showProgressDialog(false);
                            Log.i(AppConfig.TAG, "onResponse: Response= " + response);
                            switch (response) {
                                case "0":
                                    SimpleToast.ok(getBaseContext(), "Account Registration Success");
//clear fields
                                    clearFields();
                                    finish();
                                    break;
                                case "1":
                                    SimpleToast.warning(getBaseContext(), "Server Error!");
                                    break;
                                case "2":
                                    SimpleToast.info(getBaseContext(), "Account already exists");

//                            show dialog to confirm details
                                    startActivity(new Intent(UserAccountActivity.this, MainActivity.class));
                                    finish();
                                    break;
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgressDialog(true);

                            Log.e(AppConfig.TAG, "onErrorResponse: UserAccount Error= " + error.getMessage());

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
//                        params.put(SUPERMARKET_ID, Prefs.getString(SUPERMARKET_ID,""));
                            params.put(ID_NUMBER, idNumber);
                            params.put(FULL_NAME, fullName);
                            params.put(PHONE_NUMBER, phoneNumber);
                            params.put(GENDER, gender);
                            params.put(LOCATION, location);
                            return params;
                        }
                    };

                    AppController.getInstance().addToRequestQueue(request);
                } else {
                    SimpleToast.warning(getBaseContext(), "Check your internet connection");
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
//            case R.id.cart:
//                startActivity(new Intent(ProductsActivity.this, CartActivity.class));
//                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog(boolean status) {
        if (status) {
            progressDialog = new ProgressDialog(UserAccountActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Registering...");
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private void clearFields() {
        txtIdNumber.setText("");
        txtFullname.setText("");
        txtPhoneNumber.setText("");
        txtGender.setText("");
        txtLocation.setText("");
    }
}
