package de.leihwelt.android.droidkobanpro.mainscreen;

import de.leihwelt.android.droidkobanpro.DroidkobanGame;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.droidkobanpro.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Settings extends Activity {

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		setContentView(R.layout.settings);

		CheckBox movementOptionCheckbox = (CheckBox) findViewById(R.id.movementOption);
		movementOptionCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						Editor edit = StorageConnector.INSTANCE.getEditor();
						edit.putBoolean("dkMoveByPath", isChecked);
						edit.commit();
					}
				});

		boolean pathfinding = StorageConnector.INSTANCE.getPreferences()
				.getBoolean("dkMoveByPath", true);

		movementOptionCheckbox.setChecked(pathfinding);

		CheckBox musicOptionCheckbox = (CheckBox) findViewById(R.id.musicOption);
		musicOptionCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						Editor edit = StorageConnector.INSTANCE.getEditor();
						edit.putBoolean("musicEnabled", isChecked);
						edit.commit();
					}
				});

		boolean musicEnabled = StorageConnector.INSTANCE.getPreferences()
				.getBoolean("musicEnabled", true);

		musicOptionCheckbox.setChecked(musicEnabled);

		CheckBox screenOptionCheckbox = (CheckBox) findViewById(R.id.screenOption);
		screenOptionCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						Editor edit = StorageConnector.INSTANCE.getEditor();
						edit.putBoolean("mode2d", isChecked);
						edit.commit();
					}
				});

		boolean mode2dEnabled = StorageConnector.INSTANCE.getPreferences()
				.getBoolean("mode2d", false);

		screenOptionCheckbox.setChecked(mode2dEnabled);
		
		CheckBox feintOptionCheckbox = (CheckBox) findViewById(R.id.feintOption);
		feintOptionCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						Editor edit = StorageConnector.INSTANCE.getEditor();
						edit.putBoolean("feintEnabled", isChecked);
						edit.commit();
					}
				});

		boolean feintEnabled = StorageConnector.INSTANCE.getPreferences()
				.getBoolean("feintEnabled", false);

		feintOptionCheckbox.setChecked(feintEnabled);
		
		CheckBox hideUIOptionCheckbox = (CheckBox) findViewById(R.id.hideUI);
		hideUIOptionCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						Editor edit = StorageConnector.INSTANCE.getEditor();
						edit.putBoolean("hideUI", isChecked);
						edit.commit();
					}
				});

		boolean hideUI = StorageConnector.INSTANCE.getPreferences()
				.getBoolean("hideUI", false);

		hideUIOptionCheckbox.setChecked(hideUI);
		

	}

}
