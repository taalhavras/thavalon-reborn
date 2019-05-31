package main.kotlin.livegame

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import main.MessageType
import main.kotlin.roles.Role

class MissionResultsState(override val g : LiveGame, val cards : List<PlayedCard>,
                          val result: Boolean, val agravaine : Role?) : LiveGameState(g, setOf()) {
    override suspend fun sendRequests() {
        val msg = JsonObject()
        msg.addProperty("type", MessageType.MISSION_RESULT.toString())
        val players = JsonArray()
        val cardsOnMission = JsonArray()
        for(c : PlayedCard in cards) {
            players.add(c.name)
            cardsOnMission.add(c.card.toString())
        }
        msg.addProperty("players", players.toString())
        msg.addProperty("cards", cardsOnMission.toString())
        msg.addProperty("result", result)
        // store missionCount
        msg.addProperty("num", g.missionCount)
        // increment missionCount
        g.incrementMissionCount()
        if(agravaine == null) {
            msg.addProperty("agravaine_declared", false)
        } else {
            msg.addProperty("agravaine_declared", true)
            msg.addProperty("agravaine", agravaine.player.name)
        }


        g.sendToAll(msg)
    }

    // Don't expect any responses here
    override suspend fun onResponse(res: JsonObject) = Unit

    override suspend fun nextState(): LiveGameState {
        val missionEvalRes = g.submitMissionResult(result)
        return when(missionEvalRes) {
            MissionEvaluationResult.CONTINUE_PLAY -> {
                // go to next player's proposal
                GameResultsState(g, GameResult.GOOD_WINS)
            }
            MissionEvaluationResult.EVIL_FAILS_THREE_MISSIONS -> {
                // go to game results
                GameResultsState(g, GameResult.EVIL_WINS_ON_MISSIONS)
            }
            MissionEvaluationResult.GOOD_PASSES_THREE_MISSIONS -> {
                // go to assassination
                AssassinationState(g)
            }
        }
    }

}