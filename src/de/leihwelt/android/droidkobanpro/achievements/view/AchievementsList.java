package de.leihwelt.android.droidkobanpro.achievements.view;

import de.leihwelt.android.droidkobanpro.Droidkoban;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class AchievementsList extends ListActivity {

	private AchievementSelectAdapter adapter = null;

	public void onListItemClick(ListView l, View v, int position, long id) {

//		this.finish();
//
//		Intent i = new Intent(this, Droidkoban.class);
//		i.putExtra("startLevel", position);
//
//		this.startActivity(i);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		adapter = new AchievementSelectAdapter(this);

		setListAdapter(adapter);
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {

		this.adapter.notifyDataSetChanged();

		super.onResume();
	}

}