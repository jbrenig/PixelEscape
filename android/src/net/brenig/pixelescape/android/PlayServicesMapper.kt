package net.brenig.pixelescape.android

import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.lib.error

/**
 *
 */
object PlayServicesMapper {

    private val leaderboards = hashMapOf(
            GameMode.CLASSIC.gameModeName           to "",
            GameMode.ARCADE.gameModeName            to "",
            GameMode.BLINK.gameModeName             to "",
            GameMode.DRAG.gameModeName              to "",
            GameMode.FLASH.gameModeName             to "",
            GameMode.SPEED.gameModeName             to ""
    )

    fun mapAchievement(id: String) : String? {
        error("Unknown achievement: $id")
        return null
    }

    fun mapLeaderboard(id: String) : String? {
        val out = leaderboards[id]
        if (out == null) {
            error("Unknown leader board: $id")
            return null
        }
        return out
    }
}