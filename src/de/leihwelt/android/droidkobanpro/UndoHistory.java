package de.leihwelt.android.droidkobanpro;

public class UndoHistory {
	private UndoAction[] history = new UndoAction[5];
	private int readPointer = 0;
	private int writePointer = 0;

	public UndoAction undo() {

		UndoAction un = history[readPointer];
		readPointer--;
		if (readPointer < 0)
			readPointer = 4;

		return un;
	}

	public void put (UndoAction entry){
		history[writePointer] = entry;
		writePointer++;
		writePointer%=5;		
	}
}
