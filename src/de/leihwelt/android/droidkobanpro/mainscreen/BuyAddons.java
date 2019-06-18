package de.leihwelt.android.droidkobanpro.mainscreen;

import de.leihwelt.android.Helper;
import de.leihwelt.android.droidkobanpro.R;
import de.leihwelt.android.droidkobanpro.Droidkoban;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BuyAddons extends Activity {

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

	public void showChangelog() {
		Intent i = new Intent(this, Changelog.class);
		this.startActivity(i);
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.buy_addons);

		// Button chooserNotAvailable = (Button)
		// findViewById(R.id.chooserNotButton);
		// chooserNotAvailable.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// showChangelog();
		// }
		// });

		final Activity ctx = this;

		View buyAddon1 = (View) findViewById(R.id.buyAddon1);
		buyAddon1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Helper.openWebURL("market://search?q=de.leihwelt.android.droidchooser1", ctx);
			}
		});

		View buyAddon2 = (View) findViewById(R.id.buyAddon2);
		buyAddon2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Helper.openWebURL("market://search?q=de.leihwelt.android.droidchooser2", ctx);
			}
		});

		View buyAddon3 = (View) findViewById(R.id.buyAddon3);
		buyAddon3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Helper.openWebURL("market://search?q=de.leihwelt.android.droidchooser3", ctx);
			}
		});
		
		View buyAddon4 = (View) findViewById(R.id.buyAddon4);
		buyAddon4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Helper.openWebURL("market://search?q=de.leihwelt.android.droidchooser_4", ctx);
			}
		});

		View title = (View) findViewById(R.id.buyAddonsTitle);
		title.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ctx.finish();
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

}
