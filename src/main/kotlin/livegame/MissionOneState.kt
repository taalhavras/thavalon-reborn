package main.kotlin.livegame

import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import main.MessageType
import java.util.concurrent.CountDownLatch

class MissionOneState(override val g : LiveGame) : LiveGameState(g, setOf(MessageType.MISSION_ONE_PROPOSAL_RESPONSE)) {

    private val firstProposingPlayer : PlayerInfo
    private val secondProposingPlayer : PlayerInfo
    lateinit var firstPlayerProposal : Mission
    lateinit var secondPlayerProposal : Mission

    init {
         // get first and second proposing players
        val players = g.players
        firstProposingPlayer = players[players.size - 2]
        secondProposingPlayer = players[players.size - 1]
        // init cdl before messages are sent
        cdl = CountDownLatch(2)
    }

    override suspend fun sendRequests() {
        // send mission 1 proposal message to the two selected players
        val msg = JsonObject()
        msg.addProperty("type", MessageType.MISSION_ONE_PROPOSAL.toString())
        g.sendMessage(msg, firstProposingPlayer.second.name)
        g.sendMessage(msg, secondProposingPlayer.second.name)
    }

    /**
     * This needs to be synchronized because if a message is deemed valid it affects the validity of other
     * valid messages (we are only accepting the first valid message per player per state)
     */
    @Synchronized override fun validResponse(res: JsonObject): Boolean {
        // superclass validResponse ensures only 1 response per player and that
        // message is of correct type. Now, we just have to ensure that the message
        // came from a player with a proposal and that it's the correct size
        val name = res.get("name").asString

        val proposal = missionFromResponse(res)

        val ret = super.validResponse(res)
                // message is from a valid player
                && (name == firstProposingPlayer.second.name || name == secondProposingPlayer.second.name)
                // message contains the correct number of players
                && proposal.size == g.proposalSizes[g.missionCount]


        // we have a valid message, add the player to the seen set
        if(ret) {
            alreadyResponded.add(name)
        }

        return ret
    }

    override suspend fun onResponse(res : JsonObject) {
        val (info, session) = g.players.find { it.second.name == res.get("name").asString }!!
        if(!validResponse(res)) {
            val errorResponse = JsonObject()
            errorResponse.addProperty("error", "Invalid mission one proposal")
            session.socket!!.send(errorResponse.toString())
        } else {
            // we have a valid response, store it
            val m : Mission = missionFromResponse(res)
            val name = res.get("name").asString
            if(name == firstProposingPlayer.second.name) {
                firstPlayerProposal = m
            } else {
                secondPlayerProposal = m
            }

            // now decrement latch, possibly releasing nextState
            cdl.countDown()
        }
    }

    override suspend fun nextState(): LiveGameState {
        // wait until thread is done
        cdl.await()
        // now we have received two mission proposals, we want to transition into a MissionOneVotingState
        return MissionOneVotingState(g, firstPlayerProposal, secondPlayerProposal)
    }

}