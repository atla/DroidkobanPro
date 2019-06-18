package de.leihwelt.android.droidkobanpro.mainscreen;

import java.util.List;

import com.openfeint.api.OpenFeint;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.OpenFeintSettings;
import com.openfeint.api.resource.CurrentUser;
import com.openfeint.api.ui.Dashboard;

import de.leihwelt.android.Helper;
import de.leihwelt.android.droidkobanpro.*;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.achievements.view.AchievementView;
import de.leihwelt.android.droidkobanpro.chooser.DroidkobanChooser;
import de.leihwelt.android.droidkobanpro.select.LevelStatsInfo;
import de.leihwelt.android.droidkobanpro.select.SelectLevelList;
import de.leihwelt.android.droidkobanpro.select.SelectStoryList;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.droidkobanpro.storage.StorageStrategy;
import de.leihwelt.android.droidkobanpro.R;

import de.leihwelt.android.utilities.Base64Coder;
import android.app.*;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends Activity {

	private static final int MENU_TOGGLE_SOUNDS = 1;
	private static final int MENU_TOGGLE_MOVEMENT = 2;

	private Intent gameIntent = null;
	private View toastView = null;
	private ImageView image = null;
	private TextView text = null;

	@Override
	protected void onResume() {
		super.onResume();

		OpenFeint.setCurrentActivity(this);

		if (gameIntent == null)
			this.gameIntent = new Intent(this, Droidkoban.class);

		StorageConnector.INSTANCE.setContext(this.getApplicationContext());
		StorageConnector.INSTANCE.load();

		if (SoundManager.INSTANCE != null)
			SoundManager.INSTANCE.play(this.getApplicationContext());

		ImageView of = (ImageView) findViewById(R.id.ofButton);
		of.setImageResource(OpenFeint.isUserLoggedIn() ? R.drawable.of : R.drawable.ofd);

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case MENU_TOGGLE_SOUNDS:

			SoundManager.INSTANCE.toggleMusic(this.getApplicationContext());

			break;
		case MENU_TOGGLE_MOVEMENT:
			boolean pathfinding = StorageConnector.INSTANCE.getPreferences().getBoolean("dkMoveByPath", true);

			Editor edit = StorageConnector.INSTANCE.getEditor();
			edit.putBoolean("dkMoveByPath", !pathfinding);
			edit.commit();

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	protected void onPause() {
		super.onPause();

		SoundManager.INSTANCE.stop(this.getApplicationContext());

		StorageConnector.INSTANCE.store();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	public void openWebURL(String inURL) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));

		startActivity(browse);
	}

	public void playResume() {

		int story = this.getPreferences().getInt("dkCurrentStory", 0);

		if (story == 6 && PlayerModelSelector.INSTANCE.getPlayerModel() != 4) {
			Intent i2 = new Intent(this, DroidkobanChooser.class);
			i2.putExtra("MODEL_TYPE", 4);
			i2.putExtra("TEXTURE_CHOICES", 2);
			i2.putExtra("startLevel", StatTracker.INSTANCE.getLastFinishedLevel(6));
			i2.putExtra("startStory", 6);

			Toast.makeText(this, "loading model data", Toast.LENGTH_LONG).show();

			this.startActivity(i2);
		} else {
			
			makeLevelToast(-1, "last level");

			this.startActivity(this.gameIntent);

		}

	}

	public void showInstructions() {
		Intent i = new Intent(this, Instructions.class);
		this.startActivity(i);
	}

	public void showSettings() {
		Intent i = new Intent(this, Settings.class);
		this.startActivity(i);
	}

	public void showAbout() {
		Intent i = new Intent(this, About.class);
		this.startActivity(i);
	}

	public void showAchievements() {
		Intent i = new Intent(this, AchievementView.class);
		this.startActivity(i);
	}

	public void showSelectStory() {
		Intent i = new Intent(this, SelectStoryList.class);
		this.startActivity(i);

	}

	public void showSelectLevel() {
		Intent i = new Intent(this, SelectLevelList.class);
		this.startActivity(i);
	}

	public void showSetup() {
		Intent i = new Intent(this, TwitterSetup.class);
		this.startActivity(i);
	}

	public void showVideos() {
		Intent i = new Intent(this, Videos.class);
		this.startActivity(i);
	}

	public static final String CUSTOMIZE_DROIDKOBAN_3D_PLAYER = "de.leihwelt.android.droidkobanpro.CUSTOMIZE_PLAYER";

	public void chooseModel() {

		this.startActivity(new Intent(this, ChoosePlayer.class));
		//		
		// if (Helper.isIntentAvailable(this, CUSTOMIZE_DROIDKOBAN_3D_PLAYER)) {
		// Intent i = new Intent(CUSTOMIZE_DROIDKOBAN_3D_PLAYER);
		//
		// this.startActivity(Intent.createChooser(i, "Which Model-Type?"));
		// } else {
		// this.startActivity(new Intent(this, BuyAddonsAd.class));
		// }

	}

	public void buyAddons() {
		this.startActivity(new Intent(this, BuyAddons.class));
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set htc desire test device
		// AdManager.setTestDevices( new String[] {
		// "352CA38E5504DDDCCE284C9267663162" } );

		StorageConnector.INSTANCE.setContext(this.getApplicationContext());
		StorageConnector.INSTANCE.addStorageStrategy(AchievementUnlocker.INSTANCE);
		StorageConnector.INSTANCE.addStorageStrategy(StatTracker.INSTANCE);
		StorageConnector.INSTANCE.addStorageStrategy(AppSettings.INSTANCE);

		Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		DroidkobanGame.INSTANCE.vibrator = vib;

		setContentView(R.layout.mainmenu);

		final ImageView of = (ImageView) findViewById(R.id.ofButton);

		OpenFeintSettings ofSettings = new OpenFeintSettings("Droidkoban 3D", "KRM1uTwlR79zC53htALiA", "CiqVp0mvwCQgJuzou0391cu0IwwSrV4FHvFx85tUcTY", "188312");

		OpenFeint.initializeWithoutLoggingIn(this, ofSettings, new OpenFeintDelegate() {

			@Override
			public void userLoggedIn(CurrentUser user) {

				of.setImageResource(R.drawable.of);

				AppSettings.INSTANCE.setFeintEnabled(true);

				Editor editor = StorageConnector.INSTANCE.getEditor();
				AppSettings.INSTANCE.store(editor);
				editor.commit();

			}

		});

		if (AppSettings.INSTANCE.isFeintEnabled()) {
			OpenFeint.login();
		}

		View playResumeButton = (View) findViewById(R.id.PlayResume);
		playResumeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				playResume();

			}
		});

		View fbButton = (View) findViewById(R.id.facebookButton);
		fbButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/pages/Droidkoban-3D/151680671511986")));

			}
		});

		View achieve = (View) findViewById(R.id.achieveButton);
		achieve.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showAchievements();

			}
		});

		View instructions = (View) findViewById(R.id.instructions);
		instructions.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showInstructions();
			}
		});

		View settings = (View) findViewById(R.id.settings);
		settings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showSettings();
			}
		});

		View about = (View) findViewById(R.id.about);
		about.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showAbout();
			}
		});

		View select = (View) findViewById(R.id.Select);
		select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// showSelectLevel();
				showSelectStory();
			}

		});

		View videos = (View) findViewById(R.id.videos);
		videos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// showSelectLevel();
				showVideos();
			}

		});

		//
		// View setup = (View) findViewById(R.id.twitter);
		// setup.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// showSetup();
		// }
		// });

		// View donate = (View) findViewById(R.id.donate);
		// donate.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// openWebURL("market://search?q=Droidkoban Donate");
		// }
		// });
		//
		// View addons = (View) findViewById(R.id.addons);
		// addons.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// buyAddons();
		// }
		// });
		//
		// View addons2 = (View) findViewById(R.id.addons2);
		// addons2.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// buyAddons();
		// }
		// });

		View chooseModel = (View) findViewById(R.id.chooseButton);
		chooseModel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseModel();
			}
		});

		of.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Dashboard.open();

			}
		});

		View space = (View) findViewById(R.id.spaceButton);
		space.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// startSpace();
				startMaya();

			}
		});

		
		// OpenFeint.initialize(this, null, new OpenFeintDelegate() {});

		AchievementUnlocker.INSTANCE.addListener(new OpenFeintAchievementUnlocker());

	}

	public void startMaya() {
		this.gameIntent.putExtra("startLevel", StatTracker.INSTANCE.getLastFinishedLevel(7));
		this.gameIntent.putExtra("startStory", 7);

		makeLevelToast(7, "Maya Temple");

		this.startActivity(this.gameIntent);
	}

	public void makeLevelToast(int story, String level) {

		if (toastView == null) {
			toastView = this.getLayoutInflater().inflate(R.layout.loading, null);

			image = (ImageView) toastView.findViewById(R.id.image);
			text = (TextView) toastView.findViewById(R.id.text);

		}

		if (story == -1){
			image.setImageResource(R.drawable.play_button);	
		}
		else if (story == 6){
			image.setImageResource(R.drawable.space);
		}
		else if (story == 7){
			image.setImageResource(R.drawable.mayat);
		}
		else{
			image.setImageResource(LevelStatsInfo.INSTANCE.getStoryResourceFor(story));
		}
		
		
		
		if (level == null)
			text.setText("loading " + LevelStatsInfo.INSTANCE.getStoryNameFor(story) + ", please wait...");
		else
			text.setText("loading " + level + ", please wait...");

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.show();
	}

	public void startSpace() {

		if (PlayerModelSelector.INSTANCE.getPlayerModel() != 4) {
			Intent i2 = new Intent(this, DroidkobanChooser.class);
			i2.putExtra("MODEL_TYPE", 4);
			i2.putExtra("TEXTURE_CHOICES", 2);
			i2.putExtra("startLevel", StatTracker.INSTANCE.getLastFinishedLevel(6));
			i2.putExtra("startStory", 6);

			Toast.makeText(this, "loading model data", Toast.LENGTH_LONG).show();

			this.startActivity(i2);
		} else {

			this.gameIntent.putExtra("startLevel", StatTracker.INSTANCE.getLastFinishedLevel(6));
			this.gameIntent.putExtra("startStory", 6);

			Toast.makeText(this, "loading Space Addon, please wait...", Toast.LENGTH_LONG).show();

			this.startActivity(this.gameIntent);

		}

	}

	public SharedPreferences getPreferences() {
		return StorageConnector.INSTANCE.getPreferences();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

	}

}
