package de.leihwelt.android.objects;

public class MoveTo implements Animation {

	private float x;
	private float y;
	private float z;
	private float speed = 2.7f;
	private RenderObject target;
	private boolean finished = false;

	public MoveTo(RenderObject target, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.target = target;
	}

	public void process(float frameTime) {

		finished = true;

		if (Math.abs(target.x - x) > 0.2) {
			target.x += (x > target.x ? 1.0f : -1.0f) * speed * frameTime;
			finished = false;
		}
		if (Math.abs(target.y - y) > 0.2) {
			target.y += (y > target.y ? 1.0f : -1.0f) * speed * frameTime;
			finished = false;
		}
		if (Math.abs(target.z - z) > 0.2) {
			target.z += (z > target.z ? 1.0f : -1.0f) * speed * frameTime;
			finished = false;
		}
	}

	public boolean finished() {
		
		return finished;
	}

}

/*
 * 
 * if (Math.abs(mPlayerX - mPlayerNextX) > 0.2) { boolean negative =
 * mPlayerNextX > mPlayerX;
 * 
 * mPlayerX += (negative ? 1.0f : -1.0f) * 2.7 * frameTime; moving = true; }
 * else { mPlayerX = mPlayerNextX; }
 * 
 * if (Math.abs(mPlayerY - mPlayerNextY) > 0.2) { boolean negative =
 * mPlayerNextY > mPlayerY;
 * 
 * mPlayerY += (negative ? 1.0f : -1.0f) * 2.7 * frameTime; moving = true; }
 * else { mPlayerY = mPlayerNextY; }
 */