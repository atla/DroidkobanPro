package de.leihwelt.android.droidkobanpro;

import java.util.Random;

import de.leihwelt.android.Helper;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AddonReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// player model changed
		if (intent.getAction().equals("de.leihwelt.android.addon.nsfjdg98z23lkjnsdfiwejb3422")) {

			int playerModel = 0;
			int playerTexture = 0;

			if (intent.hasExtra("PLAYER_MODEL") && intent.hasExtra("PLAYER_TEXTURE")) {
				playerModel = intent.getIntExtra("PLAYER_MODEL", 0);
				playerTexture = intent.getIntExtra("PLAYER_TEXTURE", 0);

			}

			SharedPreferences.Editor edit = StorageConnector.INSTANCE.getEditor();
			edit.putInt("PLAYER_MODEL", playerModel);
			edit.putInt("PLAYER_TEXTURE", playerTexture);
			edit.commit();

		} else if (intent.getAction().equals("de.leihwelt.android.addon.n235ljkn23ljnk32nb34")) {

			if (intent.hasExtra("STORY_ID") && intent.hasExtra("LEVEL_REACHED")) {

				int story = intent.getIntExtra("STORY_ID", 0);
				int level = intent.getIntExtra("LEVEL_REACHED", 1);

				SharedPreferences.Editor edit = StorageConnector.INSTANCE.getEditor();
				edit.putInt(StatTracker.PREF_LAST_FINISHED_LEVEL + story, level);
				edit.commit();

			}
		}

	}

}
