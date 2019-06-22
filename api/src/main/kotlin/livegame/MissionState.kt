package main.kotlin.livegame

import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import main.MessageType
import java.util.concurrent.CountDownLatch

class MissionState(override val g : LiveGame) : LiveGameState(g, setOf(MessageType.MISSION_PROPOSAL_RESPONSE)) {

    // the player with the proposal for this mission
    val proposingPlayer: PlayerInfo = g.players[g.playerProposalIndex]
    lateinit var proposal : Mission

    init {
        cdl = CountDownLatch(1)
    }

    override suspend fun sendRequests() {
        val msg = JsonObject()
        msg.addProperty("type", MessageType.MISSION_PROPOSAL.toString())
        proposingPlayer.second.socket!!.send(msg.toString())
    }

    override fun validResponse(res: JsonObject): Boolean {
        val name = res.get("name").asString
        return name == proposingPlayer.second.name && super.validResponse(res)
    }

    override suspend fun onResponse(res: JsonObject) {
        if(validResponse(res)) {
            val prop = res.get("proposal") .asJsonArray.map { it.asString }.toSet()
            proposal = Mission(prop, proposingPlayer.second.name)
            cdl.countDown()
        }
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()
        // now that we've gotten a response we move the playerProposalIndex
        // this happens regardless of the results of voting on the mission so
        // it's done here
        g.incrementPlayerProposalIndex()
        return MissionVotingState(g, proposal)
    }

}