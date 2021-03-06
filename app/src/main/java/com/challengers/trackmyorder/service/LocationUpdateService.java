package com.challengers.trackmyorder.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.challengers.trackmyorder.util.Constants;
import com.challengers.trackmyorder.util.NetworkUtil;
import com.challengers.trackmyorder.util.Prefs;
import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

//import android.support.v4.app.ActivityCompat;


public class LocationUpdateService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks {
    //PowerManager.WakeLock wakeLock;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Wake Lock to send in background
        //PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        //wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.LOCATION_FAST_UPDATE_INTERVAL);

        Toast.makeText(LocationUpdateService.this, "Location Change service called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null && NetworkUtil.isConnected(this)) {
            //Store the date in Firebase
            /*Realm realm = Realm.getDefaultInstance();
            String delBoyId = realm.where(DelBoy.class).findFirst().getId();*/
            String currentDelBoyId = Prefs.getString(Constants.CURRENT_DELBOY, null);
            Toast.makeText(LocationUpdateService.this, "Location Change triggered", Toast.LENGTH_SHORT).show();
            if(currentDelBoyId != null) {
                Firebase currentDelBoyRef = Constants.delboyRef.child("/" + currentDelBoyId);
                currentDelBoyRef.child("currentLocation").setValue(location.getLatitude() + Constants.LOCATION_DELIMITER + location.getLongitude());
            } else {
                Toast.makeText(LocationUpdateService.this, "Location Change failed because of null user", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        }
        if(Prefs.getString(Constants.CURRENT_DELBOY, null) == null) {
            //wakeLock.release();
            stopSelf();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(LocationUpdateService.this, "On Connected API client in Service", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Toast.makeText(LocationUpdateService.this, "Location Services called", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LocationUpdateService.this, "Location Services calling failed", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //wakeLock.release();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
}
