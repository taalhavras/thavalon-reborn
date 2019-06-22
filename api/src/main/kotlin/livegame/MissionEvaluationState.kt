package main.kotlin.livegame

import com.google.gson.JsonObject
import main.kotlin.roles.Card

class MissionEvaluationState(override val g : LiveGame, private val cardsPlayed : List<PlayedCard>) : LiveGameState(g, setOf()) {
    /**
     * Neither sendRequests or onResponse is needed for this state
     */
    override suspend fun sendRequests() = Unit
    override suspend fun onResponse(res: JsonObject) = Unit

    /**
     * Returns true if mission passes, false otherwise
     */
    private fun missionPasses() : Boolean {
        val cards = cardsPlayed.map{ it.card }
        val numFails = cards.count { it == Card.F }
        val numReverses = cards.count { it == Card.R }
        // the mission reverses if there are an odd number of reverses
        val missionReverses = numReverses % 2 == 1

        // mission 4 needs 2 fails in 7 player or higher games
        val needsDoubleFail = g.players.size >= 7 && g.missionCount == 4
        // result is true if mission passes, false otherwise (WITHOUT REVERSES)
        val result = if(needsDoubleFail) {
            numFails < 2
        } else {
            numFails < 1
        }
        return if(missionReverses) !result else result
    }

    override suspend fun nextState() : LiveGameState {
        val result = missionPasses()
        return AgravaineState(g, cardsPlayed, result)
    }

}