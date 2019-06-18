package de.leihwelt.android.droidkobanpro;

import de.leihwelt.android.utilities.Base64Coder;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LevelData {
	public int idx = 0;
	public int x = 0;
	public int y = 0;
	public int width = 20;
	public int height = 16;
	public byte[] lvl = null;
	
//	public void load (SharedPreferences prefs){
//		this.idx = prefs.getInt("dkCurrentLevelDataIdx", idx);
//		this.x = prefs.getInt("dkCurrentLevelDatax", x);
//		this.y = prefs.getInt("dkCurrentLevelDatay", y);
//		this.width = prefs.getInt("dkCurrentLevelDatawidth", width);
//		this.height = prefs.getInt("dkCurrentLevelDataheight", height);
//		this.lvl = Base64Coder.decodeLines(prefs.getString("dkCurrentLevelDataLvl", defValue))
//	}
//	
//	public void store (Editor editor){
//	
//	}
}