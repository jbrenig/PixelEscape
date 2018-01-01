package net.brenig.pixelescape.android

import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.lib.error

/**
 *
 */
object PlayServicesMapper {

    private val leaderboards = hashMapOf(
            GameMode.CLASSIC.gameModeName           to "CgkI4pKZvp8aEAIQAg",
            GameMode.ARCADE.gameModeName            to "CgkI4pKZvp8aEAIQAw",
            GameMode.BLINK.gameModeName             to "CgkI4pKZvp8aEAIQBA",
            GameMode.DRAG.gameModeName              to "CgkI4pKZvp8aEAIQBQ",
            GameMode.FLASH.gameModeName             to "CgkI4pKZvp8aEAIQBg",
            GameMode.SPEED.gameModeName             to "CgkI4pKZvp8aEAIQBw"
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