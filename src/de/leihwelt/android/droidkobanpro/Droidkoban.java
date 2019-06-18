package de.leihwelt.android.droidkobanpro;

import com.openfeint.api.OpenFeint;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.OpenFeintSettings;
import com.openfeint.api.resource.CurrentUser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import de.leihwelt.android.Helper;
import de.leihwelt.android.droidkobanpro.SoundManager;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.select.LevelStatsInfo;
import de.leihwelt.android.droidkobanpro.select.StoryInfo;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.droidkobanpro.R;

public class Droidkoban extends Activity {

	private static final String START_LEVEL = "startLevel";
	private static final String START_STORY = "startStory";

	private static final int MENU_ENABLE_LIGHTING = 0;
	private static final int MENU_ENABLE_BACKGROUNDS = 1;
	private static final int MENU_HIDE_UI = 2;

	public static Droidkoban INSTANCE;

	public Droidkoban() {
		DroidkobanGame.INSTANCE.activity = this;
		INSTANCE = this;
	}

	public static final String CUSTOMIZE_DROIDKOBAN_3D_PLAYER = "de.leihwelt.android.droidkobanpro.CUSTOMIZE_PLAYER";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Log.v("DROIDKOBAN", "ON CREATE");

		// AdManager.setTestDevices( new String[] {
		// "352CA38E5504DDDCCE284C9267663162" } );

		// resetPlayerModelCheck();

		DroidkobanGame.INSTANCE.activity = this;

		mGLSurfaceView = new DroidkobanView(this);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);

		setContentView(mGLSurfaceView);


		DroidkobanGame.INSTANCE.renderer = mGLSurfaceView.getRenderer();

		if (this.getIntent().hasExtra(START_LEVEL)) {

			int lvl = this.getIntent().getIntExtra(START_LEVEL, 0);
			int story = this.getIntent().getIntExtra(START_STORY, 0);

			
			Log.v ("DROIDKOBANGAME", "LVL " + lvl);
			Log.v ("DROIDKOBANGAME", "storyL " + story);
			
			StoryInfo info = LevelStatsInfo.INSTANCE.storyInfos.get(story);

			DroidkobanGame.INSTANCE.setCurrentStory(info);
			DroidkobanGame.INSTANCE.setToLevel(lvl);
		} else {

			DroidkobanGame.INSTANCE.doResume();
		}
		
		mGLSurfaceView.getRenderer().setupObjects();
	}

	//
	// private void resetPlayerModelCheck() {
	// if (!Helper.isIntentAvailable(this, CUSTOMIZE_DROIDKOBAN_3D_PLAYER)) {
	// SharedPreferences.Editor edit = StorageConnector.INSTANCE.getEditor();
	// edit.putInt("PLAYER_MODEL", 0);
	// edit.putInt("PLAYER_TEXTURE", 0);
	// edit.commit();
	// }
	// }

	@Override
	protected void onResume() {

		super.onResume();
		mGLSurfaceView.onResume();

		Log.v("DROIDKOBAN", "ON RESUME");

		StorageConnector.INSTANCE.setContext(this.getApplicationContext());
		SharedPreferences prefs = StorageConnector.INSTANCE.getPreferences();

		DroidkobanGame.INSTANCE.activity = this;

		DroidkobanGame.INSTANCE.resume(prefs);

		// DroidkobanGame.INSTANCE.renderer.reload = true;

		SoundManager.INSTANCE.play(this.getApplicationContext());

		if (AppSettings.INSTANCE.isFeintEnabled()) {
			OpenFeintSettings ofSettings = new OpenFeintSettings("Droidkoban 3D", "", "",
					"");
			OpenFeint.initialize(this, ofSettings, new OpenFeintDelegate() {

				@Override
				public void userLoggedIn(CurrentUser user) {

				}

			});

			OpenFeint.setCurrentActivity(this);
		}

	}

	protected void onStop() {
		super.onStop();

	}

	protected void onDestroy() {

		super.onDestroy();
		
		Log.v ("DROIDKOBAN", "DESTROYED");
	}

	@Override
	protected void onPause() {

		super.onPause();

		Editor editor = StorageConnector.INSTANCE.getEditor();
		DroidkobanGame.INSTANCE.save(editor);
		editor.commit();

		StorageConnector.INSTANCE.store();

		SoundManager.INSTANCE.stop(this.getApplicationContext());

		mGLSurfaceView.onPause();

		DroidkobanGame.INSTANCE.doResume();

		// TODO: what do about it?

		// this.finish();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ENABLE_BACKGROUNDS, 0, "Turn Backgrounds On/Off");
		menu.add(0, MENU_HIDE_UI, 1, "Toggle Hide UI");
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case MENU_ENABLE_LIGHTING:

			AppSettings.INSTANCE.setEnableLighting(!AppSettings.INSTANCE.isEnableLighting());

			break;
		case MENU_ENABLE_BACKGROUNDS:
			AppSettings.INSTANCE.setEnableBackgrounds(!AppSettings.INSTANCE.isEnableBackgrounds());

			if (AppSettings.INSTANCE.isEnableBackgrounds())
				this.mGLSurfaceView.onBackgroundsEnabled();

			break;

		
		case MENU_HIDE_UI:
			AppSettings.INSTANCE.setHideUI(!AppSettings.INSTANCE.isHideUI());

			if (DroidkobanGame.INSTANCE.renderer != null){
				DroidkobanGame.INSTANCE.renderer.hud.setHidden(AppSettings.INSTANCE.isHideUI());
			}
			
			break;
	}
		return super.onOptionsItemSelected(item);
	}

	private DroidkobanView mGLSurfaceView;
}
