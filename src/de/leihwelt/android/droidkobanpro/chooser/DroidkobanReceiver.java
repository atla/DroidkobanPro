package de.leihwelt.android.droidkobanpro.chooser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DroidkobanReceiver extends BroadcastReceiver {

	public final static String START_INTENT = "de.leihwelt.droidkobanchooser.CHOOSER";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("de.leihwelt.android.droidkobanpro.CUSTOMIZE_PLAYER")) {

			Intent i = new Intent(context, DroidkobanChooser.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);

		}
	}

}
