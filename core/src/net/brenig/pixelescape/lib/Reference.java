package net.brenig.pixelescape.lib;

/**
 * Created by Jonas Brenig on 02.08.2015.
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

	public static final int GAME_UI_SCORE_SCREEN_SIZE_BUFFER = 20;
	public static final float GAME_UI_MAIN_MENU_FONT_SIZE = 0.8F;

	public static final double STARTING_SPEED = 100D;
	public static final float SPEED_MODIFIER = 3F;

	//PATH ENTITIES
	public static final int PLAYER_ENTITY_SIZE = 10;
	public static final int PATH_ENTITY_SIZE = 6;
	public static final float PATH_ENTITY_ACCELERATION_MOD = 0.0008F;
	public static final int PATH_ENTITY_OFFSET = 16;

	//SIMULATION LIMITS
	public static final float MAX_FRAME_TIME = 1;
	public static final float MAX_ENTITY_SPEED = 1000;

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

	public static final float GRAVITIY_ACCELERATION = -10 * BLOCK_WIDTH;

	public static final float TOUCH_ACCELERATION = 20 * BLOCK_WIDTH;
	public static final float CLICK_ACCELERATION = 200 * BLOCK_WIDTH;


	//////////////////
	// DEBUG CONFIG //
	//////////////////

	public static final boolean PLAYER_EXPLOSION_RED        = false;
	public static final boolean AUTO_PAUSE                  = true;

	public static final boolean SHOW_FPS                    = false;

	public static final boolean DEBUG_MODE_COORDS           = false;
	public static final boolean DEBUG_WORLD_GEN_VALIDATE    = false;
	public static final boolean DEBUG_SCREEN_BOUNDS         = false;
	public static final boolean DEBUG_UI                    = true;
	public static final boolean DEBUG_LOGGING               = true;
	public static final boolean DEBUG_GOD_MODE              = false;
}
