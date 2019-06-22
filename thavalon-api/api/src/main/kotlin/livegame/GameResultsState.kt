package main.kotlin.livegame

import com.google.gson.JsonObject
import main.MessageType
import java.lang.UnsupportedOperationException

class GameResultsState(override val g : LiveGame, val gameResult : GameResult) : LiveGameState(g, setOf()) {
    override fun isTerminal(): Boolean {
        return true
    }

    override suspend fun sendRequests() {
        val msg = JsonObject()
        msg.addProperty("type", MessageType.GAME_RESULTS.toString())
        msg.addProperty("result", gameResult.toString())
        g.sendToAll(msg)
    }

    override suspend fun onResponse(res: JsonObject) = Unit

    // this should never be called since the state is terminal
    override suspend fun nextState(): LiveGameState {
        throw UnsupportedOperationException("Calling next state in GameResultsState")
    }


}
