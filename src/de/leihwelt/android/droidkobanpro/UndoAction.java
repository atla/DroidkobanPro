package de.leihwelt.android.droidkobanpro;

import de.leihwelt.android.objects.RenderObject;

public class UndoAction {

	public class ResetField{
		int fieldId = 0;
		byte value = 0;
	}
	
	float playerX = 0.0f;
	float playerZ = 0.0f;
	
	RenderObject box = null;
	float boxX = 0.0f;
	float boxZ = 0.0f;
	
	ResetField f1 = new ResetField ();
	ResetField f2 = new ResetField ();
	ResetField f3 = new ResetField ();
}
