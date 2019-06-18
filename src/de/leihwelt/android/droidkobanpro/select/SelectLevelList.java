package de.leihwelt.android.droidkobanpro.select;

import de.leihwelt.android.droidkobanpro.Droidkoban;
import de.leihwelt.android.droidkobanpro.PlayerModelSelector;
import de.leihwelt.android.droidkobanpro.chooser.DroidkobanChooser;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class SelectLevelList extends Activity implements OnItemClickListener {

	private LevelSelectAdapter adapter = null;
	private int story = 0;
	private GridView gridView = null;
	private View toastView;
	private ImageView image;
	private TextView text;

	public void onListItemClick(ListView l, View v, int position, long id) {

		if (position <= StatTracker.INSTANCE.getLastFinishedLevel(story)) {

			this.finish();

			if (story == 6 && PlayerModelSelector.INSTANCE.getPlayerModel() != 4) {
				Intent i2 = new Intent(this, DroidkobanChooser.class);
				i2.putExtra("MODEL_TYPE", 4);
				i2.putExtra("TEXTURE_CHOICES", 2);
				i2.putExtra("startLevel", StatTracker.INSTANCE.getLastFinishedLevel(6));
				i2.putExtra("startStory", 6);

				Toast.makeText(this, "loading model data", Toast.LENGTH_LONG).show();

				this.startActivity(i2);
			} else {
				Intent i = new Intent(this, Droidkoban.class);
				i.putExtra("startLevel", position);
				i.putExtra("startStory", story);

				
				makeLevelToast(story, null);

				this.startActivity(i);
			}

		}
	}

	public void makeLevelToast(int story, String level) {

		if (toastView == null) {
			toastView = this.getLayoutInflater().inflate(R.layout.loading, null);

			image = (ImageView) toastView.findViewById(R.id.image);
			text = (TextView) toastView.findViewById(R.id.text);

		}

		if (story == -1){
			image.setImageResource(R.drawable.play_button);	
		}
		else if (story == 6){
			image.setImageResource(R.drawable.space);
		}
		else if (story == 7){
			image.setImageResource(R.drawable.mayat);
		}
		else{
			image.setImageResource(LevelStatsInfo.INSTANCE.getStoryResourceFor(story));
		}
		
		
		
		if (level == null)
			text.setText("loading " + LevelStatsInfo.INSTANCE.getStoryNameFor(story) + ", please wait...");
		else
			text.setText("loading " + level + ", please wait...");

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.show();
	}

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (this.getIntent().hasExtra("startStory")) {
			story = this.getIntent().getIntExtra("startStory", 0);
		}

		this.setContentView(R.layout.levellistview);

		this.gridView = (GridView) this.findViewById(R.id.gridview);

		adapter = new LevelSelectAdapter(this);
		adapter.setStory(story);

		this.gridView.setAdapter(adapter);
		this.gridView.setOnItemClickListener(this);

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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (position <= StatTracker.INSTANCE.getLastFinishedLevel(story)) {

			this.finish();

			if (story == 6 && PlayerModelSelector.INSTANCE.getPlayerModel() != 4) {
				Intent i2 = new Intent(this, DroidkobanChooser.class);
				i2.putExtra("MODEL_TYPE", 4);
				i2.putExtra("TEXTURE_CHOICES", 2);
				i2.putExtra("startLevel", StatTracker.INSTANCE.getLastFinishedLevel(6));
				i2.putExtra("startStory", 6);

				Toast.makeText(this, "loading model data", Toast.LENGTH_LONG).show();

				this.startActivity(i2);
			} else {
				Intent i = new Intent(this, Droidkoban.class);
				i.putExtra("startLevel", position);
				i.putExtra("startStory", story);

				makeLevelToast(story, null);

				this.startActivity(i);
			}

		}

	}

}