package de.leihwelt.android.droidkobanpro.select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.R;
import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.*;

public class StorySelectAdapter extends BaseAdapter {

	private LevelStatsInfo info = LevelStatsInfo.INSTANCE;

	class ViewHolder {

		private View base = null;
		private TextView storylabel = null;
		private ImageView storyImage = null;

		public ViewHolder(View base) {
			this.base = base;
		}

		public TextView getLevelLabel() {
			if (this.storylabel == null) {
				this.storylabel = (TextView) base.findViewById(R.id.storyLabel);
			}
			return this.storylabel;
		}
		
		
		public ImageView getIcon() {
			if (this.storyImage == null) {
				 this.storyImage = (ImageView) base.findViewById(R.id.storyImage);
			}
			return this.storyImage;
		}
	}

	private Activity context = null;

	public StorySelectAdapter(Activity context) {
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.storyselectitem, null);
			holder = new ViewHolder(row);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		StoryInfo s = (StoryInfo) this.getItem(position);

		StatTracker st = StatTracker.INSTANCE;
		
		
		String l2 = "Levels: " + s.numberOfLevels;
		String l3 = "Finished: " + st.getLastFinishedLevel(s.id);
		
		holder.getLevelLabel().setText(s.name + '\n' + l2 + '\n' + l3);
		
		// update the icon according to the model elements event type
		 holder.getIcon().setImageResource(s.imageRessource);

		return row;
	}

	public int getCount() {
		return info.storyInfos.size();
	}

	public Object getItem(int position) {
		return info.storyInfos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

}
