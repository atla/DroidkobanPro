package de.leihwelt.android.droidkobanpro.achievements;

import de.leihwelt.android.droidkobanpro.stats.StatTracker;

public class Achievement {

	public interface AchievementCheck {
		public boolean completed(StatTracker statTracker);
	}

	public String title = "Generic";
	public String description = "achieved something";
	public int points = 10;
	public int id = 0;
	
	public String feintId = "";
	
	public AchievementCheck check = null;
	
	public boolean unlocked = false;

}
