package com.example.currenyconverter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity class that shows main page
 */
public class CurrencyConverterActivity extends AppCompatActivity implements CurrencyLoaderService.ConverterListener<ArrayList<Currency>> {

    /**
     * List of currencies got from API
     */
    private final String TAG = "CurrencyConverter";
    private List<Currency> currencyList = new ArrayList<Currency>();
    private RecyclerView recyclerView;
    private ConverterAdapter mAdapter;
    CurrencyLoaderService currencyLoaderService;
    Intent service;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = this.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        setUpRecycler();

        retrieveCurrencyData();
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            /**
             * Clear volley RequestQueue when there is a change in SharedPreferences
             */
            currencyLoaderService.clearOldRequestQueue();
        }
    };

    /**
     * Setting up the recyclerview
     */
    private void setUpRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ConverterAdapter(this, currencyList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Start and bind CurrencyLoaderService
     */
    private void retrieveCurrencyData() {
        Log.d(TAG, " startService");
        service = new Intent(this, CurrencyLoaderService.class);
        startService(service); //Starting the service
        bindService(service, mConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(TAG, " onServiceConnected");
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            CurrencyLoaderService.LocalBinder binder = (CurrencyLoaderService.LocalBinder) service;
            currencyLoaderService = binder.getServiceInstance(); //Get instance of your service!
            currencyLoaderService.registerClient(CurrencyConverterActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, " onServiceDisconnected");
        }
    };


    @Override
    public void onConverterViewDownloaded(ArrayList<Currency> a) {
        currencyList.clear();
        currencyList.addAll(a);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorDownloading(String errorMessage) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPref.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        unbindService(mConnection);
        stopService(service);
        //currencyLoaderService.onStop();
    }
}
