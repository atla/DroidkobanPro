package de.leihwelt.android.droidkobanpro.mainscreen;

import de.leihwelt.android.droidkobanpro.R;
import de.leihwelt.android.droidkobanpro.Droidkoban;
import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class About extends Activity {

	@Override
	protected void onResume() {

		super.onResume();
	}

	public boolean onCreateOptionsMenu(MainMenu menu) {
		return true;
	}

	public void back() {
		this.finish();
	}

	public void showChangelog (){
		Intent i = new Intent(this, Changelog.class);
		this.startActivity(i);
	}
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);
		TextView mainMenu = (TextView) findViewById(R.id.aboutText);

		Button changelog = (Button) findViewById(R.id.changelogButton);
		changelog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showChangelog();
			}
		});
		
		mainMenu.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				back();
				return true;
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

}
