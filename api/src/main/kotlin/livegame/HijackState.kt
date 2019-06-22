package main.kotlin.livegame

import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import main.MessageType
import main.kotlin.thavalon.HijackInfo
import java.util.concurrent.CountDownLatch

data class HijackInfo(val hijacker : String, val removed : String)

class HijackState(override val g : LiveGame, var mission : Mission) : LiveGameState(g, setOf(MessageType.HIJACK_RESPONSE)) {
    var active : Boolean = determineActive()
    lateinit var hijackPlayer : PlayerInfo
    lateinit var hijackInfo : HijackInfo

    init {
        cdl = if(active) {
            CountDownLatch(1)
        } else {
            CountDownLatch(0)
        }
    }

    /**
     * Determines if the state is active or not
     * Active means that there is a player in the game with hijack who is NOT ALREADY ON THE MISSION
     */
    private fun determineActive() : Boolean {
        // we must determine if hijack is actually present in the game
        hijackPlayer = g.players.find { it.first.information.alerts.contains(HijackInfo) } ?: return false
        // if it is, make sure the player with hijack isn't already on the mission
        return hijackPlayer.second.name !in mission.players
    }

    override suspend fun sendRequests() {
        if(active) {
            val msg = JsonObject()
            msg.addProperty("type", MessageType.HIJACK.toString())
            msg.addProperty("proposal", setToJson(mission.players).toString())

            hijackPlayer.second.socket!!.send(msg.toString())
        }
    }

    override suspend fun onResponse(res: JsonObject) {
        if(active && validResponse(res)) {
            val choice = res.get("choice").asBoolean
            if(choice) {
                val toRemove = res.get("player").asString
                assert(toRemove in mission.players)
                val newPlayers = mission.players.minus(toRemove).plus(hijackPlayer.second.name)
                mission = Mission(newPlayers, mission.proposer)
            }

            cdl.countDown()
        }
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()
        // we can pass in a blank votingRecord here since we know it won't be used because we got here
        // via force and thus did not have any voting
        return MissionVotingResultState(g, mission, HashMap(), true, hijackInfo)
    }

}