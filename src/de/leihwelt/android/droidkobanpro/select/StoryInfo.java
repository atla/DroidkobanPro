package de.leihwelt.android.droidkobanpro.select;

public class StoryInfo {
	public int numberOfLevels;
	public int imageRessource;
	public String name;
	public String storyId;
	public int finishedLevels;
	public int id;
	
	
	public StoryInfo (int numLevels, int image, String name, int finishedLevels, String storyId, int id){
		this.numberOfLevels = numLevels;
		this.imageRessource = image;
		this.name = name;
		this.finishedLevels = finishedLevels;
		this.storyId = storyId;
		this.id = id;
	}
	
}
