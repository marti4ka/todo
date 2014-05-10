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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.unimannheim.becker.todo.md.model.LocationDAO;

public class MyMap implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
        OnMyLocationButtonClickListener, OnMapLongClickListener, OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private boolean addingLocation = false;
    private long itemId = 0;
    private long locationId = 0;
    private LocationDAO locationDao;
    OnMapLongClickListener listener;

    public MyMap(GoogleMap mMap, Context context) {
        this.mMap = mMap;
        if (mLocationClient == null) {
            this.mLocationClient = new LocationClient(context, this, this);
            this.mLocationClient.connect();
            this.mMap.setMyLocationEnabled(true);
            this.mMap.setOnMyLocationButtonClickListener(this);
            this.mMap.setOnMapLongClickListener(this);
            this.mMap.setOnMarkerClickListener(this);
        }
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void setMap(GoogleMap map) {
        this.mMap = map;
    }

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

    public void startAddingLocation(long itemId, LocationDAO reminderDao) {
        addingLocation = true;
        this.itemId = itemId;
        this.locationDao = reminderDao;
        addLocationsToMap(locationDao);
    }
    
    public void addLocationsToMap(LocationDAO reminderDao) {
//        de.unimannheim.becker.todo.md.model.Location[] locations = locationDao.getAll();
//        for (de.unimannheim.becker.todo.md.model.Location l : locations) {
//            
//            MarkerOptions marker = new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongtitude()));
//            // TODO change color of marker to blue
//            mMap.addMarker(marker);
//        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        // TODO if (addingLocation) {
        MarkerOptions marker = new MarkerOptions().position(point).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        de.unimannheim.becker.todo.md.model.Location location = new de.unimannheim.becker.todo.md.model.Location(
                point.latitude, point.longitude);
        locationId = location.getId();
        locationDao.storeLocation(location);

        mMap.addMarker(marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (locationId != 0) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            locationDao.mapLocationToItem(locationId, itemId);
        }
        return false;
    }

}
