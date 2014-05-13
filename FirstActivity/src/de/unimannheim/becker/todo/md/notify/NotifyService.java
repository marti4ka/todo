package de.unimannheim.becker.todo.md.notify;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import de.unimannheim.becker.todo.md.CardsActivity;
import de.unimannheim.becker.todo.md.R;

@SuppressLint("NewApi")
public class NotifyService extends IntentService {
	public static final int NOTIFICATION_ID = 2311;
	private static final long NOTIFY_INTERVAL = 1 * 60 * 1000;

	public NotifyService() {
		super("@todo notify service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// check todos in configured radius
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int r = prefs.getInt(CardsActivity.PREF_NOTIFICATION_RADIUS, CardsActivity.DEFAULT_NOTIFICATION_RADIUS);
		Log.v(CardsActivity.LOG_TAG, "checking todos in radius " + r + " m");

		// show notification
		NotificationManager m = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Builder b = new Builder(getApplicationContext());
		b.setContentTitle("Todos nearby");
		b.setContentText("Buy beer");
		Intent i = new Intent(getApplicationContext(), CardsActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 2223, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		b.setContentIntent(pi);
		b.setAutoCancel(true);
		b.setSmallIcon(R.drawable.ic_launcher);
		m.notify(NOTIFICATION_ID, b.build());
		Log.v(CardsActivity.LOG_TAG, "showed notification");

		scheduleNextRun();
	}

	private void scheduleNextRun() {
		Intent i = new Intent(getApplicationContext(), NotifyService.class);
		PendingIntent pi = PendingIntent
				.getService(getApplicationContext(), 2222, i, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + NOTIFY_INTERVAL, pi);
	}

}
