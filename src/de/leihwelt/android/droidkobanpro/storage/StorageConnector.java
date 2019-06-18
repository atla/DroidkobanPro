package de.leihwelt.android.droidkobanpro.storage;

import java.util.HashSet;
import java.util.Set;

import de.leihwelt.android.droidkobanpro.Droidkoban;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public enum StorageConnector {
	INSTANCE;

	private Set<StorageStrategy> strategies = new HashSet<StorageStrategy>();

	private Context ctx = null;

	private static final String PREFS_NAME = "DroidkobanPrefsPro";

	public void addStorageStrategy(StorageStrategy storageStrategy) {
		this.strategies.add(storageStrategy);
	}

	public void setContext(Context ctx) {
		this.ctx = ctx;
	}

	public SharedPreferences getPreferences() {

		if (this.ctx == null)
			return Droidkoban.INSTANCE.getSharedPreferences(PREFS_NAME, 0);

		return this.ctx.getSharedPreferences(PREFS_NAME, 0);
	}

	public void store() {

		Editor editor = this.getPreferences().edit();

		for (StorageStrategy strat : this.strategies) {
			strat.store(editor);
		}

		editor.commit();
	}

	public void load() {
		SharedPreferences prefs = this.getPreferences();

		for (StorageStrategy strat : this.strategies) {
			strat.load(prefs);
		}
	}

	public SharedPreferences.Editor getEditor() {
		return this.getPreferences().edit();
	}

}
