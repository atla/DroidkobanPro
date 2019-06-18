package de.leihwelt.android.droidkobanpro.achievements.view;

import java.util.ArrayList;

import de.leihwelt.android.droidkobanpro.achievements.*;
import de.leihwelt.android.droidkobanpro.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.view.*;
import android.widget.*;

public class AchievementSelectAdapter extends BaseAdapter {

	// private LevelStatsInfo info = LevelStatsInfo.INSTANCE;

	private ArrayList<Achievement> achievements = AchievementUnlocker.INSTANCE.getAchievements();

	class ViewHolder {

		private View base = null;
		private TextView title = null;
		private TextView desc = null;
		private ImageView icon = null;
		private ImageView points = null;

		public ViewHolder(View base) {
			this.base = base;
		}

		public TextView getTitle() {
			if (this.title == null) {
				this.title = (TextView) base.findViewById(R.id.aTitleLabel);
			}
			return this.title;
		}

		public TextView getDesc() {
			if (this.desc == null) {
				this.desc = (TextView) base.findViewById(R.id.aDescLabel);
			}
			return this.desc;
		}

		public ImageView getIcon() {
			if (this.icon == null) {
				this.icon = (ImageView) base.findViewById(R.id.aImage);
			}
			return this.icon;
		}

		public ImageView getPoints() {
			if (this.points == null) {
				this.points = (ImageView) base.findViewById(R.id.pointView);
			}
			return this.points;
		}
	}

	private Activity context = null;

	public AchievementSelectAdapter(Activity context) {
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.achievementitem, null);
			holder = new ViewHolder(row);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		Achievement a = (Achievement) this.getItem(position);

		holder.getTitle().setText(a.title);		

		holder.getTitle().setTextColor(a.unlocked ? 0xFFFFFFFF : 0xAAAAAAAA);
		holder.getDesc().setTextColor(a.unlocked ? 0xFFFFFFFF : 0xAAAAAAAA);

		if (a.unlocked) {
			holder.getIcon().setImageResource(R.drawable.aitem);

			holder.getDesc().setText(a.description);
			
			if (a.points == 5)
				holder.getPoints().setImageResource(R.drawable.five);
			else if (a.points == 10)
				holder.getPoints().setImageResource(R.drawable.ten);
			else
				holder.getPoints().setImageResource(R.drawable.twenfive);

		} else {
			holder.getIcon().setImageResource(R.drawable.aitem_in);
			holder.getPoints().setImageResource(0);
			holder.getDesc().setText("");
		}

		return row;
	}

	public int getCount() {
		return this.achievements.size();
	}

	public Object getItem(int position) {
		return this.achievements.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

}
