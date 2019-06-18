package de.leihwelt.android.droidkobanpro.chooser;

import de.leihwelt.android.droidkobanpro.Droidkoban;
import de.leihwelt.android.droidkobanpro.select.LevelStatsInfo;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DroidkobanChooser extends Activity {

	public static Activity DROIDKOBAN;

	public static int MAX_TEXTURE_CHOICES = 2;
	public static int MODEL_NUMBER = 0;

	private int startLevel = -1;
	private int startStory = -1;
	private boolean gotoGame = false;

	private View toastView;

	private ImageView image;

	private TextView text;

	public DroidkobanChooser() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		DROIDKOBAN = this;

		if (this.getIntent().hasExtra("startLevel") && this.getIntent().hasExtra("startStory")) {
			this.startLevel = this.getIntent().getIntExtra("startLevel", 0);
			this.startStory = this.getIntent().getIntExtra("startStory", 0);
			this.gotoGame = true;
		}

		MODEL_NUMBER = this.getIntent().getIntExtra("MODEL_TYPE", 0);
		MAX_TEXTURE_CHOICES = this.getIntent().getIntExtra("TEXTURE_CHOICES", 1);

		mGLSurfaceView = new DroidkobanView(this, MODEL_NUMBER);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);

		FrameLayout layout = new FrameLayout(this);
		layout.addView(mGLSurfaceView);

		View overlay = View.inflate(this, R.layout.overlay, null);

		layout.addView(overlay);

		Button choose = (Button) overlay.findViewById(R.id.choose);
		choose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				selectTexture(DroidkobanChooserRenderer.offsetTarget);
			}
		});

		setContentView(layout);

	}

	public static final String CUSTOMIZE_DROIDKOBAN_3D_PLAYER = "de.leihwelt.android.addon.nsfjdg98z23lkjnsdfiwejb3422";

	protected void selectTexture(int i) {

		Intent intent = new Intent(CUSTOMIZE_DROIDKOBAN_3D_PLAYER);
		intent.putExtra("PLAYER_MODEL", MODEL_NUMBER);
		intent.putExtra("PLAYER_TEXTURE", i);
		this.sendBroadcast(intent);

		this.finish();

		if (gotoGame) {
			Intent i2 = new Intent(this, Droidkoban.class);
			i2.putExtra("startLevel", this.startLevel);
			i2.putExtra("startStory", this.startStory);

			makeLevelToast (startStory, null);
			
			this.startActivity(i2);

		}

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

	@Override
	protected void onResume() {

		super.onResume();
		mGLSurfaceView.onResume();

	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onPause() {

		super.onPause();
		mGLSurfaceView.onPause();
	}

	private DroidkobanView mGLSurfaceView;
}
