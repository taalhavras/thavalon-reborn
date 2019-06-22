package main.kotlin.livegame

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import main.MessageType
import main.kotlin.roles.Alignment
import main.kotlin.roles.RoleType
import java.lang.IllegalArgumentException
import java.util.concurrent.CountDownLatch

class AssassinationState(override val g : LiveGame) : LiveGameState(g, setOf(MessageType.ASSASSINATE_RESPONSE)) {
    var rankingEvil : PlayerInfo = g.players
            // get evil players
        .filter { it.first.role.alignment == Alignment.Evil }
            // the highest ranking evil is the one with the lowest role enum
            // based on how we declared them
        .minBy { it.first.role.role }!!

    lateinit var assassinationResponse : JsonObject

    init {
        cdl = CountDownLatch(1)
    }

    override fun validResponse(res: JsonObject): Boolean {
        val name = res.get("name").asString
        return name == rankingEvil.second.name && super.validResponse(res)
    }

    override suspend fun sendRequests() {
        val msg = JsonObject()
        msg.addProperty("type", MessageType.ASSASSINATE.toString())
        val goodPlayers = JsonArray()
        g.game.getGoodRoles().forEach { goodPlayers.add(it.player.name) }
        msg.addProperty("targets", goodPlayers.toString())
        rankingEvil.second.socket!!.send(msg.toString())
    }

    override suspend fun onResponse(res: JsonObject) {

        if(!validResponse(res)) {
            val errorResponse = JsonObject()
            errorResponse.addProperty("error", "Invalid assassination")
            val name = res.get("name").asString
            g.sendMessage(errorResponse, name)
        } else {
            assassinationResponse = res
            cdl.countDown()
        }
    }

    private fun evaluateAssassination() : GameResult {
        val assassinationType = assassinationResponse.get("assassination_type").asString
        val targets = assassinationResponse.get("targets").asJsonArray.map { it.asString }

        val merlins = g.game.getGoodRoles()
            .filter { it.role == RoleType.Merlin }
            .map { it.player.name }

        val tristans = g.game.getGoodRoles()
            .filter { it.role == RoleType.Tristan }
            .map { it.player.name }

        val iseults = g.game.getGoodRoles()
            .filter { it.role == RoleType.Iseult }
            .map { it.player.name }

        when(assassinationType) {
            "lovers" -> {
                val firstLover = targets[0]
                val secondLover = targets[1]
                if((firstLover in tristans && secondLover in iseults) ||
                    (secondLover in tristans && firstLover in iseults)
                ) {
                    return GameResult.EVIL_WINS_BY_ASSASSINATION
                }
            }
            "merlin" -> {
                val merlin = targets[0]
                if(merlin in merlins) {
                    return GameResult.EVIL_WINS_BY_ASSASSINATION
                }
            }
            "no_targets" -> {
                if(merlins.isEmpty() && (tristans.isEmpty() || iseults.isEmpty())) {
                    return GameResult.EVIL_WINS_BY_ASSASSINATION
                }
            }
            else -> throw IllegalArgumentException("Bad assassination type")
        }
        // evil missed assassination
        return GameResult.GOOD_WINS
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()
        return GameResultsState(g, evaluateAssassination())
    }

}