package main.kotlin.livegame

import com.google.gson.JsonObject
import main.MessageType
import java.util.concurrent.CountDownLatch

class ReadyState(override val g: LiveGame) : LiveGameState(g, setOf(MessageType.READY)) {

    init {
        cdl = CountDownLatch(g.players.size)
    }

    override suspend fun sendRequests() = Unit

    override suspend fun onResponse(res: JsonObject) {
        if(validResponse(res)) {
            cdl.countDown()
        }
    }

    override suspend fun nextState(): LiveGameState {
        cdl.await()

        return MissionOneState(g)
    }

}