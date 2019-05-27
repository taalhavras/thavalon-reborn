package main.kotlin.livegame

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import main.MessageType
import main.THavalonUserSession
import main.kotlin.thavalon.Game
import java.lang.IllegalArgumentException
import java.util.concurrent.CountDownLatch

typealias PlayerInfo = Pair<JsonObject, THavalonUserSession>

// Missions are just Sets of player names
typealias Mission = Set<String>

abstract class LiveGameState(open val g : LiveGame, respondsTo : Set<MessageType>) {
    // message types we can respond to
    private val respondsTo : Set<String> = respondsTo.map { it.toString() }.toSet()
    // set of players who have already responded validly to our message. If
    // they have, they cannot respond again
    private val alreadyResponded : MutableSet<String> = HashSet()

    // used to synchronize the onResponse function calls so they don't
    // clobber each other. We should also synchronize using this before
    // sending requests because we might get responses before we are in
    // our call to nextState

    // will be initialized by sendRequests since that'll tell us
    // how many responses we will need
    lateinit var cdl : CountDownLatch

    abstract suspend fun sendRequests() : Unit

    // checks to see if a response is valid, which would mean we have to take action
    // this will be called from onResponse
    fun validResponse(res : JsonObject) : Boolean {
        return res.get("type").asString !in respondsTo || res.get("player").asString !in alreadyResponded
    }

    abstract suspend fun onResponse(res : JsonObject) : Unit

    abstract suspend fun nextState() : LiveGameState

    suspend fun advance() : Unit {
        sendRequests()
        val state = nextState()
        g.setState(state)
        state.advance()
    }
}

class MissionOneState(override val g : LiveGame) : LiveGameState(g, setOf(MessageType.MISSION_ONE_PROPOSAL_RESPONSE)) {
    override suspend fun sendRequests() {
        // get first and second proposing players
        val players = g.players
        val firstProposingPlayer = players[players.size - 2]
        val secondProposingPlayer = players[players.size - 1]
        // send mission 1 proposal message to the two selected players
        val msg = JsonObject()
        msg.addProperty("type", MessageType.MISSION_ONE_PROPOSAL.toString())
        // init cdl
        cdl = CountDownLatch(2)
        firstProposingPlayer.second.socket!!.send(msg.toString())
        secondProposingPlayer.second.socket!!.send(msg.toString())
    }

    override suspend fun onResponse(res : JsonObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun nextState(): LiveGameState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * This class represents a remote live game running on the server.
 */
class LiveGame(val game : Game, jsonifiedGame : JsonArray, playerSessions : List<THavalonUserSession>) {

    val players : MutableList<PlayerInfo> = ArrayList()
    // the number of proposals per round, will be used in hijack logic
    val proposalsPerRound = when(playerSessions.size) {
        5 -> 3
        7 -> 3
        8 -> 4
        10 -> 4
        else -> throw IllegalArgumentException("Bad game size in LiveGame")
    }

    // represents the state in our state machine. We will only accept incoming messages of this type
    // game starts waiting for mission one proposal responses
    // var currentState : MessageType = MessageType.MISSION_ONE_PROPOSAL_RESPONSE
    var currentState : LiveGameState = MissionOneState(this)

    init {
        assert(jsonifiedGame.size() == playerSessions.size)
        for (player : JsonElement in jsonifiedGame) {
            for (session : THavalonUserSession in playerSessions) {
                if(player.asJsonObject.get("name").asString == session.name) {
                    players.add(Pair(player.asJsonObject, session))
                }
            }
            throw IllegalArgumentException("Live game player without session")
        }


        // start the first state
        GlobalScope.launch {
            currentState.advance()
        }
    }

    suspend fun sendMessage(msg : JsonObject, name : String) {
        val info = players.find { it.second.name == name }!!
        info.second.socket!!.send(msg.toString())
    }

    suspend fun sendToAll(msg : JsonObject) {
        players.map { it.second.socket!!.send(msg.toString()) }
    }

    suspend fun setState(state : LiveGameState) {
        currentState = state
        state.advance()
    }

}