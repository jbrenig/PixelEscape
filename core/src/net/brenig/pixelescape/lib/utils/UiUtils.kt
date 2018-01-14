package net.brenig.pixelescape.lib.utils

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.render.ui.general.TwoStateImageButton

/**
 *
 */
object UiUtils {

    const val BUTTON_SIZE = 32f


    /**
     * creates an instance of Table to use for sound and music controls, has a default background
     */
    fun createUIHeadLayout(game: PixelEscape): Table {
        val table = Table()
        val ninePatch = (game.skin.getDrawable("up") as NinePatchDrawable).minimize()
        table.background = ninePatch
        //minimize padding
        table.pad(16f, 16f, 16f, 16f)
        table.defaults().expand().fillY()
        return table
    }

    fun createDefaultUIHeadControls(): Table {
        val game = PixelEscape.INSTANCE
        return addFullScreenButtonToTable(game, addSoundAndMusicControllerToLayout(game, createUIHeadLayout(game)))
    }

    /**
     * adds two TwoStateButtons to a Table that are used to control sound and music
     *
     * @param game   instance of the game
     * @param layout the table they should get added to
     * @return the table they got added to
     */
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

        @Suppress("ConstantConditionIf")
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
    fun addFullScreenButtonToTable(layout: Table): Table {
        return addFullScreenButtonToTable(PixelEscape.INSTANCE, layout)
    }

    /**
     * adds one TwoStateButtons to a Table that is used to go to fullscreen<br></br>
     * gets skipped if fullscreen is not supported
     *
     * @param game   instance of the game
     * @param layout the table they should get added to
     * @return the table they got added to
     */
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
}

fun Table.horizontalSpacer() {
    this.add(net.brenig.pixelescape.render.ui.general.HorizontalSpacer())
}