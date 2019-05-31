package main.kotlin.livegame

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.ktor.http.cio.websocket.send
import main.MessageType
import main.kotlin.roles.Card
import main.kotlin.roles.Role
import java.util.concurrent.CountDownLatch

data class PlayedCard(val name : String, val card: Card)

class CardPlayingState(override val g: LiveGame, val m : Mission) : LiveGameState(g, setOf(MessageType.PLAY_CARD_RESPONSE)) {

    private val cardsPlayedOnMission : MutableList<PlayedCard> = ArrayList()

    init {
        // expect a response from each player on the mission
        cdl = CountDownLatch(m.players.size)
    }

    override suspend fun sendRequests() {
        val onMission = g.players.filter { it.second.name in m.players }
        onMission.forEach {
            val msg = JsonObject()
            // get all cards the role can play
            val cards = JsonArray()
            it.first.cardOptions().forEach { c: Card ->
                cards.add(c.toString())
            }
            msg.addProperty("type", MessageType.PLAY_CARD.toString())
            msg.addProperty("cards", cards.toString())
            it.second.socket!!.send(msg.toString())
        }
    }

    override fun validResponse(res: JsonObject): Boolean {
        // we want to make sure the given role can play the card they've chosen
        val name = res.get("name").asString
        val role : Role = g.players.find { it.second.name == name }!!.first
        val playableCards : List<String> = role.cardOptions().map { it.toString() }
        val card = res.get("card").asString
        return card in playableCards && super.validResponse(res)
    }

    override suspend fun onResponse(res: JsonObject) {
        val name = res.get("name").asString
        if(!validResponse(res)) {
            val errorResponse = blankErrorMessage()
            errorResponse.addProperty("error", "Invalid card played")
            g.sendMessage(errorResponse, name)
        } else {
            // valid response, record the card
            val c : Card = Card.valueOf(res.get("card").asString)
            synchronized(cardsPlayedOnMission) {
                cardsPlayedOnMission.add(PlayedCard(name, c))
            }
            cdl.countDown()
        }
    }

    override suspend fun nextState() : LiveGameState {
        cdl.await()

        return MissionEvaluationState(g, cardsPlayedOnMission)
    }

}