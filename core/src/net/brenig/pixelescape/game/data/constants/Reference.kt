package net.brenig.pixelescape.game.data.constants

/**
 * Provides default values for PixelEscape
 */
object Reference {

    //SCREEN SIZING & UI LAYOUT CONSTANTS

    /**
     * this is the y-axis world height
     */
    const val GAME_RESOLUTION_Y = 416

    /**
     * in-game-UI-Bar height
     */
    const val GAME_UI_Y_SIZE = 64
    const val GAME_UI_Y_PADDING = 4f
    const val GAME_UI_Y_SIZE_BUTTON_PANEL = Reference.GAME_UI_Y_SIZE.toFloat() - 2 * Reference.GAME_UI_Y_PADDING

    /**
     * this is the targeted x-axis world length, this value may be different on other devices
     */
    const val TARGET_RESOLUTION_X = 800
    const val TARGET_RESOLUTION_Y = GAME_RESOLUTION_Y + GAME_UI_Y_SIZE

    const val GAME_UI_SCORE_SCREEN_SIZE_BUFFER = 20
    const val GAME_UI_MAIN_MENU_FONT_SIZE = 1f
    const val GAME_UI_SMALL_FONT_SIZE = 1f

    const val SCREEN_TINT_STRENGTH = 0.6f

    //DEFAULT SPEED MODIFIERS
    const val STARTING_SPEED = 100f
    const val SPEED_MODIFIER = 3f

    //PATH ENTITIES
    const val PLAYER_ENTITY_SIZE = 10
    const val PATH_ENTITY_SIZE = 6
    const val PATH_ENTITY_ACCELERATION_MOD = 0.05f
    const val PATH_ENTITY_OFFSET = 16

    //SIMULATION LIMITS
    const val MAX_FRAME_TIME = 0.03f
    const val MAX_ENTITY_SPEED = 420f

    /**
     * dimension of one inworld Block
     */
    const val BLOCK_WIDTH = 20

    //WORLDGEN
    const val MIN_HEIGHT = 1
    const val MAX_HEIGHT = 8
    /**
     * This value describes the maximum value of the sum of both top and bottom terrain<br></br>
     * This Cannot be more than two times MAX_HEIGHT
     */
    const val MAX_TERRAIN_SUM = 12

    /**
     * amount of blocks that is left of the screen at all times
     */
    const val TERRAIN_BUFFER_LEFT = 2

    /**
     * amount of blocks that is not visible on screen but already generated (needs to be higher that [.TERRAIN_GENERATION_THRESHOLD], otherwise not enough blocks are available toe the right of the screen)
     */
    const val TERRAIN_BUFFER = 32
    /**
     * minimum number of blocks to start generating new terrain
     */
    const val TERRAIN_GENERATION_THRESHOLD = 16


    const val ADDITIONAL_GENERATION_PASSES = 0

    /**
     * the world will be rendered this high if the terrain generator fails
     */
    const val FALLBACK_TERRAIN_HEIGHT = 4
    const val STARTING_TERRAIN_HEIGHT = 4

    const val OBSTACLE_MIN_HEIGHT = 3
    const val OBSTACLE_MIN_SPACE = BLOCK_WIDTH * 2f
    const val OBSTACLE_X_CHECK_RADIUS_MAX = 12

    const val GRAVITY_ACCELERATION = -8f * BLOCK_WIDTH

    const val TOUCH_ACCELERATION = 12f * BLOCK_WIDTH
    const val CLICK_ACCELERATION = 3f * BLOCK_WIDTH

    //DEBUG FLAGS
    const val SUPPRESS_TUTORIALS = true

    const val ENABLE_MUSIC = true

    const val DEBUG_SETTINGS_AVAILABLE = true

    //VERSION CONSTANTS
    /**
     * current version of saved data<br></br>
     * changes every time a breaking change happens to the way data is saved on disk
     */
    const val PREFS_REVISION = 2


}
