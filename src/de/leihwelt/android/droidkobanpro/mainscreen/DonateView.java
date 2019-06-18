package de.leihwelt.android.droidkobanpro.mainscreen;

import de.leihwelt.android.droidkobanpro.R;
import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DonateView extends Activity {

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

		setContentView(R.layout.hello_activity);
		View mainMenu = (View) findViewById(R.id.donateView);

		mainMenu.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				back();
				return true;
			}
		});

		Button donate = (Button) findViewById(R.id.DonateButton);
		donate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// back ();

				openWebURL("market://search?q=Droidkoban Donate");
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

	}

	public void openWebURL(String inURL) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));

		startActivity(browse);
	}

}
