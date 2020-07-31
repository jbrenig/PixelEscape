package net.brenig.pixelescape.android

import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.lib.error

/**
 *
 */
class PlayServicesMapper {

    companion object {

        @JvmStatic
        private val leaderboards = hashMapOf(
                GameMode.CLASSIC.scoreboardName to "CgkI4pKZvp8aEAIQAg",
                GameMode.ARCADE.scoreboardName to "CgkI4pKZvp8aEAIQAw",
                GameMode.BLINK.scoreboardName to "CgkI4pKZvp8aEAIQBg",
                GameMode.DRAG.scoreboardName to "CgkI4pKZvp8aEAIQBw",
                GameMode.FLASH.scoreboardName to "CgkI4pKZvp8aEAIQBQ",
                GameMode.SPEED.scoreboardName to "CgkI4pKZvp8aEAIQBA"
        )

        @JvmStatic
        fun mapAchievement(id: String): String? {
            error("Unknown achievement: $id")
            return null
        }

        @JvmStatic
        fun mapLeaderboard(id: String): String? {
            val out = leaderboards[id]
            if (out == null) {
                error("Unknown leader board: $id")
                return null
            }
            return out
        }
    }
}