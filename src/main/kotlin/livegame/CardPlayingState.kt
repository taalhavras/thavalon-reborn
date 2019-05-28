package main.kotlin.livegame

import com.google.gson.JsonObject
import main.MessageType

class CardPlayingState(override val g: LiveGame, val m : Mission) : LiveGameState(g, setOf(MessageType.PLAY_CARD_RESPONSE)) {
    override suspend fun sendRequests() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun onResponse(res: JsonObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun nextState(): LiveGameState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}