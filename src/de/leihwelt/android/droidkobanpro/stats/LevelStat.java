package de.leihwelt.android.droidkobanpro.stats;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;

public class LevelStat {

	public int movesForCurrentLevel = 0;
	public int undosForCurrentLevel = 0;

	public long levelStartTime = 0;
	public long levelFinishedTime = 0;

	public long getLevelTime() {
		return this.levelFinishedTime - this.levelStartTime;
	}

	public int boxesLeft = 0;
	public int boxesInLevel = 0;

	public boolean finished = false;

	public int level = 0;

	public void load(SharedPreferences prefs) {
		this.movesForCurrentLevel = prefs.getInt("dkCurrentMoves", this.movesForCurrentLevel);
		this.undosForCurrentLevel = prefs.getInt("dkUndosMoves", this.undosForCurrentLevel);

		long levelTime = prefs.getLong("dkLevelTime", -1);

		this.levelStartTime = SystemClock.uptimeMillis() - levelTime;
	}

	public void store(Editor editor) {

		editor.putInt("dkCurrentMoves", this.movesForCurrentLevel);
		editor.putInt("dkUndosMove", this.undosForCurrentLevel);
		editor.putLong("dkLevelStartTime", this.levelStartTime);
		long levelTime = SystemClock.uptimeMillis() - this.levelStartTime;
		editor.putLong("dkLevelTime", levelTime);

	}

}
