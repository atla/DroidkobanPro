package de.leihwelt.android.droidkobanpro.mainscreen;

import de.leihwelt.android.droidkobanpro.AppSettings;
import de.leihwelt.android.droidkobanpro.DroidkobanGame;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.R;
import de.leihwelt.android.social.TwitterConnect;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TwitterSetup extends Activity {

	@Override
	protected void onResume() {

		super.onResume();
	}

	public void back() {
		this.finish();
	}

	public void onPause() {
		SharedPreferences prefs = this.getSharedPreferences("DroidkobanPrefs", 0);
		Editor editor = prefs.edit();

		AppSettings.INSTANCE.store(editor);

		editor.commit();

		super.onPause();

	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.twittersetup);

		boolean enabled = AppSettings.INSTANCE.isTwitterEnabled();

		final EditText twitterUserView = (EditText) findViewById(R.id.twitterUsername);
		final EditText twitterPasswordView = (EditText) findViewById(R.id.twitterPassword);

		twitterUserView.setEnabled(enabled);
		twitterPasswordView.setEnabled(enabled);

		if (enabled) {
			twitterUserView.setText(AppSettings.INSTANCE.getTwitterUsername());
			twitterPasswordView.setText(AppSettings.INSTANCE.getTwitterPassword());
		}

		final CheckBox twitterEnable = (CheckBox) findViewById(R.id.twitterEnable);
		twitterEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				twitterUserView.setEnabled(isChecked);
				twitterPasswordView.setEnabled(isChecked);

				AppSettings.INSTANCE.setTwitterEnabled(isChecked);
			}

		});

		twitterEnable.setChecked(enabled);

		final Button acceptButton = (Button) findViewById(R.id.twitterAcceptButton);

		acceptButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				boolean checked = twitterEnable.isChecked();

				if (checked) {

					String user, pass;
					user = twitterUserView.getText().toString();
					pass = twitterPasswordView.getText().toString();

					AppSettings.INSTANCE.setTwitterUsername(user);
					AppSettings.INSTANCE.setTwitterPassword(pass);

					TwitterConnect.INSTANCE.setCredentials(user, pass);
				}

				finish();
			}

		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

	}

}
