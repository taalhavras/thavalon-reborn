package main.kotlin.livegame

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import main.MessageType
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

class MissionOneVotingState(
    override val g: LiveGame, private val firstProposal: Mission,
    private val secondProposal: Mission
) : LiveGameState(g, setOf(MessageType.MISSION_ONE_VOTING_RESPONSE)) {

    // map the missions to who voted for them
    private val votingRecord = ConcurrentHashMap<Mission, MutableSet<String>>()

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
        firstProposal.players.forEach { firstJson.add(it) }
        val secondJson = JsonArray()
        secondProposal.players.forEach { secondJson.add(it) }

        msg.addProperty("first_proposal", firstJson.toString())
        msg.addProperty("second_proposal", secondJson.toString())

        g.sendToAll(msg)
    }

    override fun validResponse(res: JsonObject): Boolean {
        val vote = res.get("vote").asString
        return (vote == "upvote" || vote == "downvote") && super.validResponse(res)
    }

    override suspend fun onResponse(res: JsonObject) {
        val name = res.get("name").asString
        if (!validResponse(res)) {
            val errorResponse = blankErrorMessage()
            errorResponse.addProperty("error", "Invalid vote in mission one")
            g.sendMessage(errorResponse, name)
        } else {
            // record vote
            val vote = res.get("vote").asString
            when (vote) {
                "upvote" -> votingRecord[firstProposal]!!.add(name)
                "downvote" -> votingRecord[secondProposal]!!.add(name)
                else -> throw IllegalArgumentException("Bad vote in mission one onResponse")
            }

            // now that we've recorded the vote, decrement the cdl
            cdl.countDown()
        }
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()

        // now we want to transition into a MissionOneVotingResultsState in order
        // to report the results of this voting to everyone in the game
        return MissionOneVotingResultState(g, firstProposal, secondProposal, votingRecord)
    }

}