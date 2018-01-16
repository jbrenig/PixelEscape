package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Scaling
import de.golfgl.gdxgamesvcs.IGameServiceClient
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.lib.LangKeys
import net.brenig.pixelescape.lib.translate
import net.brenig.pixelescape.lib.utils.UiUtils

/**
 *
 */
class PlayServiceLoginButton(
        val styleLogin: ImageTextButtonStyle,
        val styleWorking: ImageTextButtonStyle,
        val styleLogout: ImageTextButtonStyle,
        val playService: IGameServiceClient) : ImageTextButton("", styleWorking) {

    var state = State.WORKING
        set(value) {
            field = value
            onStateChanged()
        }

    constructor(skin: Skin, styleLogin: String, styleWorking: String, styleLogout: String, playService: IGameServiceClient) : this(
            skin.get(styleLogin, ImageTextButtonStyle::class.java),
            skin.get(styleWorking, ImageTextButtonStyle::class.java),
            skin.get(styleLogout, ImageTextButtonStyle::class.java),
            playService)

    init {
        pad(8F)
        image.setScaling(Scaling.fit)
        imageCell.size(UiUtils.BUTTON_SIZE)
        imageCell.fill()
        image.setSize(UiUtils.BUTTON_SIZE, UiUtils.BUTTON_SIZE)
        label.setFontScale(Reference.GAME_UI_SMALL_FONT_SIZE)
        updateInfo()
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                with(playService) {
                    when (state) {
                        State.READY_FOR_LOGIN -> {
                            if (!isConnectionPending && !isSessionActive) {
                                state = State.WORKING
                                logIn()
                            } else {
                                updateInfo()
                            }
                        }
                        State.READY_FOR_LOGOUT ->
                            if (!isConnectionPending && isSessionActive) {
                                state = State.WORKING
                                logOff()
                            } else {
                                updateInfo()
                            }
                        State.WORKING -> updateInfo()
                    }
                }
            }
        })

    }

    fun updateInfo() {
        state = when {
            playService.isConnectionPending -> State.WORKING
            playService.isSessionActive -> State.READY_FOR_LOGOUT
            else -> State.READY_FOR_LOGIN
        }
    }

    private fun onStateChanged() {
        when (state) {
            State.READY_FOR_LOGIN -> {
                style = styleLogin
                text = LangKeys.PlaySerices.LOGIN.translate()
                isDisabled = false
            }
            State.READY_FOR_LOGOUT -> {
                style = styleLogout
                text = LangKeys.PlaySerices.LOGOUT.translate()
                isDisabled = false
            }
            State.WORKING -> {
                style = styleWorking
                text = LangKeys.PlaySerices.WORKING.translate()
                isDisabled = true
            }
        }
    }


    enum class State {
        READY_FOR_LOGIN, READY_FOR_LOGOUT, WORKING
    }
}