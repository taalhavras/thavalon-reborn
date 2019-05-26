package main.kotlin.livegame

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import main.MessageType
import main.THavalonUserSession
import main.kotlin.thavalon.Game
import java.lang.IllegalArgumentException

typealias PlayerInfo = Pair<JsonObject, THavalonUserSession>

// Missions are just Sets of player names
typealias Mission = Set<String>

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
    var currentState : MessageType =

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
    }

    suspend fun sendMessage(msg : JsonObject, name : String) {
        val info = players.find { it.second.name == name }!!
        info.second.socket!!.send(msg.toString())
    }

    suspend fun sendToAll(msg : JsonObject) {
        players.map { it.second.socket!!.send(msg.toString()) }
    }

}