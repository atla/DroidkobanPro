package de.leihwelt.android.droidkobanpro.mainscreen;

import de.leihwelt.android.droidkobanpro.R;
import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class Instructions extends Activity {

	@Override
	protected void onResume() {

		super.onResume();
	}

	public void back() {
		this.finish();
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.instructions);
		TextView mainMenu = (TextView) findViewById(R.id.instructionsText);

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
