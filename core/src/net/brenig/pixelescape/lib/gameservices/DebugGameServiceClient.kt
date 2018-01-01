package net.brenig.pixelescape.lib.gameservices

import com.badlogic.gdx.utils.Array
import de.golfgl.gdxgamesvcs.MockGameServiceClient
import de.golfgl.gdxgamesvcs.achievement.IAchievement
import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry

/**
 *
 */
class DebugGameServiceClient : MockGameServiceClient(LATENCY) {
    private val achievements = Array<IAchievement>(10)
    private val leaderboards = Array<ILeaderBoardEntry>(10)

    override fun getAchievements() = achievements

    override fun getLeaderboardEntries() = leaderboards

    override fun getPlayerName() = "Test"

    override fun getGameState() = ByteArray(0)

    override fun getGameStates() = Array<String>()

    companion object {
        const val LATENCY = 2F
    }
}