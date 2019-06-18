package de.leihwelt.android.droidkobanpro.mainscreen;

import de.leihwelt.android.droidkobanpro.R;
import de.leihwelt.android.droidkobanpro.Droidkoban;
import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Videos extends Activity {

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

	public void showChangelog (){
		Intent i = new Intent(this, Changelog.class);
		this.startActivity(i);
	}
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
	                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		setContentView(R.layout.videos);

		
		

		View video1 = (View) findViewById(R.id.video1);
		video1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=gL3w6bpAB_E")));

			}

		});

		View video2 = (View) findViewById(R.id.video2);
		video2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=eeiI2IyAnsQ")));

			}

		});

		View video3 = (View) findViewById(R.id.video3);
		video3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=dyjNON95GN0")));

			}

		});

		View video4 = (View) findViewById(R.id.video4);
		video4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/v/1562900042354")));

			}

		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

}
