package de.leihwelt.android.droidkobanpro.select;

import de.leihwelt.android.droidkobanpro.PlayerModelSelector;
import de.leihwelt.android.droidkobanpro.chooser.DroidkobanChooser;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class SelectStoryList extends ListActivity {

	private StorySelectAdapter adapter = null;

	public void onListItemClick(ListView l, View v, int position, long id) {

	
			this.finish();
			
			Intent i2 = new Intent(this, SelectLevelList.class);
			i2.putExtra("startStory", position);
			this.startActivity(i2);	
	
		
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		adapter = new StorySelectAdapter(this);

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