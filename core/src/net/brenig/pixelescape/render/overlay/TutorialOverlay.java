package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.data.constants.StyleNames;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack;
import net.brenig.pixelescape.render.ui.general.VerticalSpacer;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Overlay that is displayed at the first start of any gamemode explaining controls and features to the player
 */
public class TutorialOverlay extends OverlayWithUi implements SwipeTabbedStack.IOverSwipeListener {

	private final SwipeTabbedStack stack;
	private float timer = Float.MIN_VALUE;
	private static final float timer_amount = 0.2F;
	private final Button buttonLeft;
	private final Button buttonRight;
	private final Table controls;

	public TutorialOverlay(final GameScreen screen) {
		super(screen);

		final GameMode gameMode = screen.getGameMode();

		Table headLayout = stage.createHeadUiLayoutTable();
		Table table = stage.createContentUiLayoutTable();
		table.defaults().fill();

		final int contentSizeX = screen.world.getWorldWidth() - 40;
		final int contentSizeY = screen.world.getWorldHeight() - 40;
		stack = new SwipeTabbedStack();
		stack.setOverSwipeListener(this);
		stack.setCycle(false);

		stack.add(screen.world.getPlayer().getMovementController().createTutorialWindow(getSkin(), screen, contentSizeX, contentSizeY));

		if(gameMode.itemsEnabled()) {

		}
		if(gameMode.abilitiesEnabled()) {

		}

		table.add(stack).center().fill().pad(20).width(contentSizeX);

		controls = new Table();

		buttonLeft = new Button(getSkin(), StyleNames.BUTTON_ARROW_LEFT);
		buttonRight = new Button(getSkin(), StyleNames.BUTTON_ARROW_RIGHT);

		buttonRight.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(stack.hasNextElement()) {
					stack.next();
					buttonLeft.setDisabled(false);
					if(!stack.hasNextElement()) {
						buttonRight.getColor().r = 0;
						buttonRight.getColor().g = 1;
						buttonRight.getColor().b = 0;
					}
				} else {
					onAfterLast();
				}
			}
		});
		buttonRight.padLeft(40).padRight(40);

		buttonLeft.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(stack.hasLastElement()) {
					stack.last();
					buttonRight.getColor().r = 1;
					buttonRight.getColor().g = 1;
					buttonRight.getColor().b = 1;
					if(!stack.hasLastElement()) {
						buttonLeft.setDisabled(true);
					}
				}
			}
		});
		buttonLeft.padLeft(40).padRight(40);

		TextButton btnMainMenu = new TextButton("Main Menu", screen.game.getSkin());
		btnMainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				screen.showMainMenu();

			}
		});

		controls.add(buttonLeft).padLeft(40);

		controls.add(new Container<TextButton>(btnMainMenu)).fillX().expandX().center();
		controls.add(buttonRight).padRight(40);

		table.add(new VerticalSpacer());
		table.row();
		table.add(controls);

		if(!stack.hasNextElement()) {
			buttonRight.getColor().r = 0;
			buttonRight.getColor().g = 1;
			buttonRight.getColor().b = 0;
		}

		if(!stack.hasLastElement()) {
			buttonLeft.setDisabled(true);
		}

	}


	@Override
	public void renderFirst(float delta) {
		//noinspection PointlessBooleanExpression,ConstantConditions
		if(Reference.SCREEN_TINT_STRENGTH > 0) {
			if(timer != Float.MIN_VALUE) {
				renderScreenTint(Reference.SCREEN_TINT_STRENGTH * (timer / timer_amount));
			} else {
				renderScreenTint(Reference.SCREEN_TINT_STRENGTH);
			}
		}
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if(timer != Float.MIN_VALUE) {
			if(timer < 0) {
				screen.setOverlay(new CountDownOverlay(screen));
			} else {
				timer -= delta;
			}
		}
	}

	@Override
	public boolean doesPauseGame() {
		return true;
	}

	@Override
	public void onAfterLast() {
		stack.swipeOut(false);
		screen.setOverlayInputProcessor(null);
		controls.addAction(Actions.parallel(Actions.moveBy(0, -90, timer_amount), Actions.alpha(0.2F, timer_amount)));
		timer = timer_amount;
	}

	@Override
	public void onBeforeFirst() {

	}
}
