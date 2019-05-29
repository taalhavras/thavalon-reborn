package main.kotlin.livegame

import com.google.gson.JsonObject
import main.MessageType
import java.util.concurrent.CountDownLatch

class MissionOneVotingResultState(override val g: LiveGame, val firstProposal : Mission, val secondProposal : Mission,
                                  val votes : Map<Mission, Set<String>>)
    : LiveGameState(g, setOf(MessageType.MISSION_ONE_VOTING_RESPONSE)) {

    // the mission that is actually going
    var chosenProposal : Mission

    init {
        cdl = CountDownLatch(g.players.size)
        val firstVotes = votes.getValue(firstProposal).size
        val secondVotes = votes.getValue(secondProposal).size
        // first proposal needs majority, ties to go second
        chosenProposal = if(firstVotes > secondVotes) {
            firstProposal
        } else {
            secondProposal
        }

    }

    override suspend fun sendRequests() {
        val msg = JsonObject()
        val sent = chosenProposal
        val notSent = if(chosenProposal == firstProposal) secondProposal else firstProposal
        msg.addProperty("sent", missionToJson(sent).toString())
        msg.addProperty("not_sent", missionToJson(notSent).toString())
        msg.addProperty("voted_sent", setToJson(votes.getValue(sent)).toString())
        msg.addProperty("voted_not_sent", setToJson(votes.getValue(notSent)).toString())
        g.sendToAll(msg)
    }

    override suspend fun onResponse(res: JsonObject) {
        // just count down if we get something valid
        // don't bother sending errors back for this one since
        // we'll just be getting blank messages
        if(validResponse(res)) {
            cdl.countDown()
        }
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()
        // once everyone has responded, we can go to the card playing phase
        // with the chosen mission
        return CardPlayingState(g, chosenProposal)
    }

}
