package ch.markusko.stalkme;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by markus on 10/25/17.
 */

public class LocationService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private String id = "";
    private String share_url = "";
    private String cookie = "";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1f;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener() {
            Log.e(TAG, "LocationListener created");
            mLastLocation = new Location("none");
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            UpdateRemoteLocationRequest request = new UpdateRemoteLocationRequest(cookie, mLastLocation.getLongitude(), mLastLocation.getLatitude());
            new UpdateRemoteLocationTask().execute(request);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

    }

    LocationListener mLocationListener = new LocationListener();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals("ch.markusko.stalkme.action.startforeground")) {
            Log.e(TAG, "onStartCommand");
            super.onStartCommand(intent, flags, startId);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction("ch.markusko.stalkme.action.main");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.notification);

            Notification notification = new Notification.Builder(this)
                    .setContentTitle("nTODOFoo")
                    .setTicker("nTODOBar")
                    .setContentText("nFOOWHYn")
                    .setContent(notificationView)
                    .setOngoing(true).build();


            startForeground(101, notification);
        } else if (intent.getAction().equals("ch.markusko.stalkme.action.stopforeground")) {
            stopForeground(true);
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        createSession();
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }

    private void createSession() {
        try {
            CreateSessionTask createSessionTask = new CreateSessionTask();
            createSessionTask.execute();
            CreateSessionResult createSessionResult = createSessionTask.get();
            cookie = createSessionResult.getCookie();
            Message message = new Message();
            Bundle data = new Bundle();
            data.putString("url", createSessionResult.getShareUrl());
            message.setData(data);
            message.setAsynchronous(true);
            Intent intent = new Intent("set_url");
            intent.putExtra("url", createSessionResult.getShareUrl());
            sendBroadcast(intent);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        id = "";
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
