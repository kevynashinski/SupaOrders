package com.ryletech.supaorders.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.pierry.simpletoast.SimpleToast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pixplicity.easyprefs.library.Prefs;
import com.ryletech.supaorders.AppController;
import com.ryletech.supaorders.R;
import com.ryletech.supaorders.adapter.SupermarketAdapter;
import com.ryletech.supaorders.model.SuperMarket;
import com.ryletech.supaorders.model.SupermarketLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ryletech.supaorders.util.AppConfig.GEOMETRY;
import static com.ryletech.supaorders.util.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.ryletech.supaorders.util.AppConfig.ICON;
import static com.ryletech.supaorders.util.AppConfig.INTENT_SUPERMARKETSACTIVITY_CATEGORIESACTIVITY_DATA;
import static com.ryletech.supaorders.util.AppConfig.LATITUDE;
import static com.ryletech.supaorders.util.AppConfig.LOCATION;
import static com.ryletech.supaorders.util.AppConfig.LONGITUDE;
import static com.ryletech.supaorders.util.AppConfig.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.ryletech.supaorders.util.AppConfig.MIN_TIME_BW_UPDATES;
import static com.ryletech.supaorders.util.AppConfig.OK;
import static com.ryletech.supaorders.util.AppConfig.PLACE_ID;
import static com.ryletech.supaorders.util.AppConfig.PLAY_SERVICES_RESOLUTION_REQUEST;
import static com.ryletech.supaorders.util.AppConfig.PROXIMITY_RADIUS;
import static com.ryletech.supaorders.util.AppConfig.REFERENCE;
import static com.ryletech.supaorders.util.AppConfig.RESULTS;
import static com.ryletech.supaorders.util.AppConfig.STATUS;
import static com.ryletech.supaorders.util.AppConfig.SUPERMARKET_ID;
import static com.ryletech.supaorders.util.AppConfig.SUPERMARKET_NAME;
import static com.ryletech.supaorders.util.AppConfig.TAG;
import static com.ryletech.supaorders.util.AppConfig.VICINITY;
import static com.ryletech.supaorders.util.AppConfig.ZERO_RESULTS;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener, SupermarketAdapter.ClickListener {

    LocationManager locationManager;
    CoordinatorLayout mainCoordinatorLayout;
//    ProgressBar progressDialog;
    SupermarketLocation supermarketLocation;
    RecyclerView supermarketsRecyclerView;
    ArrayList<SuperMarket> nearBySuperMarkets = new ArrayList<>();
    AppBarLayout appBarLayout;
    private GoogleMap mMap;
    private MenuItem refreshMenuItem;
    private LinearLayout emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            return;
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Supermarkets");


        assignViews();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        supermarketsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        supermarketsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        supermarketsRecyclerView.addOnItemTouchListener(new SupermarketAdapter.RecyclerTouchListener(getBaseContext(), supermarketsRecyclerView, this));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationSettings();
        }
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showNetworkSettings();
        }

        supermarketLocation = new SupermarketLocation();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //collapse the bar
                appBarLayout.setExpanded(false, true);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void assignViews() {
        mainCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
//        progressDialog = (ProgressBar) findViewById(R.id.progressBarMain);
        supermarketsRecyclerView = (RecyclerView) findViewById(R.id.supermarketsRecyclerView);
        emptyLayout = (LinearLayout) findViewById(R.id.emptyLayout);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);

                startActivity(intent);
            }
        });

        showCurrentLocation();
    }

    private void showCurrentLocation() {
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }

    private void loadNearByPlaces(double latitude, double longitude) {

        // set the progress bar view
        showProgressBar(true);

        String type = "grocery_or_supermarket";
        String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + latitude + "," + longitude +
                "&radius=" + PROXIMITY_RADIUS +
                "&type=" + type +
                "&sensor=true" +
                "&key=" + GOOGLE_BROWSER_API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                showProgressBar(false);

                Log.i(TAG, "onResponse: Result= " + result.toString());
                parseLocationResult(result);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgressBar(false);

                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    private void parseLocationResult(JSONObject result) {

        String id, place_id, placeName = null, reference, icon, vicinity = null;
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray(RESULTS);

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                nearBySuperMarkets.clear();
                mMap.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    id = place.getString(SUPERMARKET_ID);
                    place_id = place.getString(PLACE_ID);
                    if (!place.isNull(SUPERMARKET_NAME)) {
                        placeName = place.getString(SUPERMARKET_NAME);
                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION).getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION).getDouble(LONGITUDE);
                    reference = place.getString(REFERENCE);
                    icon = place.getString(ICON);

                    Log.i(TAG, "parseLocationResult: Id= " + id);
                    Log.i(TAG, "parseLocationResult: Place_Id= " + place_id);
                    Log.i(TAG, "parseLocationResult: Place name= " + placeName);
                    Log.i(TAG, "parseLocationResult: Reference= " + reference);
                    Log.i(TAG, "parseLocationResult: Vicinity= " + vicinity);
                    Log.i(TAG, "parseLocationResult: Icon= " + icon);
                    Log.i(TAG, "parseLocationResult: Latitude= " + latitude);
                    Log.i(TAG, "parseLocationResult: Longitude= " + longitude);

                    SuperMarket superMarket = new SuperMarket();
                    superMarket.setSupermarketId(id);
                    superMarket.setPlaceId(place_id);
                    superMarket.setPlaceName(placeName);
                    superMarket.setIcon(icon);
                    superMarket.setVicinity(vicinity);
                    superMarket.setLatitude(latitude);
                    superMarket.setLongitude(longitude);
                    superMarket.setReference(reference);

                    nearBySuperMarkets.add(superMarket);

                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latitude, longitude);

                    // Setting the position for the marker
                    markerOptions.position(latLng);

                    // Setting the title for the marker.
                    //This will be displayed on taping the marker
                    markerOptions.title(placeName + " : " + vicinity);

                    // Placing a marker on the touched position
                    mMap.addMarker(markerOptions);
                }
//                add the supermarkets to the recyclerview
                supermarketsRecyclerView.setAdapter(new SupermarketAdapter(nearBySuperMarkets, getBaseContext()));


                SimpleToast.ok(getBaseContext(), jsonArray.length() + " Supermarkets found!");
                Log.i(TAG, "parseLocationResult: Supermarkets Loaded are= " + jsonArray.length());
            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
//            show empty layout
                emptyLayout.setVisibility(View.VISIBLE);

                Log.e(TAG, "parseLocationResult: Status result= " + ZERO_RESULTS);
                Toast.makeText(getBaseContext(), "No Supermarket found within 5KM radius!!!", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

//Pass the location to the model
        supermarketLocation.setLatitude(latitude);
        supermarketLocation.setLongitude(longitude);

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        loadNearByPlaces(latitude, longitude);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_settings:
                return true;
            case R.id.refresh:
                refreshMenuItem = item;

//                showProgressBar(true);
                Log.i(TAG, "onOptionsItemSelected: Refresh; lat= " + supermarketLocation.getLatitude());
                Log.i(TAG, "onOptionsItemSelected: Refresh; lng= " + supermarketLocation.getLongitude());
                loadNearByPlaces(supermarketLocation.getLatitude(), supermarketLocation.getLongitude());
                return true;
//            case R.id.cart:
//                startActivity(new Intent(MainActivity.this,CartActivity.class));
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressBar(boolean refreshing) {
        if (refreshMenuItem != null) {
            if (refreshing) {
                refreshMenuItem.setActionView(R.layout.action_progressbar);
                refreshMenuItem.expandActionView();
            } else {
                refreshMenuItem.setActionView(null);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_supermarkets) {
            // Handle the camera action
        } else if (id == R.id.nav_shopping_cart) {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(MainActivity.this, OrdersActivity.class));
        } else if (id == R.id.nav_manage_account) {
            startActivity(new Intent(MainActivity.this, UserAccountActivity.class));
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_TEXT, "Tell A Friend About Karibu Pay");
// Pass the intent into the createShareBottomSheet method to generate the BottomSheet.
            BottomSheet share = BottomSheet.createShareBottomSheet(getActivity(), intent, "My Title");
// Make sure that it doesn't return null! If the system can not handle the intent, null will be returned.
            if (share != null) {
                share.show();
            }
        } else if (id == R.id.nav_send) {

        }
//        else if (id == R.id.refresh) {
//            loadNearByPlaces(supermarketLocation.getLatitude(), supermarketLocation.getLongitude());
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void showLocationSettings() {
        Snackbar snackbar = Snackbar
                .make(mainCoordinatorLayout, "Location Error: GPS Disabled!", Snackbar.LENGTH_LONG)
                .setAction("Enable", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

    private void showNetworkSettings() {
        Snackbar snackbar = Snackbar
                .make(mainCoordinatorLayout, "Internet Connection Error!", Snackbar.LENGTH_LONG)
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
    public void onClick(View view, int position) {
//        store the supermarkets id
        Prefs.putString(SUPERMARKET_ID, nearBySuperMarkets.get(position).getSupermarketId());

        Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
        intent.putExtra(INTENT_SUPERMARKETSACTIVITY_CATEGORIESACTIVITY_DATA, nearBySuperMarkets.get(position));
        startActivity(intent);
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
