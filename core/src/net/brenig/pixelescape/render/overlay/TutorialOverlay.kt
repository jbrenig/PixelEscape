package net.brenig.pixelescape.render.overlay

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.game.data.constants.Textures
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.render.ui.general.AnimationDrawable
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack
import net.brenig.pixelescape.render.ui.general.VerticalSpacer
import net.brenig.pixelescape.screen.GameScreen

/**
 * Overlay that is displayed at the first start of any gamemode explaining controls and features to the player
 */
class TutorialOverlay(screen: GameScreen) : OverlayWithUi(screen), SwipeTabbedStack.IOverSwipeListener, SwipeTabbedStack.IElementChangedListener {

    private val stack: SwipeTabbedStack
    private var timer = java.lang.Float.MIN_VALUE
    private val buttonLeft: Button
    private val buttonRight: Button
    private val controls: Table

    init {

        val gameMode = screen.gameMode

        //Table headLayout = stage.createHeadUiLayoutTable();
        val contentTable = stage.createContentUiLayoutTable()
        contentTable.defaults().fill()

        val contentSizeX = screen.world.worldWidth - 40
        val contentSizeY = screen.world.worldHeight - 40
        stack = SwipeTabbedStack()
        stack.setOverSwipeListener(this)
        stack.setElementChangedListener(this)
        stack.setCycle(false)
        stack.setAllowLeftOverdraw(false)

        stack.add(screen.world.player.movementController.createTutorialWindow(skin, screen, contentSizeX, contentSizeY))

        val maxLabelWidth = contentSizeX - 60
        if (gameMode.itemsEnabled()) {
            val table = Table(skin)
            table.setBackground(Textures.BUTTON_UP)
            table.defaults().padBottom(20f)
            run {
                val row1 = Table()
                run {
                    val image = Image(AnimationDrawable(screen.game.gameAssets.itemAnimatedBackground))
                    row1.add(image).size(64f)
                }
                run {
                    val lbl = Label("Collect Items", skin, StyleNames.LABEL_WHITE)
                    lbl.setAlignment(Align.left)
                    lbl.color = Color.PURPLE

                    row1.add(lbl).left()
                }
                run {
                    val image = Image(AnimationDrawable(screen.game.gameAssets.itemAnimatedBackground))
                    row1.add(image).size(64f)
                }
                table.add(row1).width(maxLabelWidth.toFloat()).center().fillX()
            }
            table.row()
            run {
                val lbl = Label("to gain abilities and effects!", skin, StyleNames.LABEL_WHITE)
                lbl.color = Color.PURPLE
                lbl.setAlignment(Align.center)
                lbl.setWrap(true)
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }

            stack.add(table)
        }
        if (gameMode.abilitiesEnabled()) {

            val table = Table(skin)
            table.setBackground(Textures.BUTTON_UP)
            table.defaults().padBottom(20f)

            run {
                val lbl = Label("Use your abilities wisely!", skin, StyleNames.LABEL_WHITE)
                lbl.color = Color.NAVY
                lbl.setWrap(true)
                lbl.setAlignment(Align.center)
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }

            stack.add(table)
        }

        gameMode.createCustomTutorial(skin, stack, contentSizeX, contentSizeY)

        contentTable.add(stack).center().fill().pad(20f).width(contentSizeX.toFloat())

        controls = Table()

        buttonLeft = Button(skin, StyleNames.BUTTON_ARROW_LEFT)
        buttonRight = Button(skin, StyleNames.BUTTON_ARROW_RIGHT)

        buttonRight.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (stack.hasNextElement()) {
                    stack.next()
                } else {
                    onAfterLast()
                }
            }
        })
        buttonRight.padLeft(40f).padRight(40f)

        buttonLeft.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (stack.hasLastElement()) {
                    stack.last()
                }
            }
        })
        buttonLeft.padLeft(40f).padRight(40f)

        val btnMainMenu = TextButton("Main Menu", screen.game.skin)
        btnMainMenu.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                screen.showMainMenu()

            }
        })

        controls.add(buttonLeft).padLeft(40f)

        controls.add(Container(btnMainMenu)).fillX().expandX().center()
        controls.add(buttonRight).padRight(40f)

        contentTable.add(VerticalSpacer())
        contentTable.row()
        contentTable.add(controls)

        onElementChanged(0)
    }


    override fun renderFirst(delta: Float) {
        @Suppress("ConstantConditionIf")
        if (Reference.SCREEN_TINT_STRENGTH > 0) {
            if (timer != java.lang.Float.MIN_VALUE) {
                renderScreenTint(Reference.SCREEN_TINT_STRENGTH * (timer / timer_amount))
            } else {
                renderScreenTint(Reference.SCREEN_TINT_STRENGTH)
            }
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (timer != java.lang.Float.MIN_VALUE) {
            if (timer < 0) {
                screen.setOverlay(CountDownOverlay(screen))
            } else {
                timer -= delta
            }
        }
    }

    override fun doesPauseGame(): Boolean {
        return true
    }

    override fun onAfterLast() {
        stack.swipeOut(false)
        screen.setOverlayInputProcessor(null)
        controls.addAction(Actions.parallel(Actions.moveBy(0f, -90f, timer_amount), Actions.alpha(0.2f, timer_amount)))
        timer = timer_amount
        screen.game.userData.setTutorialSeen(screen.gameMode, true)
    }

    override fun onBeforeFirst() {

    }

    override fun onElementChanged(newElement: Int) {
        if (!stack.hasNextElement()) {
            buttonRight.color.r = 0f
            buttonRight.color.g = 1f
            buttonRight.color.b = 0f
        } else {
            buttonRight.color.r = 1f
            buttonRight.color.g = 1f
            buttonRight.color.b = 1f
        }

        if (!stack.hasLastElement()) {
            buttonLeft.isDisabled = true
        } else {
            buttonLeft.isDisabled = false
        }
    }

    override fun switchToPausedOverlayOnFocusChange(): Boolean {
        return false
    }

    companion object {
        private const val timer_amount = 0.2f
    }
}
