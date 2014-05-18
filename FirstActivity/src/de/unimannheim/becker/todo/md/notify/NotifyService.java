package de.unimannheim.becker.todo.md.notify;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;
import de.unimannheim.becker.todo.md.CardsActivity;
import de.unimannheim.becker.todo.md.R;
import de.unimannheim.becker.todo.md.model.Location;
import de.unimannheim.becker.todo.md.model.LocationDAO;

public class NotifyService extends IntentService {
	public static final int NOTIFICATION_ID = 2311;
	public static final String SKIP_NOTIFICATION = "skipNotification";
	private static final String SKIP_GPS_REQUEST = "skipGpsRequest";
	private static final long NOTIFY_INTERVAL = 1 * 60 * 1000;

	public NotifyService() {
		super("@todo notify service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		boolean skipNotification = intent.getBooleanExtra(SKIP_NOTIFICATION, false);
		boolean skipGpsRequest = intent.getBooleanExtra(SKIP_GPS_REQUEST, false);
		
		if (skipNotification) {
			Log.v(CardsActivity.LOG_TAG,
					"skipNotification is true, just scheduling next run");
			scheduleNextRun();
			return;
		}

		// check todos in configured radius
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int r = prefs.getInt(CardsActivity.PREF_NOTIFICATION_RADIUS, CardsActivity.DEFAULT_NOTIFICATION_RADIUS);
		LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location toShow = null;

		if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			android.location.Location loc = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			if (loc != null
					&& loc.getTime() > System.currentTimeMillis()
							- NOTIFY_INTERVAL / 2) {
				Log.v(CardsActivity.LOG_TAG,
					"checking todos in radius " + r + " m from (" + loc.getLatitude() + ", " + loc.getLongitude() + ")");
			Location[] locs = new LocationDAO(getApplicationContext()).getAllLocationsForActiveItems();
				for (Location todoLoc : locs) {
					float[] results = new float[1];
				android.location.Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), todoLoc.getLatitude(),
						todoLoc.getLongtitude(), results);
					if (results[0] <= r) {
						toShow = todoLoc;
						break;
					}
				}
			} else {
				Log.v(CardsActivity.LOG_TAG, "loc is null or outdated");
				if(!skipGpsRequest) {
					Log.v(CardsActivity.LOG_TAG, "dispatching gps request");
					locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
							createPendingIntent(true));
					return;
				}
			}
		} else {
			Log.v(CardsActivity.LOG_TAG, "location service is disabled");
		}

		if (toShow != null) {
			// show notification
			NotificationManager m = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Builder b = new Builder(getApplicationContext());
			b.setContentTitle("Todos nearby");
			b.setContentText(toShow.getTitle());
			Intent i = new Intent(getApplicationContext(), CardsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 2223, i,
					PendingIntent.FLAG_CANCEL_CURRENT);
			b.setContentIntent(pi);
			b.setAutoCancel(true);
			b.setSmallIcon(R.drawable.ic_launcher);
			m.notify(NOTIFICATION_ID, b.getNotification());
			Log.v(CardsActivity.LOG_TAG, "showed notification");
		} else {
			Log.v(CardsActivity.LOG_TAG, "no todos nearby");
		}

		scheduleNextRun();
	}

	private void scheduleNextRun() {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + NOTIFY_INTERVAL, createPendingIntent(false));
	}

	private PendingIntent createPendingIntent(boolean skipGpsRequest) {
		Intent i = new Intent(getApplicationContext(), NotifyService.class);
		i.putExtra(SKIP_GPS_REQUEST, skipGpsRequest);
		PendingIntent pi = PendingIntent.getService(getApplicationContext(),
				2222, i, PendingIntent.FLAG_CANCEL_CURRENT);
		return pi;
	}

}
