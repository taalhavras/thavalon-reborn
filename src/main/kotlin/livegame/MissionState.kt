package main.kotlin.livegame

import com.google.gson.JsonObject
import main.MessageType

class MissionState(override val g : LiveGame) : LiveGameState(g, setOf(MessageType.MISSION_PROPOSAL_RESPONSE)) {
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