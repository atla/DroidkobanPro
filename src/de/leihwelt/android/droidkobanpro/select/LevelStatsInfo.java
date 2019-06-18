package de.leihwelt.android.droidkobanpro.select;

import java.util.ArrayList;

import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.R;

public enum LevelStatsInfo {
	INSTANCE;

	public ArrayList<LevelInfo> stats = new ArrayList<LevelInfo>();

	public ArrayList<StoryInfo> storyInfos = new ArrayList<StoryInfo>();

	public int getStoryResourceFor(int story) {

		int i = 0;
		for (StoryInfo info : storyInfos) {
			if (i == story) {
				return info.imageRessource;
			}
			++i;
		}

		return 0;

	}

	public String getStoryNameFor(int story) {
		int i = 0;
		for (StoryInfo info : storyInfos) {
			if (i == story) {
				return info.name;
			}
			++i;
		}

		return "";
	}

	private LevelStatsInfo() {

		for (int i = 0; i < 400; ++i) {
			stats.add(new LevelInfo(i + 1));
		}

		StatTracker st = StatTracker.INSTANCE;

		storyInfos.add(new StoryInfo(400, R.drawable.t1, "Classic Sokoban", st.getLastFinishedLevel(0), "default", 0));
		storyInfos.add(new StoryInfo(400, R.drawable.t2, "On the Beach", st.getLastFinishedLevel(1), "beach", 1));
		storyInfos.add(new StoryInfo(400, R.drawable.t3, "Winter's calling", st.getLastFinishedLevel(2), "winter", 2));
		storyInfos.add(new StoryInfo(400, R.drawable.t4, "Dinosaurs are alive!", st.getLastFinishedLevel(3), "dinosaur", 3));
		storyInfos.add(new StoryInfo(400, R.drawable.t6, "Treasure Island", st.getLastFinishedLevel(4), "treasure", 4));
		storyInfos.add(new StoryInfo(400, R.drawable.t5, "Is this Tron?", st.getLastFinishedLevel(5), "tron", 5));
		storyInfos.add(new StoryInfo(400, R.drawable.t7, "Outer Space", st.getLastFinishedLevel(6), "space", 6));
		storyInfos.add(new StoryInfo(400, R.drawable.t8, "Maya Temple", st.getLastFinishedLevel(7), "maya", 7));

	}
}
