package main

import com.google.gson.*
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call;
import io.ktor.application.install
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
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import io.netty.handler.codec.MessageToByteEncoder
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.mapNotNull
import kotlinx.coroutines.sync.Mutex
import main.kotlin.thavalon.*
import main.kotlin.roles.*
import java.io.File
import java.lang.IllegalArgumentException
import java.sql.DriverManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


enum class MessageType {
    // client to server
    CREATE_LOBBY, JOIN_LOBBY, REMOVE_PLAYER, LEAVE_LOBBY, DELETE_LOBBY,
    // server to client
    NEW_PLAYER, PLAYER_REMOVED, LOBBY_DELETED, LOBBY_CREATED, LOBBY_JOINED
}


data class THavalonUserSession(val id: String, var name: String, var socket: DefaultWebSocketSession?)

val sessionMap: ConcurrentMap<String, Lobby> = ConcurrentHashMap()
val idLength = 4

fun getID() : String {
   return UUID.randomUUID().toString().substring(0, idLength)
}

// members will include the owner, but it's useful to have access to just them
// to validate owner specific operations.
class Lobby(val owner: THavalonUserSession, val members: MutableList<THavalonUserSession>)

fun main() {
    //connects to mysql database
    Class.forName("com.mysql.cj.jdbc.Driver");
    val conn = DriverManager.getConnection(
        "jdbc:mysql://thavalon.crzfhuz2u0ow.us-east-2.rds.amazonaws.com:3306/thavalon_reborn",
        "thavalon_reborn",
        "thavalon"
    )

    val gson = Gson()
    // use concurrent map for safety when multiple games are being rolled at the same time
    // or when games are being cleared
//    val games : ConcurrentMap<String, JsonArray> = java.util.concurrent.ConcurrentHashMap()
    // use LinkedHashMap to maintain order of insertions so we can easily get most recent games. Using
    // Collections.synchronizedMap to avoid issues with multiple threads updating the map at the same time
    val games: MutableMap<String, Pair<JsonArray, Boolean>> = Collections.synchronizedMap(LinkedHashMap())

    val statsMutex = Mutex()

    // for heroku ktor deployment
    val port: String = System.getenv("PORT") ?: "4444"




    val server = embeddedServer(Netty, port = port.toInt()) {
        install(ContentNegotiation) {
            gson {

            }
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
//                            session.socket = this

                            sessionMap[id] = Lobby(session, listOf(session).toMutableList())

                            val toSend: JsonObject = JsonObject()
                            toSend.addProperty("type", MessageType.LOBBY_CREATED.toString())
                            toSend.addProperty("name", session.name)
                            toSend.addProperty("id", id)
                            outgoing.send(Frame.Text(toSend.toString()))
                        }
                        MessageType.JOIN_LOBBY -> {
                            val id = parsed.get("id").asString

                            val toSend: JsonObject = JsonObject()
                            val name  = parsed.get("name").asString
                            session.name = name
                            toSend.addProperty("type", MessageType.LOBBY_JOINED.toString())
                            if (id !in sessionMap) {
                                toSend.addProperty("error", "Invalid Lobby ID")
                            } else if (sessionMap[id]!!.members.any { it.name == name }) {
                                toSend.addProperty("error", "Player with that name in lobby")
                            } else {
                                val toAll = JsonObject()
                                toAll.addProperty("type", MessageType.NEW_PLAYER.toString())
                                toAll.addProperty("name", name)

                                // send new player name to all existing lobby members
//                                sessionMap[id]!!.members.forEach { it.socket!!.send(Frame.Text(toAll.toString()))}
                                // add new member's session
                                sessionMap[id]!!.members.add(session)
                                // now send new member an array of all names in lobby, including themselves.
                                val names = JsonArray()
                                sessionMap[id]!!.members.forEach { names.add(it.name) }
                                toSend.addProperty("names", names.toString())

                            }
                            println(toSend)
                            outgoing.send(Frame.Text(toSend.toString()))
                        }


                        MessageType.LEAVE_LOBBY -> println("Leave lobby received")
                        MessageType.DELETE_LOBBY -> println("Delete lobby received")
                        MessageType.REMOVE_PLAYER -> println("Remove player recieved")
                        else -> println("Invalid message type")
                    }
                }
            }

            get("/") {
                call.respondFile(File("react/build/index.html"))
            }

            post("/names") {
                val response = JsonObject()
                var isCustom = false;
                try {
                    val post = call.receiveText()
                    val parsed = JsonParser().parse(post).asJsonObject
                    val names = parsed["names"].asJsonArray.map { it.asString }.toMutableList()
                    val custom: JsonElement? = parsed["custom"]
                    val id = getID()
                    val rules: Ruleset = if (custom != null) {
                        isCustom = true
                        val roles: List<String> = custom.asJsonObject.entrySet()
                            .filter { it.value.asBoolean } // get key value pairs that are requested to be present
                            .map { it.key } // get names of requested roles
                        println(roles)
                        // figure out if duplicates are allowed
                        val duplicatesAllowed = parsed["duplicates"].asBoolean
                        // construct custom ruleset
                        makeCustomRuleset(roles, duplicatesAllowed)
                    } else {
                        when (names.size) {
                            5 -> FivesRuleset()
                            7 -> SevensRuleset()
                            8 -> EightsRuleset()
                            10 -> TensRuleset()
                            else -> throw IllegalArgumentException("BAD NAMES: $names")
                        }
                    }
                    val g: Game = rules.makeGame(names)

                    // construct json for player info
                    val players = JsonArray()
                    // Iterate through roles in game in a random order. This is because the starting player is defined to
                    // be the first player in the players array, so we want a random one.
                    for (r: Role in g.rolesInGame.shuffled()) {
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
                    println(g)
                    // put player info into map with id we generated
                    games.put(id, Pair(players, isCustom))
                    response.addProperty("id", id)
                } catch (e: IllegalArgumentException) {
                    // if we get an error creating the game, send message back to frontend
                    response.addProperty("error", e.message)
                }
                call.respond(gson.toJson(response))
            }

            get("/game/info/{id}") {
                val id: String = call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                val info: JsonArray? = games.get(id)?.first
                // if we can't find the game id, just redirect to homepage
                if (info == null) {
                    // send empty array
                    call.respond(JsonArray())
                } else {
                    call.respond(info)
                }
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
                call.respond(gson.toJson(games.containsKey(id)))
            }

            post("/gameover/{id}") {
                // get id
                val id: String = call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                println("Ending game $id")
                // lock stats mutex
                statsMutex.lock()
                // now, we check to make sure the id hasn't already been deleted. If it has, we already recorded stats
                // for it so we can just unlock and finish
                val notDeleted = id in games
                if (notDeleted) {
                    val custom = games[id]!!.second

                    // get game result json
                    val post = call.receiveText()
                    val resultsJson = JsonParser().parse(post).asJsonObject
                    // check if we want to record stats for the game
                    val record = resultsJson["record"].asBoolean
                    if (record) {
                        val result = resultsJson["result"].toString()
                        //prepares mysql statement
                        val prep = conn.prepareStatement("INSERT INTO games VALUES (?, ?, ?)")
                        //sets the mysql para
                        prep.setString(1, id)
                        prep.setString(2, result)
                        prep.setBoolean(3, custom)
                        prep.executeUpdate()
                        prep.close()
                        println(resultsJson)
                        val playerStat = conn.prepareStatement("INSERT INTO players VALUES (?, ?, ?, ?)")

                        for (e in games[id]!!.first) {
                            playerStat.setString(1, id)
                            playerStat.setString(2, e.asJsonObject["name"].asString)
                            playerStat.setString(3, e.asJsonObject["role"].asString)
                            playerStat.setString(4, e.asJsonObject["allegiance"].asString)
                            playerStat.executeUpdate()
                        }

                        playerStat.close()

                    }

                    // delete id from games
                    games.remove(id)
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
                val recentGameIds: List<String> = games.asIterable().reversed().take(numGames).map { it.key }
                call.respond(gson.toJson(recentGameIds))
            }
        }
    }
    server.start(wait = true)
}

