package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.data.constants.StyleNames;
import net.brenig.pixelescape.game.data.constants.Textures;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.ui.general.AnimationDrawable;
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack;
import net.brenig.pixelescape.render.ui.general.VerticalSpacer;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Overlay that is displayed at the first start of any gamemode explaining controls and features to the player
 */
public class TutorialOverlay extends OverlayWithUi implements SwipeTabbedStack.IOverSwipeListener, SwipeTabbedStack.IElementChangedListener {

	private final SwipeTabbedStack stack;
	private float timer = Float.MIN_VALUE;
	private static final float timer_amount = 0.2F;
	private final Button buttonLeft;
	private final Button buttonRight;
	private final Table controls;

	public TutorialOverlay(final GameScreen screen) {
		super(screen);

		final GameMode gameMode = screen.getGameMode();

		//Table headLayout = stage.createHeadUiLayoutTable();
		Table contentTable = stage.createContentUiLayoutTable();
		contentTable.defaults().fill();

		final int contentSizeX = screen.world.getWorldWidth() - 40;
		final int contentSizeY = screen.world.getWorldHeight() - 40;
		stack = new SwipeTabbedStack();
		stack.setOverSwipeListener(this);
		stack.setElementChangedListener(this);
		stack.setCycle(false);
		stack.setAllowLeftOverdraw(false);

		stack.add(screen.world.getPlayer().getMovementController().createTutorialWindow(getSkin(), screen, contentSizeX, contentSizeY));

		final int maxLabelWidth = contentSizeX - 60;
		if(gameMode.itemsEnabled()) {
			Table table = new Table(getSkin());
			table.setBackground(Textures.BUTTON_UP);
			table.defaults().padBottom(20);
			{
				Table row1 = new Table();
				{
					Image image = new Image(new AnimationDrawable(screen.game.getGameAssets().getItemAnimatedBackground()));
					row1.add(image).size(64);
				}
				{
					Label lbl = new Label("Collect Items", getSkin(), StyleNames.LABEL_WHITE);
					lbl.setAlignment(Align.left);
					lbl.setColor(Color.PURPLE);

					row1.add(lbl).left();
				}
				{
					Image image = new Image(new AnimationDrawable(screen.game.getGameAssets().getItemAnimatedBackground()));
					row1.add(image).size(64);
				}
				table.add(row1).width(maxLabelWidth).center().fillX();
			}
			table.row();
			{
				Label lbl = new Label("to gain abilities and effects!", getSkin(), StyleNames.LABEL_WHITE);
				lbl.setColor(Color.PURPLE);
				lbl.setAlignment(Align.center);
				lbl.setWrap(true);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}

			stack.add(table);
		}
		if(gameMode.abilitiesEnabled()) {

			Table table = new Table(getSkin());
			table.setBackground(Textures.BUTTON_UP);
			table.defaults().padBottom(20);

			{
				Label lbl = new Label("Use your abilities wisely!", getSkin(), StyleNames.LABEL_WHITE);
				lbl.setColor(Color.NAVY);
				lbl.setWrap(true);
				lbl.setAlignment(Align.center);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}

			stack.add(table);
		}

		gameMode.createCustomTutorial(getSkin(), stack, contentSizeX, contentSizeY);

		contentTable.add(stack).center().fill().pad(20).width(contentSizeX);

		controls = new Table();

		buttonLeft = new Button(getSkin(), StyleNames.BUTTON_ARROW_LEFT);
		buttonRight = new Button(getSkin(), StyleNames.BUTTON_ARROW_RIGHT);

		buttonRight.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(stack.hasNextElement()) {
					stack.next();
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

		contentTable.add(new VerticalSpacer());
		contentTable.row();
		contentTable.add(controls);

		onElementChanged(0);
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
		screen.game.userData.setTutorialSeen(screen.getGameMode(), true);
	}

	@Override
	public void onBeforeFirst() {

	}

	@Override
	public void onElementChanged(int newElement) {
		if(!stack.hasNextElement()) {
			buttonRight.getColor().r = 0;
			buttonRight.getColor().g = 1;
			buttonRight.getColor().b = 0;
		} else {
			buttonRight.getColor().r = 1;
			buttonRight.getColor().g = 1;
			buttonRight.getColor().b = 1;
		}

		if(!stack.hasLastElement()) {
			buttonLeft.setDisabled(true);
		} else {
			buttonLeft.setDisabled(false);
		}
	}
}
