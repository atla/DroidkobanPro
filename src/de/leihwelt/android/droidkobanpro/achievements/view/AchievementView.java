package de.leihwelt.android.droidkobanpro.achievements.view;

import de.leihwelt.android.droidkobanpro.Droidkoban;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.R;
import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AchievementView extends Activity {

	private AchievementSelectAdapter adapter = null;

	@Override
	protected void onResume() {
		this.adapter.notifyDataSetChanged();
		super.onResume();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public void back() {
		this.finish();
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.achievementview);

		adapter = new AchievementSelectAdapter(this);

		AchievementUnlocker ach = AchievementUnlocker.INSTANCE;

		ListView listView = (ListView) findViewById(R.id.AchievementList);
		listView.setAdapter(this.adapter);

		TextView points = (TextView) findViewById(R.id.PointsTextView);
		points.setText(ach.getScore() + " points");

		TextView completed = (TextView) findViewById(R.id.CompletedTextView);
		completed.setText("Completed " + ach.getAchievementsCompleted() + "/" + ach.getAchievementCount());

		LinearLayout view = (LinearLayout) findViewById(R.id.AchieveHeader);
		ProgressBar bar = new ProgressBar(this.getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);

		if (ach.allCompleted()) {
			bar.setProgress(bar.getMax());
		} else {
			int val = bar.getMax() / ach.getAchievementCount();

			bar.setProgress(val * ach.getAchievementsCompleted());
		}

		bar.setBackgroundColor(0xFFFFFF);
		bar.setPadding(5, 0, 5, 12);
		view.addView(bar);

		// TextView mainMenu = (TextView) findViewById(R.id.aboutText);
		//
		// mainMenu.setOnTouchListener(new View.OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// back();
		// return true;
		// }
		// });

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

}
