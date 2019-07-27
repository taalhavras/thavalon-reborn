package main

import com.google.gson.*
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call;
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.http.*
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.cio.websocket.*
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.*
import io.ktor.util.generateNonce
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.mapNotNull
import kotlinx.coroutines.sync.Mutex
import main.kotlin.livegame.LiveGame
import main.kotlin.thavalon.*
import main.kotlin.roles.*
import java.io.File
import java.lang.IllegalArgumentException
import java.sql.DriverManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.collections.LinkedHashMap


enum class MessageType {
    ERROR,
    // client to server lobby
    CREATE_LOBBY,
    JOIN_LOBBY, REMOVE_PLAYER, LEAVE_LOBBY, DELETE_LOBBY, START_GAME,
    // server to client lobby
    NEW_PLAYER,
    PLAYER_REMOVED, SELF_REMOVED, LOBBY_DELETED, LOBBY_CREATED, LOBBY_JOINED, GAME_STARTED,
    // server to client livegame
    MISSION_ONE_PROPOSAL,
    MISSION_ONE_VOTING, MISSION_ONE_VOTING_RESULT, MISSION_PROPOSAL, MISSION_PROPOSAL_RESULT, MISSION_VOTING,
    MISSION_VOTING_RESULT, MISSION_RESULT, PLAY_CARD, HIJACK, AGRAVAINE, ASSASSINATE, GAME_RESULTS,
    // client to server livegame
    MISSION_ONE_PROPOSAL_RESPONSE,
    MISSION_ONE_VOTING_RESPONSE, MISSION_PROPOSAL_RESPONSE, MISSION_VOTING_RESPONSE,
    MISSION_PROPOSAL_RESULT_RESPONSE, PLAY_CARD_RESPONSE, HIJACK_RESPONSE, AGRAVAINE_RESPONSE, ASSASSINATE_RESPONSE,
    READY
}

val liveGameMessages : Set<MessageType> = setOf(
    MessageType.MISSION_ONE_PROPOSAL_RESPONSE, MessageType.MISSION_ONE_VOTING_RESPONSE, MessageType.MISSION_PROPOSAL_RESPONSE,
    MessageType.MISSION_VOTING_RESPONSE, MessageType.MISSION_PROPOSAL_RESULT_RESPONSE, MessageType.PLAY_CARD_RESPONSE,
    MessageType.HIJACK_RESPONSE, MessageType.AGRAVAINE_RESPONSE, MessageType.ASSASSINATE_RESPONSE, MessageType.READY
)


data class THavalonUserSession(val id: String, var name: String, var socket: DefaultWebSocketSession?)

// maps lobby ids to current lobbies
val lobbyMap: ConcurrentMap<String, Lobby> = ConcurrentHashMap()
val idLength = 4
val gson = Gson()

fun getID(): String {
    return UUID.randomUUID().toString().substring(0, idLength)
}

fun jsonifyGame(g: Game): JsonArray {
    val players = JsonArray()
    // construct json for player info
    // Iterate through roles in game in a random order. This is because the starting player is defined to
    // be the first player in the players array, so we want a random one.
    for (r: Role in g.rolesInGame) {
        // construct json for individual player
        val player = JsonObject()
        player.addProperty("name", r.player.name)
        player.addProperty("role", r.role.role.toString())
        player.addProperty("description", r.getDescription())
        player.addProperty("information", gson.toJson(r.prepareInformation()))
        player.addProperty("allegiance", r.role.alignment.toString())
        // add player to players json array
        players.add(player)
    }
    return players
}

/**
 * Given a list of names, and the custom configuration for games, rolls a new game. Produces
 * a responseJson containing the id of the new game, a JsonArray with all the game info, and a
 * boolean flag indicating whether or not the game was created using custom rules.
 */
fun rollGame(names: MutableList<String>, customInfo: JsonElement?, duplicates: JsonElement?)
        : Triple<JsonObject, Game?, Boolean> {
    val response = JsonObject()
    val gson = Gson()
    var isCustom = false
    val duplicatesAllowed = duplicates?.asBoolean ?: false
    val players = JsonArray()
    var g : Game? = null
    try {
        val id = getID()
        val rules: Ruleset = if (customInfo != null) {
            isCustom = true
            val roles: List<String> = customInfo.asJsonObject.entrySet()
                .filter { it.value.asBoolean } // get key value pairs that are requested to be present
                .map { it.key } // get names of requested roles
            println(roles)
            // construct custom ruleset
            makeCustomRuleset(roles, duplicatesAllowed)
        } else {
            when (names.size) {
                5 -> FivesRuleset()
                7 -> SevensRuleset()
                8 -> EightsRuleset()
                10 -> TensRuleset()
                else -> throw IllegalArgumentException(
                    "Invalid number of players! Only games of 5, 7, 8, " +
                            "and 10 players are currently supported"
                )
            }
        }
        g = rules.makeGame(names)
        response.addProperty("id", id)
    } catch (e: IllegalArgumentException) {
        // if we get an error creating the game, send message back to frontend
        response.addProperty("error", e.message)
    }
    return Triple(response, g, isCustom)
}

// members will include the owner, but it's useful to have access to just them
// to validate owner specific operations.
class Lobby(val owner: THavalonUserSession, val members: MutableList<THavalonUserSession>)

fun main() {
    //connects to mysql database
//    Class.forName("com.mysql.cj.jdbc.Driver");
//    val conn = DriverManager.getConnection(
//        "jdbc:mysql://thavalon.crzfhuz2u0ow.us-east-2.rds.amazonaws.com:3306/thavalon_reborn",
//        "thavalon_reborn",
//        "thavalon"
//    )

    val gson = Gson()
    // use concurrent map for safety when multiple games are being rolled at the same time
    // or when games are being cleared
//    val staticGames : ConcurrentMap<String, JsonArray> = java.util.concurrent.ConcurrentHashMap()
    // use LinkedHashMap to maintain order of insertions so we can easily get most recent games. Using
    // Collections.synchronizedMap to avoid issues with multiple threads updating the map at the same time
    val staticGames: MutableMap<String, Pair<JsonArray, Boolean>> = Collections.synchronizedMap(LinkedHashMap())

    // stores games being played via the app instead of in person
    val remoteGames: MutableMap<String, Pair<JsonArray, LiveGame>> = java.util.concurrent.ConcurrentHashMap()

    val statsMutex = Mutex()

    // for heroku ktor deployment
    val port: String = System.getenv("PORT") ?: "4444"


    val server = embeddedServer(Netty, port = port.toInt()) {
        install(ContentNegotiation) {
            gson {

            }
        }
        install(CORS) {
            method(HttpMethod.Options)
            header(HttpHeaders.XForwardedProto)
            anyHost()
            // host("my-host:80")
            // host("my-host", subDomains = listOf("www"))
            // host("my-host", schemes = listOf("http", "https"))
            allowCredentials = true
        }
        install(WebSockets)

        install(Sessions) {
            cookie<THavalonUserSession>("SESSION")
        }

        intercept(ApplicationCallPipeline.Features) {
            if (call.sessions.get<THavalonUserSession>() == null) {
                call.sessions.set(THavalonUserSession(generateNonce(), "", null))
            }
        }

        routing {
            static("static") {
                staticRootFolder = File("react/build/static")
                static("js") {
                    files("js")
                }

                static("css") {
                    files("css")
                }

                static("media") {
                    files("media")
                }
            }

            webSocket("/socket") {
                val session = call.sessions.get<THavalonUserSession>()!!

                println("New client connected")

                incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
                    val text = frame.readText()
                    val parsed = JsonParser().parse(text).asJsonObject
                    val type = MessageType.valueOf(parsed.get("type").asString)

                    when (type) {
                        MessageType.CREATE_LOBBY -> {
                            println("Create lobby received")
                            val id = getID()
                            session.name = parsed.get("name").asString
                            session.socket = this

                            lobbyMap[id] = Lobby(session, listOf(session).toMutableList())

                            val toSend: JsonObject = JsonObject()
                            toSend.addProperty("type", MessageType.LOBBY_CREATED.toString())
                            toSend.addProperty("name", session.name)
                            toSend.addProperty("id", id)
                            outgoing.send(Frame.Text(toSend.toString()))
                        }

                        MessageType.DELETE_LOBBY -> {
                            // lobby has been shut down by the host
                            val id = parsed.get("id").asString

                            // now send message to everyone in the lobby
                            val toAll = JsonObject()
                            toAll.addProperty("type", MessageType.SELF_REMOVED.toString())
                            val lobby = lobbyMap[id]!!
                            lobby.members.forEach { it.socket!!.send(toAll.toString()) }

                            // finally, remove the lobby from the map
                            lobbyMap.remove(id)
                        }

                        MessageType.JOIN_LOBBY -> {
                            val id = parsed.get("id").asString

                            val toSend: JsonObject = JsonObject()
                            val name = parsed.get("name").asString
                            session.name = name
                            session.socket = this
                            toSend.addProperty("type", MessageType.LOBBY_JOINED.toString())
                            if (id !in lobbyMap) {
                                toSend.addProperty("error", "Invalid Lobby ID")
                            } else if (lobbyMap[id]!!.members.any { it.name == name }) {
                                toSend.addProperty("error", "Player with that name in lobby")
                            } else {
                                val toAll = JsonObject()
                                toAll.addProperty("type", MessageType.NEW_PLAYER.toString())
                                toAll.addProperty("name", name)

                                // send new player name to all existing lobby members
                                lobbyMap[id]!!.members.forEach { it.socket!!.send(Frame.Text(toAll.toString())) }
                                // add new member's session
                                lobbyMap[id]!!.members.add(session)
                                // now send new member an array of all names in lobby, including themselves.
                                val names = JsonArray()
                                lobbyMap[id]!!.members.forEach { names.add(it.name) }
                                toSend.addProperty("names", gson.toJson(names))
                                toSend.addProperty("name", name)
                            }
                            println(toSend)
                            outgoing.send(Frame.Text(toSend.toString()))
                        }

                        MessageType.START_GAME -> {
                            // sent by lobby host to begin game
                            // get lobby id
                            val id = parsed.get("id").asString
                            // get custom info
                            val custom: JsonElement? = parsed.get("custom")
                            val duplicates = parsed.get("duplicates")
                            val lobby = lobbyMap[id]!!
                            // get names from lobby
                            val names: MutableList<String> = lobby.members.map { it.name }.toMutableList()
                            // attempt to roll game
                            val (response, game, isCustom) = rollGame(names, custom, duplicates)
                            if (response.has("error") || game == null) {
                                // something went wrong, alert lobby owner
                                response.addProperty("type", MessageType.ERROR.toString())
                                lobby.owner.socket!!.send(response.toString())
                            } else {
                                // game was successfully rolled, store info on rolled game
                                // in server map and then send redirects to all players

                                // note: Key by the id generated when the game is rolled, NOT the lobby id
                                // TODO consider changing this later but it's fine for now. There isn't a real
                                // benefit to the ids being the same except for this part maybe being clearer?
                                // but is it even?
                                val lg = LiveGame(game, lobby.members)
                                remoteGames.put(response.get("id").asString, Pair(jsonifyGame(lg.game), lg))

                                // now we can send redirects
                                response.addProperty("type", MessageType.GAME_STARTED.toString())
                                lobby.members.forEach { it.socket!!.send(response.toString()) }
                            }
                        }


                        MessageType.LEAVE_LOBBY -> println("Leave lobby received")
                        MessageType.REMOVE_PLAYER -> {
                            println("Remove player received")
                            val id = parsed.get("id").asString
                            val nameToRemove = parsed.get("name").asString
                            val lobby = lobbyMap[id]!!

                            // the lobby creator has already gotten rid of this member,
                            // but we need to tell everyone else. Furthermore, the evicted person needs
                            // to get a different message reflecting the fact that they've been kicked.
                            // they should be redirected to the homescreen

                            // players who need to be alerted about the removal
                            val toAlertAboutRemoval = lobby.members
                                .filter { it != lobby.owner && it.name != nameToRemove }
                            // this player has been removed
                            val removedPlayer = lobby.members.find { it.name == nameToRemove }!!

                            // delete the member from the lobby's members
                            lobby.members.remove(removedPlayer)

                            // TODO send all messages
                            // TODO Maybe abstract some of the message logic so it can be reused for when players leave?
                            // actually, can players even leave on their own?
                            val toAll = JsonObject()
                            toAll.addProperty("type", MessageType.PLAYER_REMOVED.toString())
                            toAll.addProperty("name", nameToRemove)
                            toAlertAboutRemoval.forEach { it.socket!!.send(toAll.toString()) }

                            val toRemoved = JsonObject()
                            toRemoved.addProperty("type", MessageType.SELF_REMOVED.toString())
                            removedPlayer.socket!!.send(toRemoved.toString())
                        }
                        in liveGameMessages -> {
                            val lg : LiveGame = remoteGames.getValue(parsed.get("id").asString).second
                            lg.handleResponse(parsed)
                        }
                        else -> println("Invalid message type $type")
                    }
                }
            }

            get("/") {
                call.respond("Thavalon API")
            }

            post("/names") {
                val post = call.receiveText()
                val parsed = JsonParser().parse(post).asJsonObject
                println(parsed)
                val names = parsed["names"].asJsonArray.map { it.asString }.toMutableList()
                val custom: JsonElement? = parsed["custom"]
                val duplicates = parsed["duplicates"]
                val (response : JsonObject, game : Game?, isCustom : Boolean) = rollGame(names, custom, duplicates)
                if (!response.has("error") && game != null) {
                    val players : JsonArray = jsonifyGame(game)
                    staticGames.put(response["id"].asString, Pair(players, isCustom))
                }

                call.respond(gson.toJson(response))
            }

            get("/game/info/{id}") {
                val id: String = call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                val response: JsonArray = when (id) {
                    in staticGames -> staticGames.getValue(id).first
                    in remoteGames -> remoteGames.getValue(id).first
                    else -> JsonArray()
                }

                call.respond(response.toString())
            }

            get("/{id}") {
                call.respondFile(File("react/build/index.html"))
            }

            get("/{id}/{player}") {
                call.respondFile(File("react/build/index.html"))
            }

            get("/submitresults") {
                call.respondFile(File("react/build/index.html"))
            }

            get("isGame/{id}") {
                val id: String = call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                call.respond(gson.toJson(staticGames.containsKey(id)))
            }

            post("/gameover/{id}") {
                // get id
                val id: String = call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                println("Ending game $id")
                // lock stats mutex
                statsMutex.lock()
                // now, we check to make sure the id hasn't already been deleted. If it has, we already recorded stats
                // for it so we can just unlock and finish
                val notDeleted = id in staticGames
                if (notDeleted) {
//                    val custom = staticGames[id]!!.second
//
//                    // get game result json
//                    val post = call.receiveText()
//                    val resultsJson = JsonParser().parse(post).asJsonObject
//                    // check if we want to record stats for the game
//                    val record = resultsJson["record"].asBoolean
//                    if (record) {
//                        val result = resultsJson["result"].toString()
//                        //prepares mysql statement
//                        val prep = conn.prepareStatement("INSERT INTO games VALUES (?, ?, ?)")
//                        //sets the mysql para
//                        prep.setString(1, id)
//                        prep.setString(2, result)
//                        prep.setBoolean(3, custom)
//                        prep.executeUpdate()
//                        prep.close()
//                        println(resultsJson)
//                        val playerStat = conn.prepareStatement("INSERT INTO players VALUES (?, ?, ?, ?)")
//
//                        for (e in staticGames[id]!!.first) {
//                            playerStat.setString(1, id)
//                            playerStat.setString(2, e.asJsonObject["name"].asString)
//                            playerStat.setString(3, e.asJsonObject["role"].asString)
//                            playerStat.setString(4, e.asJsonObject["allegiance"].asString)
//                            playerStat.executeUpdate()
//                        }
//
//                        playerStat.close()
//
//                    }

                    // delete id from games
                    staticGames.remove(id)
                } else {
                    println("Game $id already ended")
                }
                // unlock stats mutex
                statsMutex.unlock()
                // respond saying whether or not stats were recorded
                // true if id was not deleted before this call was processed, false otherwise
                call.respond(gson.toJson(notDeleted))
            }

            post("/currentgames") {
                val post = call.receiveText()
                val numGames: Int = JsonParser().parse(post).asJsonObject["numGames"].asInt
                // if there are fewer than numGames games in our map, this will take all of them
                // LinkedHashMap maintains an iteration ordering that's the same as map insertion order,
                // so since we want the most recent games we reverse the iteration order
                val recentGameIds: List<String> = staticGames.asIterable().reversed().take(numGames).map { it.key }
                call.respond(gson.toJson(recentGameIds))
            }
        }
    }
    server.start(wait = true)
}

