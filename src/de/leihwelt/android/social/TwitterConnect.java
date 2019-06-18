package de.leihwelt.android.social;

import android.util.Log;
//import twitter4j.Twitter;
//import twitter4j.TwitterException;
//import twitter4j.TwitterFactory;
import de.leihwelt.android.droidkobanpro.achievements.Achievement;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker.AchievementListener;

public enum TwitterConnect implements AchievementListener {
	INSTANCE;

	private boolean correct = false;
	//private Twitter twitter = null;

	private TwitterConnect() {
	}

	public void setCredentials(String login, String password) {
//		this.twitter = new TwitterFactory().getInstance(login, password);
//
//		try {
//			this.correct = this.twitter.test();
//		} catch (TwitterException e) {
//			e.printStackTrace();
//		}
		
		//Log.v ("TwitterConnect", "Set credentials went " + this.correct + " " + login + " pass " + password);

	}

	@Override
	public void gained(Achievement achievement) {

//		if (this.correct) {
//
//			final Twitter t = this.twitter;
//			final Achievement a = achievement;
//
//			Thread th = new Thread() {
//				public void run() {
//					String post = buildTwitterPostForAchievement(a);
//					try {
//						t.updateStatus(post);
//					} catch (TwitterException e) {
//						Log.e("TwitterConnect", "Faild to update status " + e.getMessage());
//					}
//				}
//			};
//
//			th.start();
//		}

	}

	private String buildTwitterPostForAchievement(Achievement achievement) {
		String start = "Achievement Unlocked";
		String end = " #droidkoban";

		return start + " >" + achievement.title + "< (" + achievement.description + ") " + end;
	}
}
