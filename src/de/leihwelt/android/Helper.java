package de.leihwelt.android;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class Helper {
//
//	public static boolean isIntentAvailable(Context context, String action) {
//		final PackageManager packageManager = context.getPackageManager();
//		final Intent intent = new Intent(action);
//		List list = packageManager.queryIntentActivities(intent,
//				PackageManager.MATCH_DEFAULT_ONLY);
//		return list.size() > 0;
//	}

	public static void openWebURL(String inURL, Context ctx) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));

		ctx.startActivity(browse);
	}
}
