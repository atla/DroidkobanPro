package de.leihwelt.android.droidkobanpro;

import android.app.Activity;
import android.widget.Toast;
import de.leihwelt.android.droidkobanpro.achievements.Achievement;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker.AchievementListener;

public class OpenFeintAchievementUnlocker implements AchievementListener {

	@Override
	public void gained(Achievement achievement) {

		new com.openfeint.api.resource.Achievement(achievement.feintId).unlock(new com.openfeint.api.resource.Achievement.UnlockCB() {
			@Override
			public void onSuccess(boolean success) {

			}

			@Override
			public void onFailure(String exceptionMessage) {
				Toast.makeText(DroidkobanGame.INSTANCE.activity, "Error (" + exceptionMessage + ") unlocking achievement.", Toast.LENGTH_SHORT).show();

			}

		});

	}

}
