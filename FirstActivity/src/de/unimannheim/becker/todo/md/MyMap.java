package de.unimannheim.becker.todo.md;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.unimannheim.becker.todo.md.model.ReminderDAO;

public class MyMap implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
        OnMyLocationButtonClickListener, OnMapLongClickListener, OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private boolean addingLocation = false;
    private int itemId = 0;
    private ReminderDAO reminderDao;
    OnMapLongClickListener listener;

    public MyMap(GoogleMap mMap, Context context) {
        this.mMap = mMap;
        if (mLocationClient == null) {
            this.mLocationClient = new LocationClient(context, this, this);
            this.mLocationClient.connect();
            this.mMap.setMyLocationEnabled(true);
            this.mMap.setOnMyLocationButtonClickListener(this);
            this.mMap.setOnMapLongClickListener(this);
        }
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void setMap(GoogleMap map) {
        this.mMap = map;
    }

    // These settings are the same as the settings for the map. They will in
    // fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5
                                                                                              // seconds
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    /**
     * Button to get current Location. This demonstrates how to get the current
     * Location as required without needing to register a LocationListener.
     */
    public void showMyLocation(Context context) {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            String msg = "Location = " + mLocationClient.getLastLocation();
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default
        // behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    public LocationClient getLocationClient() {
            return mLocationClient;
    }
    
    @Override
    public void onLocationChanged(Location location) {
        // Do nothing
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // LocationListener
        mLocationClient.requestLocationUpdates(REQUEST, this);
        Location lastLocation = mLocationClient.getLastLocation();
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onDisconnected() {
        // Do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }
    
    public void startAddingLocation(int itemId, ReminderDAO reminderDao) {
        addingLocation = true;
        this.itemId = itemId;
        this.reminderDao = reminderDao;
    }

    @Override
    public void onMapLongClick(LatLng point) {
            MarkerOptions marker = new MarkerOptions().position(point).title("Added");
            mMap.addMarker(marker);
//            Reminder reminder = new Reminder();
//            reminder.setItemId(itemId);
//            reminder.setLatitude(point.latitude);
//            reminder.setLongtitude(point.longitude);
//            reminderDao.storeReminder(reminder);
        addingLocation = false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        marker.
        return false;
    }

}
