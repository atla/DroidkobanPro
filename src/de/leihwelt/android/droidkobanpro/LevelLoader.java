package de.leihwelt.android.droidkobanpro;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.util.Log;

public class LevelLoader {

	private static final int TARGET_TILE = 3;
	private static final int BOX_TILE = 2;
	private static final int WALL_TILE = 1;

	public static LevelData loadLevel(String levelSet, Activity ac, int level) {
		byte[] buffer = null;
		InputStream in = null;

		LevelData data = new LevelData();


		try {
			
			String map = "levels/" + levelSet + "/" + level + ".txt";

			findMapSize(ac.getAssets().open(map), data);
			in = ac.getAssets().open(map);

			buffer = new byte[in.available()];

			while (in.read(buffer) != -1) {

			}

			return getLevelDataFor(new String(buffer), level, data);

		} catch (IOException e) {
			Log.e("LevelLoader", "Error reading level file " + level + ".txt\n" + e.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new LevelData();
	}

	private static void findMapSize(InputStream open, LevelData data) {
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(open));
			String line = "";

			int maxColumns = 0;
			int rows = 0;

			line = br.readLine();

			while (line != null) {
				rows++;
				maxColumns = (maxColumns > line.length() ? maxColumns : line.length());
				line = br.readLine();

			}

			br.close();

			data.width = maxColumns;
			data.height = rows;
			
			Log.v ("LevelLoader", "Map size is " + data.width + "x" + data.height);

		} catch (IOException e) {
			Log.e ("LevelLoader", "Error examaning map size");
			e.printStackTrace();
		}
	}

	/**
	 * Gets the starting pos for a new loaded level and changes the 5 to 0
	 * 
	 * @param lvl
	 * @return
	 */
	public static LevelData getLevelDataFor(String lvlString, int level, LevelData data) {
		LevelData p = data;

		p.idx = level;
		p.lvl = new byte[p.width * p.height];	

		int i = 0;

		for (char c : lvlString.toCharArray()) {
			switch (c) {
			case '0':
				p.lvl[i] = 0;
				++i;
				break;
			case '1':
				p.lvl[i] = WALL_TILE;
				++i;
				break;
			case '2':
				p.lvl[i] = BOX_TILE;
				++i;
				break;
			case '3':
				p.lvl[i] = TARGET_TILE;
				++i;
				break;
			case '4':
				p.lvl[i] = 4;
				++i;
				break;
			case '5':
				p.lvl[i] = 0;
				p.x = i % p.width;
				p.y = i / p.width;
				++i;
				break;
			case '6':
				p.lvl[i] = 3;
				p.x = i % p.width;
				p.y = i / p.width;
				++i;
				break;
			default:
			}

		}
		return p;
	}

}
