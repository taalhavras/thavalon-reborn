package main.kotlin.livegame

import com.google.gson.JsonObject
import main.MessageType
import java.util.concurrent.CountDownLatch

class MissionVotingResultState(override val g : LiveGame, val mission : Mission,
                               val votingRecord : Map<Boolean, Set<String>>, val force : Boolean)
    : LiveGameState(g, setOf(MessageType.MISSION_VOTING_RESPONSE)) {


    init {
        cdl = CountDownLatch(g.players.size)
    }

    override suspend fun sendRequests() {
        val msg = JsonObject()
        msg.addProperty("type", MessageType.MISSION_VOTING_RESULT.toString())
        msg.addProperty("force", force)
        msg.addProperty("num", g.missionCount)
        msg.addProperty("players", setToJson(mission.players).toString())
        msg.addProperty("proposed_by", mission.proposer)

        if(force) {
            msg.addProperty("sent", true)
        } else {
            val upvotes = votingRecord.getValue(true).size
            val downvotes = votingRecord.getValue(false).size
            msg.addProperty("sent", upvotes > downvotes)
        }

        g.sendToAll(msg)
    }

    override suspend fun onResponse(res: JsonObject) {
        if(validResponse(res)) {
            cdl.countDown()
        }
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()

        if(force) {
            // reset proposals in round count
            g.resetProposalCount()
            // go to card playing state
            return CardPlayingState(g, mission)
        }
        // no force, do normal voting calculations
        val upvotes = votingRecord.getValue(true).size
        val downvotes = votingRecord.getValue(false).size

        return if(upvotes > downvotes) {
            // mission goes
            // reset proposals in round counter
            g.resetProposalCount()
            // go to Card playing state
            CardPlayingState(g, mission)
        } else {
            // mission doesn't go
            // increment proposal counter
            g.incrementPlayerProposalIndex()
            // go back to mission state
            MissionState(g)
        }

    }

}