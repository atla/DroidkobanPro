package de.leihwelt.android.droidkobanpro.mainscreen;

import de.leihwelt.android.droidkobanpro.Droidkoban;
import de.leihwelt.android.droidkobanpro.chooser.DroidkobanChooser;
import de.leihwelt.android.droidkobanpro.R;
import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChoosePlayer extends Activity {

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

		setContentView(R.layout.choose_layout);

		View v = this.findViewById(R.id.choose1);

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				choose(0, 9);

			}
		});

		v = this.findViewById(R.id.choose2);

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				choose(1, 7);

			}
		});

		v = this.findViewById(R.id.choose3);

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				choose(2, 6);

			}
		});

		v = this.findViewById(R.id.choose4);

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				choose(3, 9);

			}
		});

		v = this.findViewById(R.id.choose5);

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				choose(4, 1);

			}
		});

	}

	public void choose(int model, int maxtex) {

		Intent i = new Intent(this, DroidkobanChooser.class);
		i.putExtra("MODEL_TYPE", model);
		i.putExtra("TEXTURE_CHOICES", maxtex);

		Toast.makeText(this, "loading model data", Toast.LENGTH_LONG).show();
		
		this.startActivity(i);
		this.finish();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

}
