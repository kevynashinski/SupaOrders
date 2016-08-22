package com.ryletech.supaorders.util;

/**
 * Created by sydney on 6/26/2016.
 */
public final class AppConfig {

    public static final String TAG = "supa";


    public static final String RESULTS = "results";
    public static final String STATUS = "status";

    public static final String OK = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String REQUEST_DENIED = "REQUEST_DENIED";
    public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";

    //    Key for nearby locations
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String ICON = "icon";
    public static final String SUPERMARKET_ID = "id";
    //    public static final String SUPERMARKET_NAME = "supermarket_name";
    public static final String SUPERMARKET_NAME = "name";
    public static final String PLACE_ID = "place_id";
    public static final String REFERENCE = "reference";
    public static final String VICINITY = "vicinity";
    public static final String PLACE_NAME = "place_name";


    public static final String GOOGLE_BROWSER_API_KEY = "AIzaSyA_iXa9oR6cwbmYS6BjFW8xiXeoezeRXOk";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int PROXIMITY_RADIUS = 5000;
    // The minimum distance to change Updates in meters
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    //    keys for intent passing data
    public static final String INTENT_SUPERMARKETSACTIVITY_CATEGORIESACTIVITY_DATA = "intent_supermarketactivity_categoriesactivity_data";
    public static final String INTENT_CATEGORIESACTIVITY_PRODUCTSACTIVITY_DATA = "intent_categoriesactivity_productaactivity_data";

    public static final String FILENAME_NEARBY_PLACES = "nearby_places.json";

    //    keys for categories
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_DESCRIPTION = "category_description";
    public static final String CATEGORY_ICON = "category_icon";

    //    keys for product
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_PRICE = "product_price";

//    private static final String SERVER_URL = "https://orders-kevynashinski.c9users.io/";
        private static final String SERVER_URL = "http://192.168.56.1/orders/";
    public static final String CATEGORIES_URL = SERVER_URL + "categories.php";
    public static final String PRODUCTS_URL = SERVER_URL + "products.php";

//    preferences
public static final String FIRST_RUN = "first_run";
}
