package net.brenig.pixelescape.game.worldgen;

/**
 * Created by Jonas Brenig on 03.08.2015.
 */
public class TerrainPair {

	public int top, bottom;

	public TerrainPair(int top, int bottom) {
		this.top = top;
		this.bottom = bottom;
	}

	public int getTop() {
		return top;
	}

	public int getBottom() {
		return bottom;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}
}
