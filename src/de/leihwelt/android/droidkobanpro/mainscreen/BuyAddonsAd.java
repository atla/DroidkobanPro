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

public class BuyAddonsAd extends Activity {

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

	public void showBuyAddons() {
		Intent i = new Intent(this, BuyAddons.class);
		this.startActivity(i);
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.chooser_not_available);

		Button chooserNotAvailable = (Button) findViewById(R.id.chooserNotButton);
		chooserNotAvailable.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showBuyAddons();
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

}
