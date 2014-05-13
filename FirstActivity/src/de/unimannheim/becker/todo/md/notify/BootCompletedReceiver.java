package de.unimannheim.becker.todo.md.notify;

import de.unimannheim.becker.todo.md.CardsActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.v(CardsActivity.LOG_TAG, "starting service...");
		context.startService(new Intent(context, NotifyService.class));
	}
}