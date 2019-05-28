package main.kotlin.livegame

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import main.MessageType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

class MissionOneVotingState(override val g: LiveGame, val firstProposal : Mission, val secondProposal : Mission)
    : LiveGameState(g, setOf(MessageType.MISSION_ONE_VOTING_RESPONSE)) {

    // map the missions to who voted for them
    private val votingRecord = ConcurrentHashMap<Mission, Set<String>>()

    init {
        // set up empty sets to record votes
        votingRecord[firstProposal] = HashSet()
        votingRecord[secondProposal] = HashSet()

        // we need responses from every player in the game
        cdl = CountDownLatch(g.players.size)
    }

    override suspend fun sendRequests() {
        val msg = JsonObject()
        msg.addProperty("type", MessageType.MISSION_ONE_VOTING.toString())

        val firstJson = JsonArray()
        firstProposal.forEach { firstJson.add(it) }
        val secondJson = JsonArray()
        secondProposal.forEach { secondJson.add(it) }

        msg.addProperty("first_proposal", firstJson.toString())
        msg.addProperty("second_proposal", secondJson.toString())

        g.sendToAll(msg)
    }

    override suspend fun onResponse(res: JsonObject) {

    }

    override suspend fun nextState(): LiveGameState {

    }

}