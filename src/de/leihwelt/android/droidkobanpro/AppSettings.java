package de.leihwelt.android.droidkobanpro;

import android.content.SharedPreferences;
import android.util.Log;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.storage.StorageStrategy;
import de.leihwelt.android.social.TwitterConnect;
import de.leihwelt.android.utilities.EncryptedPassword;

public enum AppSettings implements StorageStrategy {
	INSTANCE;

	private boolean twitterEnabled = false;
	private String twitterUsername = "";
	private EncryptedPassword twitterPassword = new EncryptedPassword();

	private boolean enableBackgrounds = true;
	private boolean enableLighting = false;
	private boolean musicEnabled = false;
	private boolean feintEnabled = false;
	private boolean hideUI = false;

	@Override
	public void load(SharedPreferences preferences) {

		this.twitterEnabled = preferences.getBoolean("twitterEnabled", this.twitterEnabled);
		this.twitterUsername = preferences.getString("twitterUsername", this.twitterUsername);
		this.twitterPassword.setEncrypted(preferences.getString("twitterPassword", this.twitterPassword.getEncrypted()));

		this.enableBackgrounds = preferences.getBoolean("enableBackgrounds", this.enableBackgrounds);
		this.enableLighting = preferences.getBoolean("enableLighting", this.enableLighting);
		this.musicEnabled = preferences.getBoolean("musicEnabled", this.musicEnabled);
		this.feintEnabled = preferences.getBoolean("feintEnabled", this.feintEnabled);
		this.hideUI = preferences.getBoolean("hideUI", this.hideUI);

		this.setupTwitterConnect();
	}

	public boolean isEnableLighting() {
		return enableLighting;
	}

	public void setEnableLighting(boolean enableLighting) {
		this.enableLighting = enableLighting;
	}

	public boolean isFeintEnabled (){
		return this.feintEnabled;
	}
	
	public void setFeintEnabled (boolean feintEnabled){
		this.feintEnabled = feintEnabled;
	}
	
	public boolean isHideUI(){
		return this.hideUI;
	}
	
	public void setHideUI (boolean hideUI){
		this.hideUI = hideUI;
	}
	
	@Override
	public void store(SharedPreferences.Editor editor) {

		editor.putBoolean("twitterEnabled", this.twitterEnabled);
		editor.putString("twitterUsername", this.twitterUsername);
		editor.putString("twitterPassword", this.twitterPassword.getEncrypted());

		editor.putBoolean("enableBackgrounds", this.enableBackgrounds);
		editor.putBoolean("enableLighting", this.enableLighting);
		editor.putBoolean("musicEnabled", this.musicEnabled);
		editor.putBoolean("feintEnabled", this.feintEnabled);
		editor.putBoolean("hideUI", this.hideUI);
		
		Log.v("AppSettings", "stored");
	}

	public boolean isTwitterEnabled() {
		return twitterEnabled;
	}

	public void setTwitterEnabled(boolean twitterEnabled) {
		this.twitterEnabled = twitterEnabled;

		this.setupTwitterConnect();
	}

	private void setupTwitterConnect() {
		if (twitterEnabled)
			AchievementUnlocker.INSTANCE.addListener(TwitterConnect.INSTANCE);
		else
			AchievementUnlocker.INSTANCE.removeListener(TwitterConnect.INSTANCE);
	}

	public String getTwitterUsername() {
		return twitterUsername;
	}

	public void setTwitterUsername(String twitterUsername) {
		this.twitterUsername = twitterUsername;
	}

	public String getTwitterPassword() {
		return twitterPassword.get();
	}

	public void setTwitterPassword(String twitterPassword) {
		this.twitterPassword.set(twitterPassword);
	}

	public boolean isEnableBackgrounds() {
		return this.enableBackgrounds;
	}

	public void setEnableBackgrounds(boolean enableBackgrounds) {
		this.enableBackgrounds = enableBackgrounds;
	}

	public boolean isMusicEnabled() {
		return this.musicEnabled;
	}

	public void setMusicEnabled(boolean musicEnabled) {
		this.musicEnabled = musicEnabled;

	}

}
