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
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import roles.Role
import thavalon.*
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*

fun main(args: Array<String>) {
    val gson = Gson()
    val games : MutableMap<String, JsonArray> = HashMap()
    val server = embeddedServer(Netty, port = 4444) {
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
                val id = UUID.randomUUID().toString().substring(0, 6)
                val g : Game = when(names.size) {
                    5 -> FivesRuleset().makeGame(names)
                    7 -> SevensRuleset().makeGame(names)
                    8 -> EightsRuleset().makeGame(names)
                    10 -> TensRuleset().makeGame(names)
                    else -> throw IllegalArgumentException("BAD NAMES: $names")
                }

                // construct json for player info
                val players = JsonArray()
                for (r : Role in g.rolesInGame) {
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
                call.respond(json);
            }

            get("/game/info/{id}") {
                println(games)
                val id : String= call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                // TODO this might have to be gson.toJson(games.get(id)), not sure how sending a jsonArray will play w/frontend
                call.respond(games.get(id) ?: throw IllegalArgumentException("BAD ID: $id"))
            }
        }
    }
    server.start(wait = true)
}

