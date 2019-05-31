package main.kotlin.livegame

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import main.MessageType
import main.THavalonUserSession
import main.kotlin.roles.Role
import main.kotlin.thavalon.Game
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

typealias PlayerInfo = Pair<Role, THavalonUserSession>

// Missions are just Sets of player names
data class Mission(val players: Set<String>, val proposer: String)

fun blankErrorMessage(): JsonObject {
    val err = JsonObject()
    err.addProperty("type", MessageType.ERROR.toString())
    return err
}

fun missionFromResponse(res: JsonObject): Mission {
    val players: Set<String> = res.get("proposal").asJsonArray.map { it.asString }.toSet()
    println("misisonfromresponse")
    println(players)
    val name = res.get("name").asString
    return Mission(players, name)
}

fun setToJson(s: Set<String>): JsonArray {
    val arr = JsonArray()
    s.forEach { arr.add(it) }
    return arr
}

fun missionToJson(m: Mission): JsonArray {
    return setToJson(m.players)
}

/**
 * This class represents the outcomes for a game after a mission is evaluated. Either the game continues,
 * Evil wins by missions, or good has passed 3 missions and evil needs to move to assassination
 */
enum class MissionEvaluationResult {
    CONTINUE_PLAY,
    EVIL_FAILS_THREE_MISSIONS,
    GOOD_PASSES_THREE_MISSIONS,
}

/**
 * This class represents the possible results of a game
 */
enum class GameResult {
    EVIL_WINS_ON_MISSIONS,
    EVIL_WINS_BY_ASSASSINATION,
    GOOD_WINS
}

abstract class LiveGameState(open val g: LiveGame, respondsTo: Set<MessageType>) {
    // message types we can respond to
    private val respondsTo: Set<String> = respondsTo.map { it.toString() }.toSet()
    // set of players who have already responded validly to our message. If
    // they have, they cannot respond again
    val alreadyResponded: MutableSet<String> = ConcurrentHashMap.newKeySet()

    // used to synchronize the onResponse function calls so they don't
    // clobber each other. We should also synchronize using this before
    // sending requests because we might get responses before we are in
    // our call to nextState

    // will be initialized by sendRequests since that'll tell us
    // how many responses we will need
    lateinit var cdl: CountDownLatch

    abstract suspend fun sendRequests(): Unit

    open fun isTerminal() : Boolean {
        return false
    }

    /**
     * checks to see if a response is valid, which would mean we have to take action
     * this will be called from onResponse in subclasses.
     * NOTE: This function should be called last from any subclass because it adds the
     * player name to alreadyResponded
     * This needs to be synchronized because if a message is deemed valid it affects the validity of other
     * valid messages (we are only accepting the first valid message per player per state)
     */
    @Synchronized
    open fun validResponse(res: JsonObject): Boolean {
        println("valid response:\n $res")
        val name = res.get("name").asString
        val ret = res.get("type").asString !in respondsTo || name !in alreadyResponded
        if (ret) {
            alreadyResponded.add(name)
        }
        println(ret)
        return ret
    }

    abstract suspend fun onResponse(res: JsonObject): Unit

    abstract suspend fun nextState(): LiveGameState

    suspend fun advance(): Unit {
        sendRequests()
        if (!isTerminal()) {

            val state = nextState()
            g.setState(state)
            state.advance()

        }
    }
}

/**
 * This class represents a remote live game running on the server.
 */
class LiveGame(val game: Game, playerSessions: List<THavalonUserSession>) {

    val players: MutableList<PlayerInfo> = ArrayList()
    // the number of proposals per round, will be used in hijack logic
    val proposalsPerRound = when (playerSessions.size) {
        5 -> 3
        7 -> 3
        8 -> 4
        10 -> 4
        else -> throw IllegalArgumentException("Bad game size in LiveGame (proposals per round)")
    }

    // represents the number of players on missions
    val proposalSizes = when (playerSessions.size) {
        5 -> listOf(2, 3, 2, 3, 3)
        7 -> listOf(2, 3, 3, 4, 4)
        8 -> listOf(3, 4, 3, 4, 5)
        10 -> listOf(4, 5, 5, 5, 5)
        else -> throw IllegalArgumentException("Bad game size in LiveGame (proposal sizes)")
    }

    // represents index of player with next proposal
    // starts off as the first player
    var playerProposalIndex = 0

    // represents the current mission (i.e. mission 1, 4, 5, etc.)
    var missionCount: Int = 1

    // represents the proposal number we are currently at in this mission. Starts at 1,
    // and increments as proposals are rejected. Resets at the start of a new mission
    var proposalCount = 1

    // represents the results of missions in our game so far
    val missionResults : ArrayList<Boolean> = ArrayList()

    // represents the state in our state machine. We will only accept incoming messages of this type
    // game starts waiting for mission one proposal responses
    // var currentState : MessageType = MessageType.MISSION_ONE_PROPOSAL_RESPONSE
    var currentState: LiveGameState

    init {
        assert(game.rolesInGame.size == playerSessions.size)
        println(game.rolesInGame)
        println(playerSessions)
        for (r: Role in game.rolesInGame) {
            for (session: THavalonUserSession in playerSessions) {
                if (r.player.name == session.name) {
                    players.add(Pair(r, session))
                }
            }
        }
        assert(playerSessions.size == players.size)
        // once players is set up, we can initialize this state
        currentState = ReadyState(this)
        // start the first state
        GlobalScope.launch {
            currentState.advance()
        }
    }

    fun incrementPlayerProposalIndex() {
        playerProposalIndex ++
    }

    fun incrementMissionCount() {
        missionCount++
    }

    fun incrementProposalCount() {
        proposalCount++
    }

    fun resetProposalCount() {
        proposalCount = 1
    }

    /**
     * Takes in true if a mission passed, false if it fails. Returns the state of
     * the game after this result, which is used to determine which state to transition to
     */
    fun submitMissionResult(result : Boolean) : MissionEvaluationResult {
        missionResults.add(result)
        val numPassed = missionResults.count { it }
        val numFailed = missionResults.count { !it }
        return when (numFailed) {
            3 -> MissionEvaluationResult.EVIL_FAILS_THREE_MISSIONS
            else -> if(numPassed == 3) {
                MissionEvaluationResult.GOOD_PASSES_THREE_MISSIONS
            } else {
                MissionEvaluationResult.CONTINUE_PLAY
            }
        }
    }

    suspend fun sendMessage(msg: JsonObject, name: String) {
        val info = players.find { it.second.name == name }!!
        info.second.socket!!.send(msg.toString())
    }

    suspend fun sendToAll(msg: JsonObject) {
        players.map { it.second.socket!!.send(msg.toString()) }
    }

    fun setState(state: LiveGameState) {
        currentState = state
    }

    suspend fun handleResponse(msg: JsonObject) {
        currentState.onResponse(msg)
    }

}