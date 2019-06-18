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

public class Changelog extends Activity {

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

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.changelog);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

}
