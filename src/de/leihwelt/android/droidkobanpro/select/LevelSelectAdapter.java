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

public class LevelSelectAdapter extends BaseAdapter {

	private LevelStatsInfo info = LevelStatsInfo.INSTANCE;
	private int story = 0;
	private int storyImage = 0;
	
	class ViewHolder {

		private View base = null;
		private TextView levelLabel = null;

		private ImageView icon = null;
		private TextView status = null;

		public ViewHolder(View base) {
			this.base = base;
		}

		public TextView getLevelLabel() {
			if (this.levelLabel == null) {
				this.levelLabel = (TextView) base.findViewById(R.id.levelLabel);
			}
			return this.levelLabel;
		}
		
		public TextView getStatus() {
			if (this.status == null) {
				this.status  = (TextView) base.findViewById(R.id.status);
			}
			return this.status;
		}
		
		public ImageView getIcon (){
			if (this.icon == null){
				this.icon = (ImageView)base.findViewById(R.id.image);
			}
			return this.icon;
		}
	}

	private Activity context = null;
	private boolean finished = false;

	public LevelSelectAdapter(Activity context) {
		this.context = context;
		
		
	}

	public void setStory (int story){
		this.story = story;
		this.storyImage = LevelStatsInfo.INSTANCE.getStoryResourceFor(story);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.levelselectitem, null);
			holder = new ViewHolder(row);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		LevelInfo s = (LevelInfo) this.getItem(position);

		holder.getLevelLabel().setText("Level " + s.levelNumber);
		
		finished  = (position > StatTracker.INSTANCE.getLastFinishedLevel(this.story));
		
		holder.getLevelLabel().setTextColor(finished ? 0xAAAAAAAA : 0xFFFFFFFF);
		holder.getIcon().setImageResource(storyImage);
		holder.getStatus().setVisibility(!finished ? View.VISIBLE : View.INVISIBLE);

		return row;
	}

	public int getCount() {
		return info.stats.size();
	}

	public Object getItem(int position) {
		return info.stats.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

}
