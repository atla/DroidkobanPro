package de.leihwelt.android.droidkobanpro;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import de.leihwelt.android.astar.AStarPathFinder;
import de.leihwelt.android.astar.Mover;
import de.leihwelt.android.astar.PathFinder;
import de.leihwelt.android.astar.Path.Step;
import de.leihwelt.android.droidkobanpro.select.LevelStatsInfo;
import de.leihwelt.android.droidkobanpro.select.StoryInfo;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.droidkobanpro.theme.BeachTheme;
import de.leihwelt.android.droidkobanpro.theme.DefaultTheme;
import de.leihwelt.android.droidkobanpro.theme.DinosaurTheme;
import de.leihwelt.android.droidkobanpro.theme.MayaTheme;
import de.leihwelt.android.droidkobanpro.theme.SpaceTheme;
import de.leihwelt.android.droidkobanpro.theme.Theme;
import de.leihwelt.android.droidkobanpro.theme.TreasureTheme;
import de.leihwelt.android.droidkobanpro.theme.TronTheme;
import de.leihwelt.android.droidkobanpro.theme.WinterTheme;
import de.leihwelt.android.objects.RenderObject;
import de.leihwelt.android.utilities.Base64Coder;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.*;

public enum DroidkobanGame {

	INSTANCE;

	public final static int RESET_GAME = 1;
	public final static int ADVANCE_TO_NEXT_LEVEL = 2;

	public Vibrator vibrator = null;

	private Theme[] themes = new Theme[6];
	private Theme currentTheme = null;

	private boolean toNextLevel = false;
	private boolean finished = false;
	private int currentLevel = 0;
	private StoryInfo currentStory = LevelStatsInfo.INSTANCE.storyInfos.get(0);
	private int lastLoadedStory = -1;

	private int boxesInThisLevel = -1;
	public RenderObject playerObject = null;
	public DroidkobanRenderer renderer = null;

	public LinkedList<MovementCommand> movements = new LinkedList<MovementCommand>();
	public LinkedList<Integer> actions = new LinkedList<Integer>();
	private Stack<UndoAction> undoHistory = new Stack<UndoAction>();

	public LevelData levelData = null;
	public int levelWidth = 20;
	public int levelHeight = 16;

	private boolean resumed = false;
	private boolean doResume = false;

	public byte[] level = null;

	public Context context = null;
	public Activity activity = null;

	private DroidkobanMap map = DroidkobanMap.INSTANCE;
	private PathFinder pf;
	private de.leihwelt.android.astar.Path path;
	private Mover droidkobanMover = new Mover() {

	};

	private boolean hovers = true;

	public class Waypoint {
		public int x;
		public int y;
		public int fieldId;

		public boolean equals(Object o) {
			if (o instanceof Waypoint) {
				return ((Waypoint) o).fieldId == fieldId;
			}
			return false;
		}

		public int hashCode() {
			return fieldId;
		}

		public String toString() {
			return "wp [x: " + x + " y: " + y + " fieldId: " + fieldId;
		}
	}

	// public Set<Waypoint> waypoints = new LinkedHashSet<Waypoint>();
	public Queue<Waypoint> waypoints = new LinkedList<Waypoint>();
	public int lastAddedWaypointId = -1;
	public int lastReachedWaypointId = -1;
	public boolean waypointsRemain = false;
	public boolean resuming = false;

	private DroidkobanGame() {
		this.playerObject = new RenderObject();

		this.pf = new AStarPathFinder(map, 500, false);

		for (int i = 0; i < this.themes.length; ++i)
			this.themes[i] = null;
	}

	public LevelData getLevelData() {
		return this.levelData;
	}

	public void resume(SharedPreferences prefs) {

		resuming  = true;
		
		boolean stored = prefs.getBoolean("dkStored", false);

		this.hovers = PlayerModelSelector.INSTANCE.hovers();

		if (!doResume || !stored)
			resetCurrentLevel();
		else
			loadStoredState(prefs);

	}

	public void addWaypoint(int x, int y, int fieldId) {

		if (lastAddedWaypointId != fieldId) {
			Waypoint wp = new Waypoint();
			wp.x = x;
			wp.y = y;
			wp.fieldId = fieldId;

			waypoints.add(wp);

			Log.v("Droidkoban 3D", "Waypoint added, fieldId: " + fieldId);

			lastAddedWaypointId = fieldId;

			waypointsRemain = true;
		}

	}

	/*
	 * for (Waypoint wp : waypoints) { Log.v("Droidkoban 3D",
	 * "Game::startMoving / Waypoint " + wp.x + "/" + wp.y + "(" + wp.fieldId +
	 * ")");
	 * 
	 * dstX = wp.x; dstY = wp.y;
	 * 
	 * while (cX != dstX || cY != dstY) {
	 * 
	 * while (cX != dstX) {
	 * 
	 * if (cX < dstX) { cX++; movements.add(MovementCommand.LEFT); } else {
	 * cX--; movements.add(MovementCommand.RIGHT); } }
	 * 
	 * while (cY != dstY) {
	 * 
	 * if (cY < dstY) { cY++; movements.add(MovementCommand.UP); } else { cY--;
	 * movements.add(MovementCommand.DOWN); } } } }
	 */

	/**
	 * Transform waypoints into movement commands - do the magic here! ;)
	 */
	public void startMoving() {

		if (this.lastReachedWaypointId < 0) {

			Log.v("Droidkoban 3D", "startMoving Called");

			int cX = (int) this.playerObject.x;
			int cY = (int) this.playerObject.z;
			int dstX = 0;
			int dstY = 0;

			map.reset(this.levelWidth, this.levelHeight, this.level);

			Waypoint wp = waypoints.poll();

			if (wp != null) {

				map.clearVisited();

				/*
				 * if (Math.abs(cX - wp.x) + Math.abs(cY - wp.y) < 2) {
				 * 
				 * Log.v ("Droidkoban 3D", "DroidkobanGame:: fastPathfinding " +
				 * wp);
				 * 
				 * dstX = wp.x; dstY = wp.y;
				 * 
				 * while (cX != dstX || cY != dstY) {
				 * 
				 * while (cX != dstX) {
				 * 
				 * if (cX < dstX) { cX++; movements.add(MovementCommand.LEFT); }
				 * else { cX--; movements.add(MovementCommand.RIGHT); } }
				 * 
				 * while (cY != dstY) {
				 * 
				 * if (cY < dstY) { cY++; movements.add(MovementCommand.UP); }
				 * else { cY--; movements.add(MovementCommand.DOWN); } } }
				 * 
				 * } else {
				 */path = pf.findPath(droidkobanMover, cX, cY, wp.x, wp.y);

				if (path != null) {

					for (int i = 0; i < path.getLength(); ++i) {
						Step s = path.getStep(i);
						dstX = s.getX();
						dstY = s.getY();

						if (dstX != cX) {
							movements.add((cX < dstX) ? MovementCommand.LEFT : MovementCommand.RIGHT);
						} else if (dstY != cY) {
							movements.add((cY < dstY) ? MovementCommand.UP : MovementCommand.DOWN);
						}

						cX = dstX;
						cY = dstY;
					}
					this.lastReachedWaypointId = wp.fieldId;
				} else {
					renderer.removeWaypoint(wp.fieldId);
				}

				waypointsRemain = this.waypoints.size() > 0;
			}
		}
		// renderer.removeWaypoint(wp.fieldId);

		// lastAddedWaypointId = -1;
	}

	private void loadStoredState(SharedPreferences prefs) {

		this.load(prefs);

		if (renderer != null)
			this.renderer.clearObjects();

		this.movements.clear();
		this.actions.clear();
		this.undoHistory.clear();

		this.playerObject.actionRenderIndex = -1;

		// set player to correct position for this map
		RenderObject ob = this.playerObject;

		ob.y = 0.0f;
		ob.action = RenderObject.NO_ACTION;

		this.boxesInThisLevel = getBoxesCount();

		if (renderer != null)
			this.renderer.setupObjects();

		this.resumed = true;
		this.resuming = false;
	}

	public StoryInfo getCurrentStory() {
		return currentStory;
	}

	public void setCurrentStory(StoryInfo info) {
		this.currentStory = info;
	}

	public void save(Editor editor) {

		editor.putInt("dkCurrentLevel", this.currentLevel);
		editor.putString("dkCurrentData", Base64Coder.encodeLines(this.level));
		editor.putFloat("dkPlayerX", this.playerObject.x);
		editor.putFloat("dkPlayerZ", this.playerObject.z);
		editor.putInt("dkLevelWidth", this.levelWidth);
		editor.putInt("dkLevelHeight", this.levelHeight);
		editor.putInt("dkCurrentStory", this.currentStory.id);

		editor.putFloat("droidZoom", this.renderer.zoom);

		editor.putBoolean("dkStored", true);

		StatTracker.INSTANCE.storeCurrent(editor);

	}

	public void load(SharedPreferences prefs) {

		this.currentLevel = prefs.getInt("dkCurrentLevel", this.currentLevel);
		String lvl = prefs.getString("dkCurrentData", "-1");

		if (lvl != "-1")
			this.level = Base64Coder.decodeLines(lvl);

		int story = prefs.getInt("dkCurrentStory", this.lastLoadedStory);
		this.setCurrentStory(LevelStatsInfo.INSTANCE.storyInfos.get(story));
		this.lastLoadedStory = -1;

		this.levelWidth = prefs.getInt("dkLevelWidth", this.levelWidth);
		this.levelHeight = prefs.getInt("dkLevelHeight", this.levelHeight);

		this.playerObject.x = prefs.getFloat("dkPlayerX", this.playerObject.x);
		this.playerObject.z = prefs.getFloat("dkPlayerZ", this.playerObject.z);

		this.renderer.zoom = prefs.getFloat("droidZoom", this.renderer.zoom);
	}

	private byte[] getLevelData(int idx) {

		if (levelData == null || idx != levelData.idx || this.lastLoadedStory != this.currentStory.id) {
			
			Log.v ("getLevelData1", "" + this.levelData);
			
			this.levelData = LevelLoader.loadLevel(this.currentStory.storyId, this.activity, idx);
			
			
			Log.v ("getLevelData2", "" + this.levelData);
			
			this.levelWidth = this.levelData.width;
			this.levelHeight = this.levelData.height;
		}

		return this.levelData.lvl.clone();
	}
	
	public void resetCurrentLevel() {
		

		this.loadLevel(currentLevel);			
		
		this.movements.clear();
		this.renderer.removeAllWaypoints();
		this.lastReachedWaypointId = -1;

		if (renderer != null)
			this.renderer.clearObjects();


		StatTracker.INSTANCE.resetCurrent();

		this.movements.clear();
		this.renderer.removeAllWaypoints();
		this.lastReachedWaypointId = -1;
		

		if (renderer != null)
			this.renderer.setupObjects();
		
		Log.v ("GAME", "RESET CURRENT LEVEL");
		
		
		this.resuming = false;
	}
		

	/**
	 * Currently only used to set a level from the level select screen
	 * 
	 * @param level
	 */
	public void setToLevel(int level) {

		this.resumed = false;
		this.doResume = false;

		this.currentLevel = level;
		this.levelData = null;
		this.level = null;
	
	}

	private byte[] loadLevel(final int idx) {

		this.movements.clear();
		this.actions.clear();
		this.undoHistory.clear();

		this.playerObject.actionRenderIndex = -1;

		this.currentLevel = idx;
		this.level = getLevelData(this.currentLevel);

		// set player to correct position for this map
		RenderObject ob = this.playerObject;

		if (this.levelData != null) {
			ob.x = this.levelData.x * 1.0f;
			ob.z = this.levelData.y * 1.0f;
		}

		ob.y = 0.0f;
		ob.action = RenderObject.NO_ACTION;

		this.boxesInThisLevel = getBoxesCount();

		return this.level;
	}

	/**
	 * Counts boxes in the current level
	 * 
	 * @return count of boxes
	 */
	private int getBoxesCount() {
		int i = 0, ret = 0;
		int max = this.level.length;
		for (; i < max; ++i) {
			if (this.level[i] == 2 || this.level[i] == 4)
				ret++;
		}
		return ret;
	}

	public void compute(float frameTime) {

		if (this.actions.size() > 0) {

			switch (this.actions.poll()) {

			case RESET_GAME:
				resetCurrentLevel();
				break;
			}
		}

		movePlayer(frameTime);

		if (toNextLevel) {

			playerObject.rot += 35.0f * this.playerObject.y * frameTime;

			if (this.playerObject.y > 24.5f) {

				this.playerObject.action = RenderObject.NO_ACTION;
				this.playerObject.rot = -1.0f;

				renderer.clearObjects();
				nextLevel();
				renderer.setupObjects();

				toNextLevel = false;
				finished = false;

				StatTracker.INSTANCE.startTrackingForLevel();
			}
		}

		if (!this.finished && !this.toNextLevel)
			animatePlayer(frameTime);
	}

	private void animatePlayer(float frameTime) {

		if (hovers)
			this.playerObject.y = 0.05f + (float) (0.06f * Math.sin(SystemClock.currentThreadTimeMillis() * 0.0025f));
	}

	public boolean movementAllowed(MovementCommand cmd) {

		return true;
	}

	public void undo() {
		if (this.undoHistory.size() > 0) {
			UndoAction undo = this.undoHistory.pop();

			this.level[undo.f1.fieldId] = undo.f1.value;
			this.level[undo.f2.fieldId] = undo.f2.value;
			this.level[undo.f3.fieldId] = undo.f3.value;

			this.playerObject.x = undo.playerX;
			this.playerObject.z = undo.playerZ;
			this.playerObject.fieldId = undo.f1.fieldId;
			this.playerObject.action = RenderObject.NO_ACTION;
			this.playerObject.actionRenderIndex = -1;

			undo.box.x = undo.boxX;
			undo.box.z = undo.boxZ;
			undo.box.action = RenderObject.NO_ACTION;
			undo.box.fieldId = undo.f2.fieldId;

			StatTracker.INSTANCE.increaseUndos();

			this.movements.clear();
			this.renderer.removeAllWaypoints();
			this.lastReachedWaypointId = -1;

		}
	}

	private void movePlayer(float frameTime) {

		RenderObject player = this.playerObject;

		// TODO: Remove code duplication!

		if (this.playerObject.action == 0 && this.movements.size() == 0) {

			if (this.lastReachedWaypointId > 0) {
				this.renderer.removeWaypoint(this.lastReachedWaypointId);
				this.lastReachedWaypointId = -1;
			}

			if (waypointsRemain && !renderer.pick)
				startMoving();
			else if (!waypointsRemain) {
				playerObject.x = (float) ((int) playerObject.x);
				playerObject.z = (float) ((int) playerObject.z);
			}

			/*
			 * if (this.movements.size() == 0) {
			 * this.renderer.removeAllWaypoints(); this.lastAddedWaypointId =
			 * -1; }
			 */
		}

		if (this.playerObject.action == 0 && this.movements.size() > 0) {

			int field = ((int) player.z) * levelWidth + ((int) player.x);

			renderer.playerReachedField(field);

			MovementCommand cmd = this.movements.poll();

			switch (cmd) {
			case UP: {
				int nextx = ((int) player.x);
				int nexty = ((int) player.z) + 1;
				int pos = nexty * this.levelWidth + nextx;
				int val = -1;
				if (pos >= 0 && pos < level.length)
					val = level[pos];

				if (val == 0 || val == 3) {
					this.playerObject.moveTo = this.playerObject.z + 1.0f;
					this.playerObject.action = RenderObject.MOVE_TO_Z;
					StatTracker.INSTANCE.increaseMoves();
				} else if (val == 2 || val == 4) {
					int nextpos = pos + this.levelWidth;
					int val2 = level[nextpos];
					if (val2 == 0 || val2 == 3) {
						this.playerObject.moveTo = this.playerObject.z + 1.0f;
						this.playerObject.action = RenderObject.MOVE_TO_Z;
						StatTracker.INSTANCE.increaseMoves(); // TODO: refactor
						// to observer?
						// for playing
						// sounds and
						// stuff...

						RenderObject box = renderer.findObject(pos);
						if (box != null && box.objectRendererIndex == 1) {
							box.action = RenderObject.MOVE_TO_Z;
							box.moveTo = box.z + 1.0f;
							box.fieldId = nextpos;
							level[pos] = (byte) (val == 2 ? 0 : 3);
							level[nextpos] = (byte) (val2 == 0 ? 2 : 4);
							playerObject.actionRenderIndex = 4;

							int current = ((int) player.z) * this.levelWidth + ((int) player.x);
							int val3 = level[current];
							saveUndoStep(pos, val, nextpos, val2, current, val3, box);

							StatTracker.INSTANCE.boxPushed();
						}
					}
				}
				this.playerObject.rot = 0.0f;
				break;
			}
			case DOWN: {
				int nextx = ((int) player.x);
				int nexty = ((int) player.z) - 1;
				int pos = nexty * this.levelWidth + nextx;

				int val = -1;
				if (pos >= 0 && pos < level.length)
					val = level[pos];

				if (val == 0 || val == 3) {
					this.playerObject.moveTo = this.playerObject.z - 1.0f;
					this.playerObject.action = RenderObject.MOVE_TO_Z;
					StatTracker.INSTANCE.increaseMoves();
				} else if (val == 2 || val == 4) {
					int nextpos = pos - this.levelWidth;
					int val2 = level[nextpos];
					if (val2 == 0 || val2 == 3) {
						this.playerObject.moveTo = this.playerObject.z - 1.0f;
						this.playerObject.action = RenderObject.MOVE_TO_Z;
						StatTracker.INSTANCE.increaseMoves();

						RenderObject box = renderer.findObject(pos);
						if (box != null && box.objectRendererIndex == 1) {
							box.action = RenderObject.MOVE_TO_Z;
							box.moveTo = box.z - 1.0f;
							box.fieldId = nextpos;
							level[pos] = (byte) (val == 2 ? 0 : 3);
							level[nextpos] = (byte) (val2 == 0 ? 2 : 4);
							playerObject.actionRenderIndex = 4;

							int current = ((int) player.z) * this.levelWidth + ((int) player.x);
							int val3 = level[current];
							saveUndoStep(pos, val, nextpos, val2, current, val3, box);

							StatTracker.INSTANCE.boxPushed();
						}
					}
				}
				this.playerObject.rot = 180.0f;
				break;
			}
			case LEFT: {
				int nextx = ((int) player.x) + 1;
				int nexty = ((int) player.z);
				int pos = nexty * this.levelWidth + nextx;
				int val = -1;
				if (pos >= 0 && pos < level.length)
					val = level[pos];

				if (val == 0 || val == 3) {
					this.playerObject.moveTo = this.playerObject.x + 1.0f;
					this.playerObject.action = RenderObject.MOVE_TO_X;
					StatTracker.INSTANCE.increaseMoves();
				} else if (val == 2 || val == 4) {
					int nextpos = pos + 1;
					int val2 = level[nextpos];
					if (val2 == 0 || val2 == 3) {
						this.playerObject.moveTo = this.playerObject.x + 1.0f;
						this.playerObject.action = RenderObject.MOVE_TO_X;
						StatTracker.INSTANCE.increaseMoves();

						RenderObject box = renderer.findObject(pos);
						if (box != null && box.objectRendererIndex == 1) {
							box.action = RenderObject.MOVE_TO_X;
							box.moveTo = box.x + 1.0f;
							box.fieldId = nextpos;
							level[pos] = (byte) (val == 2 ? 0 : 3);
							level[nextpos] = (byte) (val2 == 0 ? 2 : 4);
							playerObject.actionRenderIndex = 4;

							int current = ((int) player.z) * this.levelWidth + ((int) player.x);
							int val3 = level[current];
							saveUndoStep(pos, val, nextpos, val2, current, val3, box);

							StatTracker.INSTANCE.boxPushed();
						}
					}
				}
				this.playerObject.rot = 90.0f;
				break;
			}
			case RIGHT: {
				int nextx = ((int) player.x) - 1;
				int nexty = ((int) player.z);
				int pos = nexty * this.levelWidth + nextx;
				int val = -1;
				if (pos >= 0 && pos < level.length)
					val = level[pos];

				if (val == 0 || val == 3) {
					this.playerObject.moveTo = this.playerObject.x - 1.0f;
					this.playerObject.action = RenderObject.MOVE_TO_X;
					StatTracker.INSTANCE.increaseMoves();
				} else if (val == 2 || val == 4) {
					int nextpos = pos - 1;
					int val2 = level[nextpos];
					if (val2 == 0 || val2 == 3) {
						this.playerObject.moveTo = this.playerObject.x - 1.0f;
						this.playerObject.action = RenderObject.MOVE_TO_X;
						StatTracker.INSTANCE.increaseMoves();

						RenderObject box = renderer.findObject(pos);
						if (box != null && box.objectRendererIndex == 1) {
							box.action = RenderObject.MOVE_TO_X;
							box.moveTo = box.x - 1.0f;
							box.fieldId = nextpos;
							level[pos] = (byte) (val == 2 ? 0 : 3);
							level[nextpos] = (byte) (val2 == 0 ? 2 : 4);
							playerObject.actionRenderIndex = 4;

							int current = ((int) player.z) * this.levelWidth + ((int) player.x);
							int val3 = level[current];
							saveUndoStep(pos, val, nextpos, val2, current, val3, box);

							StatTracker.INSTANCE.boxPushed();
						}
					}
				}

				this.playerObject.rot = 270.0f;
				break;
			}
			}

			if (!finished && checkFinished()) {
				finished = true;

				this.renderer.showFgTexture = DroidkobanRenderer.FG_TEXTURE_LEVEL_FINISHED;
				StatTracker.INSTANCE.levelFinished(this.currentStory.id, this.currentLevel + 1);

				Editor edit = this.activity.getSharedPreferences("DroidkobanPrefs", 0).edit();
				edit.putInt("dkLastFinishedLevel", StatTracker.INSTANCE.getLastFinishedLevel(this.currentStory.id));

				StatTracker.INSTANCE.store(edit);

				edit.commit();
			}

		}

	}

	public void tapOnInfo() {
		if (finished) {
			finished = false;

			this.renderer.showFgTexture = DroidkobanRenderer.FG_TEXTURE_NONE;

			toNextLevel = true;
			this.playerObject.moveTo = this.playerObject.y + 25.0f;
			this.playerObject.action = RenderObject.FLY_UP;
			this.playerObject.actionRenderIndex = 3;
		}
	}

	private void saveUndoStep(int pos, int val, int nextpos, int val2, int prevpos, int val3, RenderObject box) {
		UndoAction undo = new UndoAction();
		undo.box = box;
		undo.boxX = box.x;
		undo.boxZ = box.z;
		undo.playerX = playerObject.x;
		undo.playerZ = playerObject.z;
		undo.f1.fieldId = prevpos;
		undo.f1.value = (byte) val3;
		undo.f2.fieldId = pos;
		undo.f2.value = (byte) val;
		undo.f3.fieldId = nextpos;
		undo.f3.value = (byte) val2;
		this.undoHistory.add(undo);
	}

	/**
	 * increases levelcounter and returns the next level
	 * 
	 * @return the next level as byte array
	 */
	public byte[] nextLevel() {

		if (this.currentLevel + 1 > 399) {
			this.currentLevel = -1;
		}

		return this.loadLevel(this.currentLevel + 1);
	}

	public Theme getCurrentTheme(GL10 gl, AssetManager am) {

		if (this.currentTheme == null || this.lastLoadedStory != this.currentStory.id) {
			this.currentTheme = loadTheme(this.currentStory.id, gl, am);
			this.lastLoadedStory = this.currentStory.id;
		}
		else{
			this.currentTheme.reload(gl, am);
		}

		return this.currentTheme;
	}

	public Theme loadTheme(int theme, GL10 gl, AssetManager am) {

		switch (theme) {
		case 0:
			return new DefaultTheme(gl, am);
		case 1:
			return new BeachTheme(gl, am);
		case 2:
			return new WinterTheme(gl, am);
		case 3:
			return new DinosaurTheme(gl, am);
		case 4:
			return new TreasureTheme(gl, am);
		case 5:
			return new TronTheme(gl, am);
		case 6:
			return new SpaceTheme(gl, am);
		case 7:
			return new MayaTheme(gl, am);
		}
		return null;
	}

	public boolean checkFinished() {

		int boxesMatchedCount = 0;
		int i = 0;
		int max = this.level.length;
		byte[] field = this.level;

		for (; i < max; ++i) {
			if (field[i] == 4)
				++boxesMatchedCount;
		}

		StatTracker.INSTANCE.setBoxesInLevel(this.boxesInThisLevel);
		StatTracker.INSTANCE.setBoxesLeft(boxesMatchedCount);

		return boxesMatchedCount == this.boxesInThisLevel;
	}

	public void surfaceCreatedFinished() {
		if (this.resumed) {
			StatTracker.INSTANCE.loadCurrent(this.activity.getApplicationContext().getSharedPreferences("DroidkobanPrefs", 0));
		} else {
			StatTracker.INSTANCE.resetCurrent();
		}

	}

	public void doResume() {

		this.doResume = true;
	}
}
