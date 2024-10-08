package net.brenig.pixelescape.render.overlay

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.lib.LangKeys
import net.brenig.pixelescape.lib.translate
import net.brenig.pixelescape.lib.utils.AnimationUtils
import net.brenig.pixelescape.lib.utils.UiUtils
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer
import net.brenig.pixelescape.render.ui.general.PixelDialog
import net.brenig.pixelescape.render.ui.general.VerticalSpacer
import net.brenig.pixelescape.render.ui.ingame.ScoreWidget
import net.brenig.pixelescape.screen.GameScreen

/**
 * Displays when game gets paused
 */
class GamePausedOverlay(screen: GameScreen, private val isGameOver: Boolean) : OverlayWithUi(screen), InputProcessor {

    private val highscore: Int = screen.game.userData.getHighScore(screen.gameMode)

    private var animationProgress = 0f

    init {

        screen.game.font.data.setScale(1f)

        val table = stage.createHeadUiLayoutTable()

        if (!isGameOver) {
            val btnResume = ImageTextButton(LangKeys.Ingame.RESUME.translate(), screen.game.skin, StyleNames.BUTTON_RESUME)
            btnResume.imageCell.padRight(6f)
            btnResume.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    resumeGame()
                }
            })
            table.add(btnResume)
        }

        table.add(HorizontalSpacer())
        table.add(UiUtils.addFullScreenButtonToTable(UiUtils.addSoundAndMusicControllerToLayout(screen.game, UiUtils.createUIHeadLayout(screen.game))))

        if (!isGameOver) {
            table.add(ScoreWidget(screen))
        }

        val content = stage.createContentUiLayoutTable()
        content.add(VerticalSpacer())

        if (screen.game.gameConfig.gameService.isSessionActive) {
            content.row().expandX().center()

            val btnLeaderboard = UiUtils.createLeaderboardsButton(screen.game, stage, screen::gameMode)
            content.add(btnLeaderboard).padBottom(10f).expandX().center()
            btnLeaderboard.color.a = 0f
            btnLeaderboard.addAction(Actions.sequence(
                    Actions.scaleTo(0.05f, 0.05f),
                    Actions.moveBy(0F, -20F),
                    Actions.delay((if (isGameOver) TIME_TO_WAIT else 0F) + 0.8f),
                    Actions.parallel(
                            Actions.fadeIn(0.2f, Interpolation.fade),
                            Actions.scaleTo(1f, 1f, 0.2f, Interpolation.fade),
                            Actions.moveBy(0F, 20F, 0.2F))
            ))
        }

        content.row().expandX().center()

        val buttonPanel = Table()
        content.add(buttonPanel)

        val btnMainMenu = TextButton(LangKeys.Ingame.MAIN_MENU.translate(), screen.game.skin)
        btnMainMenu.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                gotoMainMenu()

            }
        })
        buttonPanel.add(btnMainMenu).right().bottom().padBottom(40f).padRight(10f)

        val btnRestartGame = TextButton(LangKeys.Ingame.RETRY.translate(), screen.game.skin)
        btnRestartGame.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                restartGame()
            }
        })
        btnRestartGame.isVisible = false
        buttonPanel.add(btnRestartGame).left().bottom().padBottom(40f).padLeft(10f).width(btnMainMenu.width)

        stage.rootTable.layout()

        btnMainMenu.isVisible = false
        btnRestartGame.isVisible = false
        val menuX = btnMainMenu.x
        val restartX = btnRestartGame.x
        btnMainMenu.addAction(Actions.sequence(
                Actions.delay(if (isGameOver) TIME_TO_WAIT else 0F),
                Actions.moveTo(menuX + 1000, btnMainMenu.y),
                Actions.visible(true),
                Actions.moveTo(menuX, btnMainMenu.y, 0.8f, Interpolation.swing)))
        btnRestartGame.addAction(Actions.sequence(
                Actions.delay(if (isGameOver) TIME_TO_WAIT + 0.2f else 0.2f),
                Actions.moveTo(restartX + 500, btnRestartGame.y),
                Actions.visible(true),
                Actions.moveTo(restartX, btnRestartGame.y, 0.8f, Interpolation.swing)))

    }

    override fun show() {
        screen.setOverlayInputProcessor(InputMultiplexer(stage.inputProcessor, this))
        if (isGameOver) {
            screen.game.gameMusic.fadeOutToStop(0.6f)
        } else {
            screen.game.gameMusic.fadeOutToPause()
        }
    }


    override fun renderFirst(delta: Float) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (Reference.SCREEN_TINT_STRENGTH > 0 && animationProgress > 0) {
            renderScreenTint(AnimationUtils.easeOut(animationProgress, if (isGameOver) ANIM_TIME_GAME_OVER else ANIM_TIME_PAUSED, 2) * Reference.SCREEN_TINT_STRENGTH)
        }
    }

    override fun render(delta: Float) {
        //Game Paused
        screen.game.renderManager.begin()
        screen.game.bigFont.data.setScale(1.6f, 3f)
        if (isGameOver) {
            screen.game.bigFont.setColor(1f, 0f, 0f, 1f)
            screen.fontLayout.setText(screen.game.bigFont, LangKeys.Ingame.Overlay.GAME_OVER.translate())
        } else {
            screen.game.bigFont.setColor(0f, 0f, 1f, 1f)
            screen.fontLayout.setText(screen.game.bigFont, LangKeys.Ingame.Overlay.GAME_PAUSED.translate())
        }

        //Slide in
        val gameOverAnim = if (isGameOver) Math.max(0f, screen.world.worldHeight / 2 - screen.world.worldHeight / 2 * AnimationUtils.easeOut(animationProgress, ANIM_TIME_GAME_OVER, 2)) else 0F
        var xPos = screen.world.worldWidth / 2 - screen.fontLayout.width / 2
        val txtGameOverHeight = screen.fontLayout.height / 2
        var yPos = (2 * screen.world.worldHeight / 3).toFloat() + screen.uiPos.toFloat() + txtGameOverHeight + gameOverAnim
        screen.game.bigFont.draw(screen.game.batch, screen.fontLayout, xPos, yPos)

        //Score
        screen.game.bigFont.setColor(0f, 1f, 0f, 1f)
        screen.game.bigFont.data.setScale(1f)
        screen.fontLayout.setText(screen.game.bigFont, LangKeys.Ingame.Overlay.YOUR_SCORE.translate(screen.world.player.score))
        xPos = screen.world.worldWidth / 2 - screen.fontLayout.width / 2
        val txtScoreHeight = screen.fontLayout.height / 2
        yPos -= txtGameOverHeight + screen.game.bigFont.lineHeight + txtScoreHeight
        screen.game.bigFont.draw(screen.game.batch, screen.fontLayout, xPos, yPos)

        //Highscore
        if (isGameOver && highscore < screen.world.player.score) {
            screen.game.bigFont.setColor(0f, 1f, 0f, 1f)
            screen.game.bigFont.data.setScale(1f)
            screen.fontLayout.setText(screen.game.bigFont, LangKeys.Ingame.Overlay.NEW_HIGHSCORE.translate())
        } else {
            screen.game.bigFont.setColor(0f, 0f, 1f, 1f)
            screen.game.bigFont.data.setScale(0.8f)
            screen.fontLayout.setText(screen.game.bigFont, LangKeys.Ingame.Overlay.HIGHSCORE.translate(highscore))
        }
        xPos = screen.world.worldWidth / 2 - screen.fontLayout.width / 2
        val txtHighscoreHeight = screen.fontLayout.height / 2
        yPos -= screen.game.bigFont.lineHeight + txtHighscoreHeight
        screen.game.bigFont.draw(screen.game.batch, screen.fontLayout, xPos, yPos)

        animationProgress += delta

        super.render(delta)
        screen.game.bigFont.data.setScale(1f)
    }

    private fun restartMusic() {
        if (isGameOver) {
            if (screen.game.gameConfig.musicAvailable) {
                screen.game.gameMusic.currentMusic = screen.gameMusic
            }
        }
        screen.game.gameMusic.play(true)
    }

    override fun doesPauseGame(): Boolean {
        return true
    }

    override fun canHideCursor(): Boolean {
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        if (!isGameOver || animationProgress > TIME_TO_WAIT) {
            when (keycode) {
                Input.Keys.SPACE -> {
                    if (isGameOver) {
                        restartGameDo()
                    } else {
                        resumeGame()
                    }
                    return true
                }
                Input.Keys.ESCAPE -> {
                    gotoMainMenu()
                    return true
                }
            }
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    private fun resumeGame() {
        screen.setOverlay(CountDownOverlay(screen))
        restartMusic()
    }

    /**
     * display dialog asking to go to main menu
     */
    private fun gotoMainMenu() {
        if (isGameOver) {
            screen.showMainMenu()
        } else {
            val dialog = PixelDialog(LangKeys.Ingame.Dialog.MAIN_MENU_TITLE.translate(), screen.game.skin)
            dialog.isMovable = false
            dialog.isModal = true
            dialog.prefWidth = stage.stageViewport.worldWidth * 0.7f
            dialog.width = stage.stageViewport.worldWidth * 0.7f
            dialog.label(LangKeys.Ingame.Dialog.MAIN_MENU_TEXT.translate())
            dialog.buttonYes(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    dialog.hide()
                    screen.showMainMenu()
                }
            })
            dialog.buttonNo(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    dialog.hide()
                }
            })
            dialog.init()
            dialog.show(stage.uiStage)
        }
    }

    private fun restartGame() {
        if (isGameOver) {
            restartGameDo()
        } else {
            val dialog = PixelDialog(LangKeys.Ingame.Dialog.RESTART_TITLE.translate(), screen.game.skin)
            dialog.isMovable = false
            dialog.isModal = true
            dialog.prefWidth = stage.stageViewport.worldWidth * 0.7f
            dialog.width = stage.stageViewport.worldWidth * 0.7f
            dialog.label(LangKeys.Ingame.Dialog.RESTART_TEXT.translate())
            dialog.buttonYes(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    dialog.hide()
                    restartGameDo()
                }
            })
            dialog.buttonNo(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    dialog.hide()
                }
            })
            dialog.init()
            dialog.show(stage.uiStage)
        }
    }

    private fun restartGameDo() {
        screen.resetToEmptyOverlay()
        screen.restart()
        restartMusic()
    }

    override fun switchToPausedOverlayOnFocusChange(): Boolean {
        return false
    }

    companion object {

        private val ANIM_TIME_GAME_OVER = 0.6f
        private val ANIM_TIME_PAUSED = 0.4f
        private val TIME_TO_WAIT = 1.2f
    }
}
