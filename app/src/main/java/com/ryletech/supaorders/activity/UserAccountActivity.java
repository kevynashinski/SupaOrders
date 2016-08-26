package com.ryletech.supaorders.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import static com.ryletech.supaorders.util.AppConfig.FULL_NAME;
import static com.ryletech.supaorders.util.AppConfig.GENDER;
import static com.ryletech.supaorders.util.AppConfig.ID_NUMBER;
import static com.ryletech.supaorders.util.AppConfig.LOCATION;
import static com.ryletech.supaorders.util.AppConfig.PHONE_NUMBER;
import static com.ryletech.supaorders.util.AppConfig.USER_ACCOUNTS_URL;

public class UserAccountActivity extends AppCompatActivity implements View.OnClickListener {

    Button clearAccount, saveDetails;
    EditText txtIdNumber, txtFullname, txtPhoneNumber, txtGender, txtLocation;
    String supermarketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assignViews();

        saveDetails.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void assignViews() {
        clearAccount = (Button) findViewById(R.id.clearAccount);
        saveDetails = (Button) findViewById(R.id.saveDetails);
        txtIdNumber = (EditText) findViewById(R.id.idNumber);
        txtFullname = (EditText) findViewById(R.id.fullName);
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

//                upload the details
                    StringRequest request = new StringRequest(Request.Method.POST, USER_ACCOUNTS_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

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

    private void clearFields() {
        txtIdNumber.setText("");
        txtFullname.setText("");
        txtPhoneNumber.setText("");
        txtGender.setText("");
        txtLocation.setText("");
    }
}
