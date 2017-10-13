package net.brenig.pixelescape.lib

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.render.ui.general.TwoStateImageButton

/**
 * general utilities
 */
object Utils {

    private val buttonSize = (if (PixelEscape.getPixelEscape().gameConfig.useBiggerButtons) 48F else 32F)

    /**
     * creates an instance of Table to use for sound and music controls (unnecessary)
     */
    @JvmStatic
    fun createUIHeadLayout(game: PixelEscape): Table {
        val table = Table()
        val ninePatch = Utils.minimizeNinePatch(game.skin.getDrawable("up") as NinePatchDrawable)
        table.background = ninePatch
        //minimize padding
        table.pad(8f, 8f, 8f, 8f)
        table.defaults().size(buttonSize)
        if (game.gameConfig.useBiggerButtons) {
            table.defaults().pad(2f, 1f, 2f, 1f)
            table.defaults().expand().fillY()
            table.height = Reference.GAME_UI_Y_SIZE.toFloat()
        }
        return table
    }

    @JvmStatic
    fun createDefaultUIHeadControls(): Table {
        val game = PixelEscape.getPixelEscape()
        return Utils.addFullScreenButtonToTable(game, Utils.addSoundAndMusicControllerToLayout(game, createUIHeadLayout(game)))
    }

    /**
     * adds two TwoStateButtons to a Table that are used to control sound and music
     *
     * @param game   instance of the game
     * @param layout the table they should get added to
     * @return the table they got added to
     */
    @JvmStatic
    @JvmOverloads
    fun addSoundAndMusicControllerToLayout(game: PixelEscape, layout: Table = createUIHeadLayout(game)): Table {
        val btnSound = TwoStateImageButton(game.skin, "sound")
        btnSound.state = !game.gameSettings.isSoundEnabled
        btnSound.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                //Invert current selection
                //btn checked --> no sound
                //btn not checked --> sound enabled
                game.gameSettings.isSoundEnabled = btnSound.state
                btnSound.state = !game.gameSettings.isSoundEnabled
            }
        })
        layout.add(btnSound)

        if (Reference.ENABLE_MUSIC) {
            val btnMusic = TwoStateImageButton(game.skin, "music")
            btnMusic.state = !game.gameSettings.isMusicEnabled
            btnMusic.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    //Invert current selection
                    //btn checked --> no music
                    //btn not checked --> music enabled
                    game.gameSettings.isMusicEnabled = btnMusic.state
                    btnMusic.state = !game.gameSettings.isMusicEnabled
                    game.updateMusicPlaying()
                }
            })
            layout.add(btnMusic)
        }
        return layout
    }

    /**
     * adds one TwoStateButtons to a Table that is used to go to fullscreen<br></br>
     * gets skipped if fullscreen is not supported
     *
     * @param layout the table they should get added to
     * @return the table they got added to
     */
    @JvmStatic
    fun addFullScreenButtonToTable(layout: Table): Table {
        return addFullScreenButtonToTable(PixelEscape.getPixelEscape(), layout)
    }

    /**
     * adds one TwoStateButtons to a Table that is used to go to fullscreen<br></br>
     * gets skipped if fullscreen is not supported
     *
     * @param game   instance of the game
     * @param layout the table they should get added to
     * @return the table they got added to
     */
    @JvmStatic
    fun addFullScreenButtonToTable(game: PixelEscape, layout: Table): Table {
        if (game.gameConfig.canGoFullScreen) {
            val btnFullScreen = TwoStateImageButton(game.skin, "fullscreen")
            btnFullScreen.state = game.gameSettings.fullscreen
            btnFullScreen.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    //Invert current selection
                    //btn checked --> fullscreen
                    //btn not checked --> no fullscreen
                    game.gameSettings.fullscreen = !btnFullScreen.state
                    btnFullScreen.state = game.gameSettings.fullscreen
                    game.updateFullscreen()
                }
            })
            layout.add(btnFullScreen)
        }
        return layout
    }

    /**
     * NinePatchDrawables use their total size as minimum size by default
     * This helper function resizes them to their minimum, so they can be resized to be smaller than their total size
     *
     * @param patch The ninepatch to minimize
     * @return the given, minimized Ninepatch
     */
    @JvmStatic
    fun minimizeNinePatch(patch: NinePatchDrawable): Drawable {
        patch.minHeight = patch.patch.bottomHeight + patch.patch.topHeight
        patch.minWidth = patch.patch.leftWidth + patch.patch.rightWidth
        return patch
    }

    @JvmStatic
    fun easeOut(timePassed: Float, maxTime: Float, intensity: Int, target: Float): Float {
        return easeOut(timePassed, maxTime, intensity) * target
    }

    @JvmStatic
    @JvmOverloads
    fun easeOut(timePassed: Float, maxTime: Float, intensity: Int = 3): Float {
        return if (timePassed > maxTime) {
            1f
        } else (1 - Math.pow((1 - timePassed / maxTime).toDouble(), intensity.toDouble())).toFloat()
    }

    @JvmStatic
    fun easeInAndOut(timePassed: Float, maxTime: Float): Float {
        if (timePassed > maxTime) {
            return 1f
        }
        val timeProgress = timePassed / maxTime
        return if (timeProgress < 0.5) {
            2f * timeProgress * timeProgress
        } else {
            -1 + (4 - 2 * timeProgress) * timeProgress
        }
    }

    @JvmStatic
    @JvmOverloads
    fun easeIn(timePassed: Float, maxTime: Float, intensity: Int = 2): Float {
        return if (timePassed > maxTime) {
            1f
        } else Math.pow((timePassed / maxTime).toDouble(), intensity.toDouble()).toFloat()
    }

    @JvmStatic
    fun EaseIn(timePassed: Float, maxTime: Float, intensity: Int, target: Float): Float {
        return easeIn(timePassed, maxTime, intensity) * target
    }
}
