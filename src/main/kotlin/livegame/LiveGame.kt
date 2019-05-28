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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

typealias PlayerInfo = Pair<JsonObject, THavalonUserSession>

// Missions are just Sets of player names
typealias Mission = Set<String>


fun missionFromResponse(res : JsonObject) = res.get("proposal").asJsonArray.map { it.asString }.toSet()

abstract class LiveGameState(open val g : LiveGame, respondsTo : Set<MessageType>) {
    // message types we can respond to
    private val respondsTo : Set<String> = respondsTo.map { it.toString() }.toSet()
    // set of players who have already responded validly to our message. If
    // they have, they cannot respond again
    val alreadyResponded : MutableSet<String> = ConcurrentHashMap.newKeySet()

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
    open fun validResponse(res : JsonObject) : Boolean {

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
        else -> throw IllegalArgumentException("Bad game size in LiveGame (proposals per round)")
    }

    // represents the number of players on missions
    val proposalSizes = when(playerSessions.size) {
        5 -> listOf(2, 3, 2, 3, 3)
        7 -> listOf(2, 3, 3, 4, 4)
        8 -> listOf(3, 4, 3, 4, 5)
        10 -> listOf(4, 5, 5, 5, 5)
        else -> throw IllegalArgumentException("Bad game size in LiveGame (proposal sizes)")
    }

    // represents the current mission index (i.e. mission 1, 4, 5, etc.)
    var missionCount : Int = 0

    // represents the proposal number we are currently at in this mission. Starts at 1,
    // and increments as proposals are rejected. Resets at the start of a new mission
    var proposalCount = 1


    // represents the state in our state machine. We will only accept incoming messages of this type
    // game starts waiting for mission one proposal responses
    // var currentState : MessageType = MessageType.MISSION_ONE_PROPOSAL_RESPONSE
    private var currentState : LiveGameState = MissionOneState(this)

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

    fun incrementMissionCount() {
        missionCount ++
    }

    fun incrementProposalCount() {
        proposalCount ++
    }

    fun resetProposalCount() {
        proposalCount = 1
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

    suspend fun handleResponse(msg : JsonObject) {
        currentState.onResponse(msg)
    }

}