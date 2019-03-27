package main
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.sync.Mutex
import main.kotlin.thavalon.*
import main.kotlin.roles.*
import java.io.File
import java.lang.IllegalArgumentException
import java.sql.DriverManager
import java.util.*
import java.util.concurrent.ConcurrentMap
import kotlin.collections.LinkedHashMap

fun main() {
    Class.forName("com.mysql.cj.jdbc.Driver");
    val conn = DriverManager.getConnection(
        "jdbc:mysql://thavalon.crzfhuz2u0ow.us-east-2.rds.amazonaws.com:3306/thavalon_reborn","thavalon_reborn","thavalon")

    val gson = Gson()
    // use concurrent map for safety when multiple games are being rolled at the same time
    // or when games are being cleared
//    val games : ConcurrentMap<String, JsonArray> = java.util.concurrent.ConcurrentHashMap()
    // use LinkedHashMap to maintain order of insertions so we can easily get most recent games. Using
    // Collections.synchronizedMap to avoid issues with multiple threads updating the map at the same time
    val games : MutableMap<String, JsonArray> = Collections.synchronizedMap(LinkedHashMap())

    val statsMutex = Mutex()

    // for heroku ktor deployment
    val port : String = System.getenv("PORT") ?: "4444"

    val idLength = 6

    val server = embeddedServer(Netty, port = port.toInt()) {
        install(ContentNegotiation) {
            gson {

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

            get("/") {
                call.respondFile(File("react/build/index.html"))
            }

            post("/names") {
                val post = call.receiveText()
                val parsed = JsonParser().parse(post).asJsonObject
                val names = parsed["names"].asJsonArray.map { it.asString }.toMutableList()
                val id = UUID.randomUUID().toString().substring(0, idLength)
                val g : Game = when(names.size) {
                    5 -> FivesRuleset().makeGame(names)
                    7 -> SevensRuleset().makeGame(names)
                    8 -> EightsRuleset().makeGame(names)
                    10 -> TensRuleset().makeGame(names)
                    else -> throw IllegalArgumentException("BAD NAMES: $names")
                }

                // construct json for player info
                val players = JsonArray()
                // Iterate through roles in game in a random order. This is because the starting player is defined to
                // be the first player in the players array, so we want a random one.
                for (r : Role in g.rolesInGame.shuffled()) {
                    // construct json for individual player
                    val player = JsonObject()
                    player.addProperty("name", r.player.name)
                    player.addProperty("role", r.role.role.toString())
                    player.addProperty("description", r.getDescription())
                    player.addProperty("information", gson.toJson(r.prepareInformation()))
                    // add player to players json array
                    players.add(player)
                }
                println(g)
                // put player info into map with id we generated
                games.put(id, players)
                val json = gson.toJson(id);
                // send id back to frontend
                call.respond(json)
            }

            get("/game/info/{id}") {
                val id : String = call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                val info : JsonArray? = games.get(id)
                // if we can't find the game id, just redirect to homepage
                if(info == null) {
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
                val id : String = call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                call.respond(gson.toJson(games.containsKey(id)))
            }

            post("/gameover/{id}") {
                // get id
                val id : String = call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                println("Ending game $id")
                // lock stats mutex
                statsMutex.lock()
                // now, we check to make sure the id hasn't already been deleted. If it has, we already recorded stats
                // for it so we can just unlock and finish
                val notDeleted = id in games
                if(notDeleted) {
                    // delete id from games
                    games.remove(id)

                    // TODO process stats!
                    // get game result json
                    val post = call.receiveText()
                    val resultsJson = JsonParser().parse(post).asJsonObject
                    val result = resultsJson["result"].toString()
                    val prep = conn.prepareStatement("INSERT INTO games VALUES (?, ?)")
                    prep.setString(1, id)
                    prep.setString(2, result)
                    prep.executeUpdate()
                    prep.close()
                    println(resultsJson)
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
                val numGames : Int = JsonParser().parse(post).asJsonObject["numGames"].asInt
                // if there are fewer than numGames games in our map, this will take all of them
                // LinkedHashMap maintains an iteration ordering that's the same as map insertion order,
                // so since we want the most recent games we reverse the iteration order
                val recentGameIds : List<String> = games.asIterable().reversed().take(numGames).map { it.key }
                call.respond(gson.toJson(recentGameIds))
            }
        }
    }
    server.start(wait = true)
}

