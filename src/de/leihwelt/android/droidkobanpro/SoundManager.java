package de.leihwelt.android.droidkobanpro;

import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.droidkobanpro.R;
import android.content.Context;
import android.media.MediaPlayer;

public enum SoundManager {
	INSTANCE;
	
	private MediaPlayer mp = null;
		
	public void toggleMusic (Context ctx){
		this.create (ctx);
		
		
		
		boolean enabled = AppSettings.INSTANCE.isMusicEnabled();
		AppSettings.INSTANCE.setMusicEnabled(!enabled);
		
		if (!enabled)
			play (ctx);
		else
			stop (ctx);
		
		StorageConnector.INSTANCE.store ();
	}
	

	public void play (Context ctx){
		this.create(ctx);
		
		boolean enabled = AppSettings.INSTANCE.isMusicEnabled();
		
		if (enabled && this.mp.isPlaying() == false)
			this.mp.start();
	}
	
	public void stop (Context ctx){
		this.create (ctx);
		
		if (this.mp.isPlaying())
			this.mp.pause ();
	}
	
	private void create(Context ctx) {
		if (this.mp == null){
			this.mp = MediaPlayer.create(ctx, R.raw.track1);
			this.mp.setLooping(true);	
		}				
	}

	public boolean isEnabled (){
		return AppSettings.INSTANCE.isMusicEnabled();
	}
	
}
