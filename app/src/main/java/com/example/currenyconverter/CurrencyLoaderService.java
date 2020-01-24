package com.example.currenyconverter;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class responsible for downloading data from the Currency API.
 */
public class CurrencyLoaderService extends Service {


    Handler handler = new Handler();

    private SharedPreferences sharedPref;
    private final IBinder mBinder = new LocalBinder();
    ConverterListener activity;

    /**
     * The interface which will be notified when the ConverterViews are downloaded
     */
    public interface ConverterListener<AnyType> {
        void onConverterViewDownloaded(AnyType a);

        void onErrorDownloading(String errorMessage);
    }


    private static CurrencyLoaderService CurrencyLoaderService = null;

    /**
     * RequestQueue Volley-library
     */
    protected RequestQueue queue;


    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        this.sharedPref = this.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }


    /**
     * Fetching popular ConverterViews
     */
    public void getPopularConverterViews(String baseCurrency) {

        // Append the api key to the url
        Uri uri = Uri.parse(Constants.API_BASE_URL).buildUpon()
                .appendQueryParameter(Constants.API_REQUEST_KEY, baseCurrency)
                .build();

        Log.d("Revathy loader", uri.toString());


        // Construction a JsonObjectRequest.
        // This means that we expect to receive a JsonObject from the API.
        final JsonObjectRequest json = new JsonObjectRequest(
                Request.Method.GET, uri.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // This indicates that the call was successfull.
                        Log.d("response ", "" + response);
                        try {
                            // Creating an ArrayList with Currency-objects.
                            ArrayList<Currency> currencyViews = parseConverterViews(response);
                            //Log.d("Revathy currencyViews ", "" + currencyViews);
                            // Firing our interface-method, returning the currencyViews to the activity

                            activity.onConverterViewDownloaded(currencyViews);
                        } catch (NullPointerException | JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // This indicates that the call wasn't successful.
                // We'll return a String to our activity
                Log.d("Revathy error", "" + error);
                activity.onErrorDownloading("Error connecting to the API");
            }
        }
        );
        json.setTag(Constants.REQUEST_TAG);
        // Adding the response to our requestqueue
        queue.add(json);
    }

    /**
     * Fetching ConverterViews from json provided by the currency API.
     *
     * @param jsonObject
     * @return ArrayList of ConverterViews
     */
    private ArrayList<Currency> parseConverterViews(JSONObject jsonObject) throws JSONException {
        ArrayList<Currency> currencyArrayList = new ArrayList<>();
        Currency currency = null;
        String baseCurrecny = sharedPref.getString(Constants.PREFERENCE_CURRENCY, Constants.INITIAL_BASE_CURRENCY);
        int defualtFlag = this.getResources().getIdentifier("ic_sentiment_very_satisfied_black_24dp", "drawable", this.getPackageName());
        int flag = this.getResources().getIdentifier("ic_" + baseCurrecny.toLowerCase(), "drawable", this.getPackageName());
        currency = new Currency((flag != 0) ? flag : defualtFlag, sharedPref.getString(Constants.PREFERENCE_CURRENCY, Constants.INITIAL_BASE_CURRENCY), sharedPref.getString(Constants.PREFERENCE_VALUE, Constants.INITIAL_BASE_CURRENCY_VALUE), true);
        currencyArrayList.add(currency);
        JSONObject rates = jsonObject.optJSONObject(Constants.API_RATE_KEY);
        Iterator<?> keys = rates.keys();
        while (keys.hasNext()) {
            try {
                String key = (String) keys.next();
                Double value = (Double) rates.get(key) * Double.parseDouble(sharedPref.getString(Constants.PREFERENCE_VALUE, Constants.INITIAL_BASE_CURRENCY_VALUE));
                int currencyFlag = this.getResources().getIdentifier("ic_" + key.toLowerCase(), "drawable", this.getPackageName());
                currency = new Currency((currencyFlag != 0) ? currencyFlag : defualtFlag, key, value + "", false);
                currencyArrayList.add(currency);
            } catch (JSONException e) {
            }
        }

        return currencyArrayList;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        if (queue != null) {
            queue.cancelAll(Constants.REQUEST_TAG);
        }
        if (sharedPref != null) {
            sharedPref.edit().remove(Constants.PREFERENCE_CURRENCY).commit();
            sharedPref.edit().remove(Constants.PREFERENCE_VALUE).commit();
        }
        return super.onUnbind(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Revathy", " onStartCommand");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Revathy", " onBind");
        return mBinder;
    }

    //returns the instance of the service
    public class LocalBinder extends Binder {
        public CurrencyLoaderService getServiceInstance() {
            return CurrencyLoaderService.this;
        }
    }

    //Here Activity register to the service as Callbacks client
    public void registerClient(Activity activity) {
        this.activity = (ConverterListener) activity;
        handler.postDelayed(serviceRunnable, 0);
    }

    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            getPopularConverterViews(sharedPref.getString(Constants.PREFERENCE_CURRENCY, Constants.INITIAL_BASE_CURRENCY));
            handler.postDelayed(this, Constants.REQUEST_INTERVAL);
        }
    };

    public void clearOldRequestQueue() {
        if (queue != null) {
            queue.cancelAll(Constants.REQUEST_TAG);
        }
    }

}