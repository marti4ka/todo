package de.unimannheim.becker.todo.md;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.unimannheim.becker.todo.md.model.LocationDAO;

public class MyMap implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener, OnMapLongClickListener, OnMarkerClickListener {

	public static final int NO_ITEM = 0;
	private static final BitmapDescriptor MAPPED_ICON = BitmapDescriptorFactory
			.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
	private static final BitmapDescriptor UNMAPPED_ICON = BitmapDescriptorFactory
			.defaultMarker(BitmapDescriptorFactory.HUE_RED);

	private final Map<Marker, de.unimannheim.becker.todo.md.model.Location> marker2location = new HashMap<Marker, de.unimannheim.becker.todo.md.model.Location>();
	private final LocationDAO locationDao;
	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private long itemId = 0;
	OnMapLongClickListener listener;

	public MyMap(GoogleMap mMap, Context context, LocationDAO locationDAO) {
		this.mMap = mMap;
		this.locationDao = locationDAO;
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

	public void setItemId(long itemId) {
		this.itemId = itemId;
		// addLocationsToMap();
	}

	public void addLocationsToMap() {
		marker2location.clear();
		mMap.clear();

		ArrayList<de.unimannheim.becker.todo.md.model.Location> all = new ArrayList<de.unimannheim.becker.todo.md.model.Location>();
		ArrayList<de.unimannheim.becker.todo.md.model.Location> forItem = new ArrayList<de.unimannheim.becker.todo.md.model.Location>();
		if (itemId != NO_ITEM) {
			all.addAll(Arrays.asList(locationDao.getAll()));
			forItem.addAll(Arrays.asList(locationDao.getLocationsForItem(itemId)));
			all.removeAll(forItem);
		} else {
			all.addAll(Arrays.asList(locationDao.getAllLocationsForActiveItems()));
		}

		for (de.unimannheim.becker.todo.md.model.Location l : all) {
			MarkerOptions marker = new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongtitude()))
					.icon(UNMAPPED_ICON).title(l.getTitle());
			marker2location.put(mMap.addMarker(marker), l);
		}
		for (de.unimannheim.becker.todo.md.model.Location l : forItem) {
			MarkerOptions marker = new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongtitude()))
					.icon(MAPPED_ICON).title(l.getTitle());
			marker2location.put(mMap.addMarker(marker), l);
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {
		if (itemId == NO_ITEM) {
			// no locations can be added
			return;
		}

		de.unimannheim.becker.todo.md.model.Location location = new de.unimannheim.becker.todo.md.model.Location(
				point.latitude, point.longitude);
		locationDao.storeLocation(location);

		MarkerOptions marker = new MarkerOptions().position(point).icon(UNMAPPED_ICON);
		marker2location.put(mMap.addMarker(marker), location);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (itemId != NO_ITEM) {
			// map or unmap location to current item
			de.unimannheim.becker.todo.md.model.Location l = marker2location.get(marker);
			if (l == null) {
				Log.w("todo", "Location not found for clicked marker.");
				return false;
			}

			if (l.isMapped()) {
				marker.setIcon(UNMAPPED_ICON);
				l.setMapped(false);
				locationDao.unMapLocationToItem(l.getId(), itemId);
			} else {
				marker.setIcon(MAPPED_ICON);
				l.setMapped(true);
				locationDao.mapLocationToItem(l.getId(), itemId);
			}
			return true;
		}
		return false;
	}

}
