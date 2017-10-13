package net.brenig.pixelescape.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.style.*
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.game.data.constants.Textures
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.lib.info
import net.brenig.pixelescape.render.SimpleAnimation
import net.brenig.pixelescape.render.ui.general.TwoStateImageButton
import net.brenig.pixelescape.render.ui.ingame.AbilityWidget
import java.util.*

/**
 * Holds and loads all game assets
 */
class GameAssets {

    lateinit var defaultFont: BitmapFont private set
    lateinit var playerCrashedSound: Sound private set

    lateinit var mainMenuMusic: Music private set
    lateinit var snpMusic: Music private set
    lateinit var sslMusic: Music private set


    lateinit var buttonNinePatch: NinePatch private set
    lateinit var textureAtlas: TextureAtlas private set

    lateinit var heart: TextureRegion private set

    lateinit var itemFrame: TextureRegion private set
    lateinit var itemBlink: Drawable private set
    lateinit var itemSlow: Drawable private set
    lateinit var itemShield: Drawable private set
    lateinit var itemMove: Drawable private set
    lateinit var heartDrawable: Drawable private set
    lateinit var itemSmallBarricades: Drawable private set
    lateinit var itemScore: Drawable private set

    lateinit var effectItemShield: TextureRegion private set

    lateinit var missingTexture: Drawable private set

    lateinit var square: TextureRegion private set

    lateinit var mainUiSkin: Skin private set

    lateinit var itemAnimatedBackground: SimpleAnimation private set

    fun disposeAll() {
        defaultFont.dispose()
        if (Reference.ENABLE_MUSIC) {
            playerCrashedSound.dispose()
            mainMenuMusic.dispose()
            snpMusic.dispose()
            sslMusic.dispose()
        }

        textureAtlas.dispose()

        mainUiSkin.dispose()
    }

    fun initAll() {
        initFont()
        initTextureAtlas()
        initTextures()
        initSkin()
        initSounds()
        initMusic()
        info("Game assets loaded!")
    }

    private fun initSounds() {
        //load sounds
        playerCrashedSound = Gdx.audio.newSound(Gdx.files.internal("sound/explode.ogg"))
    }

    private fun initMusic() {
        if (Reference.ENABLE_MUSIC) {
            mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SynthPower.ogg"))
            mainMenuMusic.isLooping = true

            snpMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SynthNPiano.ogg"))
            snpMusic.isLooping = true

            sslMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SawSquareLoop.ogg"))
            sslMusic.isLooping = true
        }
    }

    private fun initFont() {
        //Use custom font
        val texture = Texture(Gdx.files.internal("font/p2p_0.png"), false)
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        defaultFont = BitmapFont(Gdx.files.internal("font/p2p.fnt"), TextureRegion(texture))
        defaultFont.color = Color.BLACK
    }

    private fun initTextureAtlas() {
        //load ui textures
        textureAtlas = TextureAtlas(Gdx.files.internal("drawable/main_textures.atlas"))
    }

    private fun initTextures() {
        //Cache default button texture for other use cases
        buttonNinePatch = textureAtlas.createPatch("button")
        square = textureAtlas.findRegion("square")
        heart = textureAtlas.findRegion("heart")

        //ITEMS
        itemBlink = TextureRegionDrawable(textureAtlas.findRegion("item_blink"))
        itemSlow = TextureRegionDrawable(textureAtlas.findRegion("item_slow"))
        itemShield = TextureRegionDrawable(textureAtlas.findRegion("item_shield"))
        itemMove = TextureRegionDrawable(textureAtlas.findRegion("item_move"))
        itemScore = TextureRegionDrawable(textureAtlas.findRegion("item_score"))
        itemSmallBarricades = TextureRegionDrawable(textureAtlas.findRegion("item_small_obstacles"))
        heartDrawable = TextureRegionDrawable(heart)

        effectItemShield = textureAtlas.findRegion("effect_item_shield")

        itemFrame = textureAtlas.findRegion(Textures.ITEM_FRAME)
        itemAnimatedBackground = SimpleAnimation(3, 2, textureAtlas.findRegion("item_blob_filled"), 0.16f, Animation.PlayMode.LOOP_PINGPONG)

        missingTexture = TextureRegionDrawable(textureAtlas.findRegion("fullscreen_hover"))

    }

    private fun initSkin() {
        //Setting up skin
        mainUiSkin = skin {
            // button textures
            add(Textures.BUTTON_UP, buttonNinePatch)
            add(Textures.BUTTON_DOWN, textureAtlas.createPatch("button_clicked"))
            add(Textures.BUTTON_HOVER, textureAtlas.createPatch("button_hover"))
            add(Textures.BUTTON_DISABLED, textureAtlas.createPatch("button_disabled"))

            add(Textures.BUTTON_SETTINGS, textureAtlas.createSprite("gear_settings"))
            add(Textures.BUTTON_SETTINGS_WHITE, textureAtlas.createSprite("gear_settings_white"))


            add(Textures.BUTTON_MUSIC_ENABLED, textureAtlas.createSprite("music_enabled"))
            add(Textures.BUTTON_MUSIC_DISABLED, textureAtlas.createSprite("music_disabled"))
            add(Textures.BUTTON_MUSIC_ENABLED_HOVER, textureAtlas.createSprite("music_enabled_hover"))
            add(Textures.BUTTON_MUSIC_DISABLED_HOVER, textureAtlas.createSprite("music_disabled_hover"))

            add(Textures.BUTTON_SOUND_ENABLED, textureAtlas.createSprite("sound_enabled"))
            add(Textures.BUTTON_SOUND_DISABLED, textureAtlas.createSprite("sound_disabled"))
            add(Textures.BUTTON_SOUND_ENABLED_HOVER, textureAtlas.createSprite("sound_enabled_hover"))
            add(Textures.BUTTON_SOUND_DISABLED_HOVER, textureAtlas.createSprite("sound_disabled_hover"))

            add(Textures.BUTTON_FULLSCREEN, textureAtlas.createSprite("fullscreen"))
            add(Textures.BUTTON_FULLSCREEN_HOVER, textureAtlas.createSprite("fullscreen_hover"))
            add(Textures.BUTTON_RESTORE_WINDOW, textureAtlas.createSprite("fullscreen_restore"))
            add(Textures.BUTTON_RESTORE_WINDOW_HOVER, textureAtlas.createSprite("fullscreen_restore_hover"))

            add(Textures.BUTTON_RIGHT, textureAtlas.createSprite("arrow_right"))
            add(Textures.BUTTON_LEFT, textureAtlas.createSprite("arrow_left"))

            add(Textures.BUTTON_PAUSE, textureAtlas.createSprite("pause"))
            add(Textures.BUTTON_RESUME, textureAtlas.createSprite("resume"))

            add(Textures.ITEM_FRAME, textureAtlas.createSprite("item_frame"))

            add(Textures.SLIDER_BACKGROUND, textureAtlas.createSprite("slider_background"))
            add(Textures.SLIDER_KNOB, textureAtlas.createSprite("slider_knob"))

            add(Textures.CHBX_UNCHECKED, textureAtlas.createSprite("checkbox"))
            add(Textures.CHBX_CHECKED, textureAtlas.createSprite("checkbox_checked"))
            add(Textures.CHBX_HOVER, textureAtlas.createSprite("checkbox_hover"))

            add(Textures.SCROLL_BACKGROUND, textureAtlas.createPatch("scroll_background"))
            add(Textures.SCROLLBAR, textureAtlas.createSprite("scrollbar"))

            imageButton(name = StyleNames.BUTTON_SETTINGS) {
                imageUp = it.getDrawable(Textures.BUTTON_SETTINGS)
                imageOver = it.newDrawable(Textures.BUTTON_SETTINGS_WHITE, Color.LIGHT_GRAY)
                imageDown = it.newDrawable(Textures.BUTTON_SETTINGS_WHITE, Color.GRAY)
                imageDisabled = it.newDrawable(Textures.BUTTON_SETTINGS_WHITE, Color.DARK_GRAY)
            }

            addStyle(name = StyleNames.BUTTON_MUSIC, style = TwoStateImageButton.TwoStateImageButtonStyle()) {
                imageUp = it.getDrawable(Textures.BUTTON_MUSIC_ENABLED)
                image2Up = it.getDrawable(Textures.BUTTON_MUSIC_DISABLED)
                imageOver = it.newDrawable(Textures.BUTTON_MUSIC_ENABLED_HOVER, Color.LIGHT_GRAY)
                image2Over = it.newDrawable(Textures.BUTTON_MUSIC_DISABLED_HOVER, Color.LIGHT_GRAY)
                imageDown = it.newDrawable(Textures.BUTTON_MUSIC_ENABLED_HOVER, Color.GRAY)
                image2Down = it.newDrawable(Textures.BUTTON_MUSIC_DISABLED_HOVER, Color.GRAY)
                imageDisabled = it.newDrawable(Textures.BUTTON_MUSIC_ENABLED_HOVER, Color.DARK_GRAY)
                image2Disabled = it.newDrawable(Textures.BUTTON_MUSIC_DISABLED_HOVER, Color.DARK_GRAY)
            }

            addStyle(name = StyleNames.BUTTON_SOUND, style = TwoStateImageButton.TwoStateImageButtonStyle()) {
                imageUp = it.getDrawable(Textures.BUTTON_SOUND_ENABLED)
                image2Up = it.getDrawable(Textures.BUTTON_SOUND_DISABLED)
                imageOver = it.newDrawable(Textures.BUTTON_SOUND_ENABLED_HOVER, Color.LIGHT_GRAY)
                image2Over = it.newDrawable(Textures.BUTTON_SOUND_DISABLED_HOVER, Color.LIGHT_GRAY)
                imageDown = it.newDrawable(Textures.BUTTON_SOUND_ENABLED_HOVER, Color.GRAY)
                image2Down = it.newDrawable(Textures.BUTTON_SOUND_DISABLED_HOVER, Color.GRAY)
                imageDisabled = it.newDrawable(Textures.BUTTON_SOUND_ENABLED_HOVER, Color.DARK_GRAY)
                image2Disabled = it.newDrawable(Textures.BUTTON_SOUND_DISABLED_HOVER, Color.DARK_GRAY)
            }
            addStyle(name = StyleNames.BUTTON_FULLSCREEN, style = TwoStateImageButton.TwoStateImageButtonStyle()) {
                imageUp = it.getDrawable(Textures.BUTTON_FULLSCREEN)
                image2Up = it.getDrawable(Textures.BUTTON_RESTORE_WINDOW)
                imageOver = it.newDrawable(Textures.BUTTON_FULLSCREEN_HOVER, Color.LIGHT_GRAY)
                image2Over = it.newDrawable(Textures.BUTTON_RESTORE_WINDOW_HOVER, Color.LIGHT_GRAY)
                imageDown = it.newDrawable(Textures.BUTTON_FULLSCREEN_HOVER, Color.GRAY)
                image2Down = it.newDrawable(Textures.BUTTON_RESTORE_WINDOW_HOVER, Color.GRAY)
                imageDisabled = it.newDrawable(Textures.BUTTON_FULLSCREEN_HOVER, Color.DARK_GRAY)
                image2Disabled = it.newDrawable(Textures.BUTTON_RESTORE_WINDOW_HOVER, Color.DARK_GRAY)
            }

            button (name = StyleNames.BUTTON_ARROW_RIGHT) {
                up = it.getDrawable(Textures.BUTTON_RIGHT)
                down = it.newDrawable(Textures.BUTTON_RIGHT, Color.BLACK)
                over = it.newDrawable(Textures.BUTTON_RIGHT, Color.LIGHT_GRAY)
                disabled = it.newDrawable(Textures.BUTTON_RIGHT, Color.DARK_GRAY)
            }

            button (name = StyleNames.BUTTON_ARROW_LEFT) {
                up = it.getDrawable(Textures.BUTTON_LEFT)
                down = it.newDrawable(Textures.BUTTON_LEFT, Color.BLACK)
                over = it.newDrawable(Textures.BUTTON_LEFT, Color.LIGHT_GRAY)
                disabled = it.newDrawable(Textures.BUTTON_LEFT, Color.DARK_GRAY)
            }

            textButton {
                up = it.getDrawable(Textures.BUTTON_UP)
                down = it.getDrawable(Textures.BUTTON_DOWN)
                over = it.getDrawable(Textures.BUTTON_HOVER)
                disabled = it.getDrawable(Textures.BUTTON_DISABLED)
                font = defaultFont
                fontColor = Color.BLACK
                downFontColor = Color.WHITE
                disabledFontColor = Color.GRAY
            }

            addStyle(name = StyleNames.BUTTON_PAUSE, style = ImageTextButton.ImageTextButtonStyle(it.get(TextButton.TextButtonStyle::class.java))) {
                imageUp = it.newDrawable(Textures.BUTTON_PAUSE)
            }

            addStyle(name = StyleNames.BUTTON_RESUME, style = ImageTextButton.ImageTextButtonStyle(it.get(TextButton.TextButtonStyle::class.java))) {
                imageUp = it.newDrawable(Textures.BUTTON_RESUME)
            }

            addStyle(name = StyleNames.DEFAULT, style = AbilityWidget.AbilityButtonStyle()) {
                up = it.getDrawable(Textures.ITEM_FRAME)
                down = it.getDrawable(Textures.ITEM_FRAME)
                over = it.getDrawable(Textures.ITEM_FRAME)
                disabled = it.getDrawable(Textures.ITEM_FRAME)
            }

            label {
                font = defaultFont
                fontColor = Color.BLACK
            }

            label(name = StyleNames.LABEL_WHITE) {
                font = defaultFont
                fontColor = Color.WHITE
            }

            slider {
                background = it.getDrawable(Textures.SLIDER_BACKGROUND)
                knob = it.getDrawable(Textures.SLIDER_KNOB)
            }

            window {
                titleFont = defaultFont
                titleFontColor = Color.BLACK
                background = NinePatchDrawable(buttonNinePatch)
            }

            checkBox {
                checkboxOff = it.getDrawable(Textures.CHBX_UNCHECKED)
                checkboxOn = it.getDrawable(Textures.CHBX_CHECKED)
                checkboxOver = it.getDrawable(Textures.CHBX_HOVER)
                font = defaultFont
                fontColor = Color.BLACK
            }

            scrollPane {
                background = NinePatchDrawable(it.getPatch(Textures.SCROLL_BACKGROUND))
                vScrollKnob = it.getDrawable(Textures.SCROLLBAR)
            }
        }
    }

    fun getRandomGameMusic(random: Random): Music? {
        when (random.nextInt(2)) {
            0 -> return snpMusic
            1 -> return sslMusic
        }
        return null
    }
}
