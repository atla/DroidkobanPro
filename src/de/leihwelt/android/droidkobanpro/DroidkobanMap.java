package de.leihwelt.android.droidkobanpro;

import de.leihwelt.android.astar.Mover;
import de.leihwelt.android.astar.PathFinder;
import de.leihwelt.android.astar.TileBasedMap;

public enum DroidkobanMap implements TileBasedMap {

	INSTANCE;

	public int levelWidth = 20;
	public int levelHeight = 20;
	public byte[] level = null;
	public boolean[][] visited = null;
	private int c;
	private PathFinder finder = null;

	private DroidkobanMap() {

	}

	public void reset(int w, int h, byte[] lvl) {
		if (w != levelWidth || h != levelHeight) {
			levelWidth = w;
			levelHeight = h;
			visited = new boolean[w][h];

			if (finder != null)
				finder.init(this);

		}

		level = lvl;
	}

	
	public void clearVisited() {
		for (int i = 0; i < levelWidth; ++i) {
			for (int e = 0; e < levelHeight; ++e) {
				visited[i][e] = false;
			}
		}

	}

	public float getCost(int x, int y, int tx, int ty) {
		float dx = tx - x;
		float dy = ty - y;

		float result = (float) (Math.sqrt((dx * dx) + (dy * dy)));

		return result;
	}

	public boolean visited(int x, int y) {
		return visited[x][y];
	}

	@Override
	public boolean blocked(Mover mover, int x, int y) {
		c = level[y * levelWidth + x];
		return (c == 1 || c == 2 || c == 4);
	}

	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		return 1;// Math.abs(sx - tx) + Math.abs(sy - ty);
	}

	@Override
	public int getHeightInTiles() {
		return levelHeight;
	}

	@Override
	public int getWidthInTiles() {
		return levelWidth;
	}

	@Override
	public void pathFinderVisited(int x, int y) {
		visited[x][y] = true;
	}

	@Override
	public void setFinder(PathFinder finder) {
		this.finder = finder;

	}

	@Override
	public boolean isWall(Mover mover, int x, int y) {
		
		int p = y * levelWidth + x;
		
		if (p >= 0 && p < level.length)
			c = level[p];
		
		return (c == 1);
	}

}
