package net.brenig.pixelescape.lib

import net.brenig.pixelescape.PixelEscape

/**
 *
 */
object LangKeys {
    const val GAME = "game"
    const val LEADERBOARD = "ui.leaderboard"
    const val BTN_BACK = "btn.back"
    const val BTN_FINISH = "btn.finish"
    const val DIALOG_YES = "ui.dialog.yes"
    const val DIALOG_NO = "ui.dialog.no"

    object MainMenu {
        const val TITLE = "ui.main_menu.title"
        const val START = "ui.main_menu.start_game"
        const val EXIT = "ui.main_menu.exit_game"
        const val HIGHSCORE = "ui.main_menu.highscore_widget"
        const val LEADERBOARD_TOOLTIP = "ui.main_menu.leaderboard_tooltip"
    }

    object Settings {
        const val TITLE = "ui.settings.title"
        const val LANGUAGE = "ui.settings.language"
        const val MUSIC = "ui.settings.music"
        const val SOUND = "ui.settings.sound"
        const val COUNTDOWN = "ui.settings.countdown"
        const val HIGHSCORE_IN_WORLD = "ui.settings.highscore_in_world"
        const val RESET_SCORE = "ui.settings.reset_scores"
    }

    object ResetScores {
        const val TITLE = "ui.reset_scores.title"
        const val RESET_ALL = "ui.reset_scores.reset_all"
        const val DIALOG_TITLE = "ui.reset_scores.dialog.title"
        const val DIALOG_TEXT = "ui.reset_scores.dialog.text"
    }

    object DebugSettings {
        const val TITLE = "ui.debug_settings.dialog.title"
        const val LINE1 = "ui.debug_settings.dialog.line1"
        const val LINE2 = "ui.debug_settings.dialog.line2"
    }

    object PlaySerices {
        const val LOGIN = "ui.gs.login"
        const val LOGOUT = "ui.gs.logout"
        const val WORKING = "ui.gs.working"
    }

    object Ingame {
        const val PAUSE = "ui.ingame.pause"
        const val RESUME = "ui.ingame.resume"
        const val RETRY = "ui.ingame.retry"
        const val MAIN_MENU = "ui.ingame.goto_main_menu"
        const val SCORE_WIDGET = "ui.ingame.score_widget"

        object Overlay {
            const val GAME_PAUSED = "ui.ingame.overlay.game_paused"
            const val GAME_OVER = "ui.ingame.overlay.game_over"
            const val YOUR_SCORE = "ui.ingame.overlay.your_score"
            const val HIGHSCORE = "ui.ingame.overlay.highscore"
            const val NEW_HIGHSCORE = "ui.ingame.overlay.new_highscore"
            const val COUNTDOWN_GO = "ui.ingame.overlay.countdown_go"
            const val COUNTDOWN_READY = "ui.ingame.overlay.countdown_ready"
        }

        object Dialog {
            const val RESTART_TITLE = "ui.ingame.dialog.restart.title"
            const val RESTART_TEXT = "ui.ingame.dialog.restart.text"
            const val MAIN_MENU_TITLE = "ui.ingame.dialog.main_menu.title"
            const val MAIN_MENU_TEXT = "ui.ingame.dialog.main_menu.text"
        }
    }
}

fun String.translate(game: PixelEscape = PixelEscape.INSTANCE): String {
    return game.gameAssets.language[this]
}

fun String.translate(vararg args: Any, game: PixelEscape = PixelEscape.INSTANCE): String {
    return game.gameAssets.language.format(this, *args)
}