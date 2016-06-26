package net.brenig.pixelescape.game;


public enum CollisionType {
	TERRAIN_TOP_RIGHT(true, false, true, false), TERRAIN_TOP_LEFT(false, true, true, false),
	TERRAIN_BOT_RIGHT(true, false, false, true), TERRAIN_BOT_LEFT(false, true, false, true),
	TERRAIN_TOP(false, false, true, false), TERRAIN_BOTTOM(false, false, true, false),
	TERRAIN_RIGHT(true, false, false, false), TERRAIN_LEFT(false, true, false, false),
	ENTITY(true), OTHER(true), NONE(false);

	private final boolean collideRight;
	private final boolean collideLeft;
	private final boolean collideTop;
	private final boolean collideBot;


	CollisionType(boolean collide) {
		this(collide, collide, collide, collide);
	}

	CollisionType(boolean collideRight, boolean collideLeft, boolean collideTop, boolean collideBot) {
		this.collideRight = collideRight;
		this.collideLeft = collideLeft;
		this.collideTop = collideTop;
		this.collideBot = collideBot;
	}

	public boolean doesCollideRight() {
		return collideRight;
	}

	public boolean doesCollideLeft() {
		return collideLeft;
	}

	public boolean doesCollideTop() {
		return collideTop;
	}

	public boolean doesCollideBot() {
		return collideBot;
	}

	public boolean doesCollide() {
		return collideBot || collideLeft || collideRight || collideTop;
	}

	public boolean doesCollideHorizontally() {
		return collideRight || collideLeft;
	}

	public boolean doesCollideVertically() {
		return collideBot || collideTop;
	}
}
