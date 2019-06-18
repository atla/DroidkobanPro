package de.leihwelt.android.droidkobanpro.achievements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import de.leihwelt.android.droidkobanpro.DroidkobanGame;
import de.leihwelt.android.droidkobanpro.achievements.Achievement.AchievementCheck;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.droidkobanpro.storage.StorageStrategy;

public enum AchievementUnlocker implements StorageStrategy {
	INSTANCE;

	public static interface AchievementListener {
		public void gained(Achievement achievement);
	}

	private final static String PREF_ACHIEVEMENTS_GAINED = "dkAchievementsGained";

	private ArrayList<Achievement> achievements = new ArrayList<Achievement>();
	private Set<AchievementListener> achievementListeners = new HashSet<AchievementListener>();

	private int currentAchievementScore = 0;

	private AchievementUnlocker() {

		this.setupAchievements();
	}

	public ArrayList<Achievement> getAchievements() {

		return this.achievements;
	}

	public void addListener(AchievementListener achievementListener) {
		this.achievementListeners.add(achievementListener);
	}

	public void removeListener(AchievementListener achievementListener) {
		this.achievementListeners.remove(achievementListener);
	}

	public int getScore() {
		return this.currentAchievementScore;
	}

	public int getAchievementsCompleted() {

		int completed = 0;

		for (Achievement a : this.achievements)
			if (a.unlocked)
				completed++;

		return completed;
	}

	public boolean allCompleted() {
		return this.getAchievementCount() == this.getAchievementsCompleted();
	}

	public int getAchievementCount() {
		return this.achievements.size();
	}

	private void setupAchievements() {

		// Gone in 60 seconds
		this.create(1, "Quite fast", "beat a level in under 2 minutes", "665152", 5, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().getLevelTime() < 2 * 60000;
			}
		});

		// 5 box moved
		this.create(2, "Getting into the mood", "Moved 10 boxes", "669922", 5, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getOverallBoxesPushed() > 9;
			}
		});

		this.create(3, "Speedy", "beat a level in under 90 seconds", "669982", 5, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().getLevelTime() < 90000;
			}
		});

		// first level
		this.create(4, "Baby steps", "Finish the first level", "669992", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 0;
			}
		});

		// two level
		this.create(5, "Keep rolling", "Finish two levels", "670002", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 1;
			}
		});

		// 10 level
		this.create(6, "Intermediate", "Finish 10 levels", "670012", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 9;
			}
		});

		// 20 level
		this.create(7, "Hardcore", "Finish 20 levels", "670022", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 19;
			}
		});

		// 35 level
		this.create(8, "Addict!", "Finish 35 levels", "670032", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 34;
			}
		});

		// Strategist
		this.create(9, "Strategist", "Complete a level without any undo steps!", "670042", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().undosForCurrentLevel == 0;
			}
		});

		// Cheater
		this.create(10, "Cheater", "beat a level in under 30 seconds", "670052", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().getLevelTime() < 30000;
			}
		});

		// Gone in 60 seconds
		this.create(11, "Gone in 60 seconds", "beat a level in under 60 seconds", "670062", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().getLevelTime() < 60000;
			}
		});

		// It's a long road
		this.create(12, "It's a long road", "1.000 overall moves", "670072", 5, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getOverallMoves() > 999;
			}
		});

		// run forrest run
		this.create(13, "run forrest run", "50.000 overall moves", "670082", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getOverallMoves() > 49999;
			}
		});

		// 50 level
		this.create(14, "Mr. Awesome!", "Finish 50 levels", "670092", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 48;
			}
		});

		// I can't help doing it
		this.create(15, "I can't help doing it", "100.000 overall moves", "670102", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getOverallMoves() > 99999;
			}
		});

		this.create(16, "Oh my this took long!", "Need more than 15 minutes to complete a level", "670112", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().getLevelTime() > 15 * 60000;
			}
		});

		// 5 level
		this.create(17, "Casual", "Finish 5 levels", "670122", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 4;
			}
		});

		// 101 level
		this.create(18, "Who let the dogs out", "Finish 101 levels", "670132", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 100;
			}
		});

		// 200
		this.create(19, "200 OK Response", "Finish 200 levels", "670142", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 199;
			}
		});

		// 300 level
		this.create(20, "What is this?", "SPARTAAAAAA!! Finish 300 levels", "670152", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 299;
			}
		});

		// 300 level
		this.create(21, "Are you insane?", "Finish 500 levels", "670162", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 499;
			}
		});

		// You never get this one
		this.create(22, "You never get this one ;)", "100.000.000 overall moves", "670172", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getOverallMoves() > 99999999;
			}
		});

		// You never get this one
		this.create(23, "Crafty", "1.000 boxes moved", "670182", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getOverallBoxesPushed() > 999;
			}
		});

		// You never get this one
		this.create(24, "Bodybuilder", "100.000 boxes moved", "670202", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getOverallBoxesPushed() > 99999;
			}
		});

		// You never get this one
		this.create(25, "Mr. Universe", "100.000.000 boxes moved", "670212", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getOverallBoxesPushed() > 99999999;
			}
		});

		this.create(26, "Weird things happen", "Needed more than 100 undo steps", "670222", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().undosForCurrentLevel > 99;
			}
		});

		this.create(27, "Went out for lunch!", "Need more than 30 minutes to complete a level", "670232", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().getLevelTime() > 30 * 60000;
			}
		});

		this.create(28, "I haz a nap .zZZz", "Need more than an hour to complete a level", "670242", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getCurrent().finished && statTracker.getCurrent().getLevelTime() > 60 * 60000;
			}
		});

		this.create(29, "Decathlete I", "Complete 5 levels in each story", "670252", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.checkReachedLevelAcrossStories(4);
			}
		});

		this.create(30, "Decathlete II", "Complete 10 levels in each story", "670262", 10, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.checkReachedLevelAcrossStories(9);
			}
		});

		this.create(31, "Decathlete III", "Complete 25 levels in each story", "670272", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.checkReachedLevelAcrossStories(24);
			}
		});

		this.create(32, "Decathlete IV", "Complete 50 levels in each story", "670282", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.checkReachedLevelAcrossStories(49);
			}
		});

		this.create(33, "Decathlete V", "Complete 100 levels in each story", "670292", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.checkReachedLevelAcrossStories(99);
			}
		});

		this.create(34, "Decathlete Professional", "Complete 250 levels in each story", "670292", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.checkReachedLevelAcrossStories(249);
			}
		});

		this.create(35, "Decathlete ROCKSTAR", "Complete 400 levels in each story", "670292", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.checkReachedLevelAcrossStories(399);
			}
		});

		this.create(36, "Retro King", "Complete 400 levels in 'Classic Sokoban'", "670322", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getLastFinishedLevel(0) >= 399;
			}
		});

		this.create(37, "Beach Boy", "Complete 400 levels in 'On the Beach'", "670332", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getLastFinishedLevel(1) >= 399;
			}
		});

		this.create(38, "Mr. Frost", "Complete 400 levels in 'Winter's calling'", "670342", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getLastFinishedLevel(2) >= 399;
			}
		});

		this.create(39, "I survived Jurrasic Park", "Complete 400 levels in 'Dinosaurs are alive!'", "670352", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getLastFinishedLevel(3) >= 399;
			}
		});

		this.create(40, "Long John Silver", "Complete 400 levels in 'Treasure Island'", "670972", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getLastFinishedLevel(4) >= 399;
			}
		});

		this.create(41, "MCP Destroyer", "Complete 400 levels in 'Is this Tron?'", "670982", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getLastFinishedLevel(5) >= 399;
			}
		});

		// 1000 level
		this.create(42, "Unstoppable", "Finish 1000 levels", "670992", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 999;
			}
		});

		// 2000 level
		this.create(43, "Lightyears ahead", "Finish 2000 levels", "671002", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 1999;
			}
		});

		// 3000 level
		this.create(44, "Master of Droidkoban 3D", "I owe you a beer!", "829692", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {
				return statTracker.getFinishedLevelCount() > 1999;
			}
		});

		this.create(45, "Buzz Lightyear", "Complete 400 levels in 'Outer Space'", "829702", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getLastFinishedLevel(6) >= 399;
			}
		});

		this.create(46, "Indiana Jones", "Complete 400 levels in 'Maya Temple'", "829722", 25, new AchievementCheck() {
			@Override
			public boolean completed(StatTracker statTracker) {

				return statTracker.getLastFinishedLevel(7) >= 399;
			}
		});
	}

	public void create(int id, String title, String description, String feintId, int points, AchievementCheck check) {
		Achievement a = new Achievement();
		a.id = this.achievements.size() + 1;
		a.title = title;
		a.description = description;
		a.points = points;
		a.check = check;
		a.feintId = feintId;

		this.achievements.add(a);
	}

	public void onStatChange(StatTracker current) {

		boolean unlocked = false;

		for (Achievement a : this.achievements) {
			if (!a.unlocked && a.check.completed(current)) {
				a.unlocked = true;
				unlocked = true;
				this.currentAchievementScore += a.points;

				notifyListeners(a);
			}
		}

		if (unlocked)
			StatTracker.INSTANCE.updateOverallScore();

	}

	private void notifyListeners(Achievement a) {
		for (AchievementListener listener : this.achievementListeners) {
			listener.gained(a);
		}
	}

	private String getAchievementsAsString() {
		StringBuffer achieved = new StringBuffer();

		for (Achievement a : this.achievements) {
			if (a.unlocked) {
				achieved.append(a.id + ";");
			}
		}

		return achieved.length() > 1 ? achieved.toString().substring(0, achieved.length() - 1) : "";
	}

	@Override
	public void load(SharedPreferences preferences) {

		String gained = preferences.getString(PREF_ACHIEVEMENTS_GAINED, "");

		List<Integer> achievementsGainedList = this.parseAchievementString(gained);

		this.setAchievementsGained(achievementsGainedList);

		// Log.v("AchievementUnlocker", "load " +
		// achievementsGainedList.size());
	}

	private void setAchievementsGained(List<Integer> achievementsGainedList) {

		this.currentAchievementScore = 0;

		for (Achievement achievement : this.achievements) {
			achievement.unlocked = achievementsGainedList.contains(achievement.id);

			if (achievement.unlocked)
				this.currentAchievementScore += achievement.points;
		}

	}

	private List<Integer> parseAchievementString(String gained) {

		List<Integer> gainedList = new ArrayList<Integer>();
		StringTokenizer tkn = new StringTokenizer(gained, ";");

		while (tkn.hasMoreTokens()) {
			String a = tkn.nextToken();
			gainedList.add(Integer.parseInt(a));
		}

		return gainedList;
	}

	@Override
	public void store(SharedPreferences.Editor edit) {

		// Editor editor =
		// this.ctx.getSharedPreferences("DroidkobanPrefs",0).edit();

		edit.putString(PREF_ACHIEVEMENTS_GAINED, this.getAchievementsAsString());
		// editor.commit();

	}
}
