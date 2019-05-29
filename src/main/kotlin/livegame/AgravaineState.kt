package main.kotlin.livegame

import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import main.MessageType
import main.kotlin.roles.RoleType
import java.util.concurrent.CountDownLatch

class AgravaineState(override val g : LiveGame, val cardsPlayed : List<PlayedCard>, var result : Boolean)
    : LiveGameState(g, setOf(MessageType.AGRAVAINE_RESPONSE)) {
    // whether the state is active or not
    var active : Boolean = false
    // whether agravaine decides to declare or not
    var declared : Boolean = false

    lateinit var agravaine : PlayerInfo

    init {
        // try to find an agravaine on the mission
        val playersOnMission = cardsPlayed.map { it.name } .toSet()
        val agr = g.players.find {
            it.first.role == RoleType.Agravaine && it.second.name in playersOnMission
        }

        // this state is only active if the mission passed and there is an agravaine on the mission
        // otherwise, we can just move on without sending anything out
        if(result && (agr != null)) {
            agravaine = agr
            active = true
            cdl = CountDownLatch(1)
        } else {
            cdl = CountDownLatch(0)
        }
    }

    override fun validResponse(res: JsonObject): Boolean {
        if(active) {
            val name = res.get("name").asString
            return name == agravaine.second.name && super.validResponse(res)
        }
        return false
    }

    override suspend fun sendRequests() {
        if(active) {
            val msg = JsonObject()
            msg.addProperty("type", MessageType.AGRAVAINE.toString())
            agravaine.second.socket!!.send(msg.toString())
        }
    }

    override suspend fun onResponse(res: JsonObject) {
        if(active && validResponse(res)) {
            val choice = res.get("choice").asBoolean
            if(choice) {
                // update declared status to true
                declared = true
                // mission result is now a fail
                result = false
            }
        } else {
            val errorResponse = blankErrorMessage()
            errorResponse.addProperty("error", "Invalid mission one proposal")
            agravaine.second.socket!!.send(errorResponse.toString())
        }
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()
        val agr = if(declared) agravaine.first else null
        return MissionResultsState(g, cardsPlayed, result, agr)
    }
}