package de.leihwelt.android.droidkobanpro.storage;

import android.content.SharedPreferences;

public interface StorageStrategy {

	public void load(SharedPreferences preferences);

	public void store(SharedPreferences.Editor preferences);

}
