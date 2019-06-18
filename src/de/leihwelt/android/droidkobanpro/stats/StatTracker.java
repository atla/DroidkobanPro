package de.leihwelt.android.droidkobanpro.stats;

import com.openfeint.api.OpenFeint;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;

import de.leihwelt.android.droidkobanpro.Droidkoban;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.storage.StorageStrategy;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public enum StatTracker implements StorageStrategy {
	INSTANCE;

	private final static int STORY_COUNT = 9;

	public final static String PREF_LAST_FINISHED_LEVEL = "dkLastFinishedLevel";
	private final static String PREF_OVERALL_PLAY_TIME_IN_MINUTES = "dkOverallPlayTimeInMinutes";
	private final static String PREF_OVERALL_BOXES_PUSHED = "dkOverallBoxesPushed";
	private final static String PREF_OVERALL_MOVES = "dkOverallMoves";

	private long overallPlayTimeInMinutes = 0;
	private long overallBoxesPushed = 0;
	private long overallMoves = 0;
	private int lastFinishedLevel[] = new int[STORY_COUNT];

	private long overallScore = 0;

	private String storyId[] = { "538404", "538444", "538474", "538514", "538554", "538594", "538624", "646054" };

	public long getOverallMoves() {
		return overallMoves;
	}

	private LevelStat current;

	private AchievementUnlocker achievements = AchievementUnlocker.INSTANCE;

	/**
	 * Save the last five LevelStats from completed levels
	 */
	private LevelStat[] lastStats = new LevelStat[6];

	public void setLastFinishedLevel(int story, int lvl) {
		this.lastFinishedLevel[story] = lvl;

		this.updateScoreForStory(story, lvl);

	}

	public void updateScoreForStory(int story, int lvl) {

		if (story >= 0 && story < STORY_COUNT) {

			if (OpenFeint.isUserLoggedIn()) {

				Score s = new Score(lvl, null);

				Leaderboard l = new Leaderboard(storyId[story]);
				s.submitTo(l, new Score.SubmitToCB() {

					@Override
					public void onSuccess(boolean newHighScore) {

					}

					@Override
					public void onFailure(String exceptionMessage) {
						Toast.makeText(Droidkoban.INSTANCE, "Error (" + exceptionMessage + ") posting score.", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}

	}

	public void updateOverallScore() {

		this.overallScore = getOverallScore();

		if (OpenFeint.isUserLoggedIn()) {

			Score s = new Score(overallScore, null);

			Leaderboard l = new Leaderboard("536114");
			s.submitTo(l, new Score.SubmitToCB() {

				@Override
				public void onSuccess(boolean newHighScore) {
					Log.v("DROIDKOBAN", "SUBMITTED SCORE! " + newHighScore);
				}

				@Override
				public void onFailure(String exceptionMessage) {
					Toast.makeText(Droidkoban.INSTANCE, "Error (" + exceptionMessage + ") posting score.", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public long getOverallScore() {

		long overall = achievements.getScore();

		for (int i = 0; i < STORY_COUNT; ++i) {
			overall += lastFinishedLevel[i];
		}

		return overall;

	}

	private StatTracker() {
		this.current = new LevelStat();
	}

	public long getOverallBoxesPushed() {
		return this.overallBoxesPushed;
	}

	public void increaseMoves() {

		if (this.current.movesForCurrentLevel == 0) {
			this.current.levelStartTime = SystemClock.uptimeMillis();
		}

		this.current.movesForCurrentLevel++;
		this.overallMoves++;

		this.changed();
	}

	public void boxPushed() {
		this.overallBoxesPushed++;

		this.changed();
	}

	public void increaseUndos() {
		this.current.undosForCurrentLevel++;
		this.changed();
	}

	public int getMovesForCurrentLevel() {
		return this.current.movesForCurrentLevel;
	}

	public int getUndosForCurrentLevel() {
		return this.current.undosForCurrentLevel;
	}

	public void startTrackingForLevel() {

		if (this.current != null && this.current.finished) {
			this.lastStats[0] = this.current;

			this.current = new LevelStat();
		}

		this.current.levelStartTime = SystemClock.uptimeMillis();
	}

	public void setBoxesInLevel(final int boxesInLevel) {
		this.current.boxesInLevel = boxesInLevel;
	}

	public void setBoxesLeft(final int boxesLeft) {
		this.current.boxesLeft = boxesLeft;
	}

	public int elapsedTimeSeconds() {

		if (this.current.finished)
			return (int) (this.current.levelFinishedTime - this.current.levelStartTime) / 1000;

		return (int) (SystemClock.uptimeMillis() - this.current.levelStartTime) / 1000;
	}

	private void changed() {
		this.achievements.onStatChange(this);
	}

	public void paused() {

	}

	public LevelStat getCurrent() {
		return this.current;
	}

	public int getLastFinishedLevel(int story) {
		return this.lastFinishedLevel[story];
	}

	public int getFinishedLevelCount() {
		int finishedLevels = 0;

		for (int i = 0; i < STORY_COUNT; ++i) {
			finishedLevels += this.lastFinishedLevel[i];
		}

		return finishedLevels;
	}

	public void levelFinished(int story, int level) {

		this.current.levelFinishedTime = SystemClock.uptimeMillis();
		this.current.finished = true;

		this.overallScore = getOverallScore();

		if (this.lastFinishedLevel[story] < level) {
			this.lastFinishedLevel[story] = level;

			this.updateScoreForStory(story, level);
			this.updateOverallScore();
		}

		this.changed();
	}

	private void save() {
	}

	/**
	 * checks for a specific reached level across all stories minimum parameter
	 * = 1
	 * 
	 * @param lvl
	 * @return
	 */
	public boolean checkReachedLevelAcrossStories(int lvl) {

		for (int i = 0; i < StatTracker.STORY_COUNT; ++i) {
			if (this.getLastFinishedLevel(i) < lvl)
				return false;
		}

		return true;
	}

	public void resetCurrent() {
		this.current.movesForCurrentLevel = 0;
		this.current.undosForCurrentLevel = 0;
		this.current.levelFinishedTime = 0;

		this.startTrackingForLevel();

		save();
	}

	public String elapsedTimeString() {
		int seconds = this.elapsedTimeSeconds();
		int m = seconds / 60;
		int s = seconds % 60;

		return (m > 0 ? ("" + (m < 10 ? "0" + m : m) + ":") : "") + (s < 10 ? "0" + s : s);
	}

	public long getOverallPlayTimeInMinutes() {
		return overallPlayTimeInMinutes;
	}

	@Override
	public void load(SharedPreferences preferences) {

		for (int i = 0; i < STORY_COUNT; ++i) {
			this.lastFinishedLevel[i] = preferences.getInt(StatTracker.PREF_LAST_FINISHED_LEVEL + i, 0);
		}

		this.overallPlayTimeInMinutes = preferences.getLong(StatTracker.PREF_OVERALL_PLAY_TIME_IN_MINUTES, 0);
		this.overallBoxesPushed = preferences.getLong(StatTracker.PREF_OVERALL_BOXES_PUSHED, 0);
		this.overallMoves = preferences.getLong(StatTracker.PREF_OVERALL_MOVES, 0);

		// Log.v ("StatTracker", "load " + lastFinishedLevel);

	}

	@Override
	public void store(SharedPreferences.Editor editor) {

		for (int i = 0; i < STORY_COUNT; ++i) {
			editor.putInt(StatTracker.PREF_LAST_FINISHED_LEVEL + i, this.lastFinishedLevel[i]);
		}

		editor.putLong(StatTracker.PREF_OVERALL_PLAY_TIME_IN_MINUTES, this.overallPlayTimeInMinutes);
		editor.putLong(StatTracker.PREF_OVERALL_BOXES_PUSHED, this.overallBoxesPushed);
		editor.putLong(StatTracker.PREF_OVERALL_MOVES, this.overallMoves);
	}

	public void storeCurrent(Editor editor) {

		if (this.current != null)
			this.current.store(editor);

	}

	public void loadCurrent(SharedPreferences prefs) {
		if (this.current != null) {
			this.current.load(prefs);
		}
	}
}
