package de.leihwelt.android.droidkobanpro;

import android.content.SharedPreferences;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;

public enum PlayerModelSelector {
	INSTANCE;

	public static String MODEL_LOAD_PATH = "models/droids/";

	public static boolean hovers[] = { true, false, true, false, true, false };

	public String getPlayerModelNormal() {

		int model = getPlayerModel();

		return MODEL_LOAD_PATH + model + "/m.obj";
	}

	public boolean hovers() {
		return hovers[getPlayerModel()];
	}

	public String getPlayerModelMove() {

		int model = getPlayerModel();
 
		return MODEL_LOAD_PATH + model + "/m_move.obj";
	}

	public String getPlayerTex() {

		int model = getPlayerModel();
		int tex = getPlayerTexture();

		return MODEL_LOAD_PATH + model + "/" + tex + ".png";
	}

	private int getPlayerTexture() {

		SharedPreferences prefs = StorageConnector.INSTANCE.getPreferences();

		if (!prefs.contains("PLAYER_TEXTURE") || !prefs.contains("PLAYER_MODEL")) {

			SharedPreferences.Editor edit = StorageConnector.INSTANCE.getEditor();
			edit.putInt("PLAYER_MODEL", 0);
			edit.putInt("PLAYER_TEXTURE", 0);
			edit.commit();
		}

		return prefs.getInt("PLAYER_TEXTURE", 0);
	}

	public int getPlayerModel() {

		SharedPreferences prefs = StorageConnector.INSTANCE.getPreferences();

		if (!prefs.contains("PLAYER_TEXTURE") || !prefs.contains("PLAYER_MODEL")) {

			SharedPreferences.Editor edit = StorageConnector.INSTANCE.getEditor();
			edit.putInt("PLAYER_MODEL", 0);
			edit.putInt("PLAYER_TEXTURE", 0);
			edit.commit();
		}

		return prefs.getInt("PLAYER_MODEL", 0);
	}
}