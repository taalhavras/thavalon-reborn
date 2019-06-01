package main.kotlin.livegame

import com.google.gson.JsonObject
import main.MessageType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

class MissionVotingState(override val g : LiveGame, val proposal : Mission) :
    LiveGameState(g, setOf(MessageType.MISSION_VOTING_RESPONSE)) {

    val force = g.proposalCount == g.proposalsPerRound

    // map votes to players. true corresponds to an upvote and false to a downvote
    private val votingRecord = ConcurrentHashMap<Boolean, MutableSet<String>>()

    init {
        if(force) {
            // force, just set cdl to 0 because we don't have to send any requests
            cdl = CountDownLatch(0)
        } else {
            cdl = CountDownLatch(g.players.size)
            votingRecord[true] = Collections.synchronizedSet(HashSet())
            votingRecord[false] = Collections.synchronizedSet(HashSet())
        }
    }

    override suspend fun sendRequests() {
        // don't send vote requests if force
        if(!force) {

        }
    }

    override suspend fun onResponse(res: JsonObject) {
        if(validResponse(res)) {

        }
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()
        return if(force) {
            // go to hijack state, but it isn't written yet
            MissionVotingResultState(g, proposal, votingRecord, force)
        } else {
            MissionVotingResultState(g, proposal, votingRecord, force)
        }

//        // go to hijack state if we reached force
//        return if(force) {
//            // TODO make a hijack state
//            MissionVotingResultState(g, proposal, HashMap(), true)
//        } else {
//            // now we should calculate if we've gotten enough votes
//            val upvotes = votingRecord[true]!!.size
//            val downvotes = votingRecord[false]!!.size
//            if(upvotes > downvotes) {
//                // mission went, needs clear majority
//                MissionVotingResultState(g, proposal, votingRecord, false)
//            } else {
//                // mission doesn't go
//                // increment the number of proposals
//                g.incrementProposalCount()
//                MissionState(g)
//            }
//        }
    }

}