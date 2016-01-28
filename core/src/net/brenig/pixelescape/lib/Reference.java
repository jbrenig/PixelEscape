package net.brenig.pixelescape.lib;

/**
 * Provides default values for PixelEscape
 */
public class Reference {

	/**
	 * this is the targeted x-axis world length, this value may be different on other devices
	 */
	public static final int TARGET_RESOLUTION_X = 800;
	public static final int TARGET_RESOLUTION_Y = 480;
	/**
	 * this is the y-axis world height
	 */
	public static final int GAME_RESOLUTION_Y = 400;
	public static final int GAME_UI_Y_SIZE = 80;
	public static final float GAME_UI_Y_PADDING = 4;

	public static final int GAME_UI_SCORE_SCREEN_SIZE_BUFFER = 20;
	public static final float GAME_UI_MAIN_MENU_FONT_SIZE = 0.8F;

	public static final double STARTING_SPEED = 100D;
	public static final float SPEED_MODIFIER = 3F;

	//PATH ENTITIES
	public static final int PLAYER_ENTITY_SIZE = 10;
	public static final int PATH_ENTITY_SIZE = 6;
	public static final float PATH_ENTITY_ACCELERATION_MOD = 0.05F;
	public static final int PATH_ENTITY_OFFSET = 16;

	//SIMULATION LIMITS
	public static final float MAX_FRAME_TIME = 0.4F;
	public static final float MAX_ENTITY_SPEED = 420;

	/**
	 * dimension of one inworld Block
	 */
	public static final int BLOCK_WIDTH = 20;

	//WORLDGEN
	public static final int MIN_HEIGHT = 1;
	public static final int MAX_HEIGHT = 8;
	/**
	 * This value describes the maximum value of the sum of both top and bottom terrain<br>
	 * This Cannot be more than two times MAX_HEIGHT
	 */
	public static final int MAX_TERRAIN_SUM = 12;

	public static final int TERRAIN_MIN_BUFFER_LEFT = 6;
	public static final int TERRAIN_MIN_BUFFER_RIGHT = 4;
	public static final int TERRAIN_BUFFER = 16;
	public static final int ADDITIONAL_GENERATION_PASSES = 20;

	/**
	 * the world will be rendered this high if the terrain generator fails
	 */
	public static final int FALLBACK_TERRAIN_HEIGHT = 2;
	public static final int STARTING_TERRAIN_HEIGHT = 4;

	public static final int OBSTACLE_MIN_HEIGHT = 3;
	public static final float OBSTACLE_MIN_SPACE = BLOCK_WIDTH * 2F;
	public static final int OBSTACLE_X_CHECK_RADIUS_MAX = 10;

	public static final float GRAVITY_ACCELERATION = -8 * BLOCK_WIDTH;

	public static final float TOUCH_ACCELERATION = 12 * BLOCK_WIDTH;
	public static final float CLICK_ACCELERATION = 3 * BLOCK_WIDTH;

	public static final float SCREEN_TINT_STRENGTH = 0.4F;


	public static final boolean DEBUG_SETTINGS_AVAILABLE    = true;

	/**
	 * current version of saved data<br></br>
	 * changes every time a breaking change happens to the way data is saved on disk
	 */
	public static final int PREFS_REVISION = 1;


}
