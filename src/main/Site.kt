package main
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import io.ktor.application.call;
import io.ktor.application.install
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.request.receive
import io.ktor.request.receiveParameters
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
import jdk.nashorn.internal.parser.JSONParser
import roles.Role
import thavalon.*
import java.io.File
import java.lang.IllegalArgumentException

data class Names(val names : List<String>)

fun main(args: Array<String>) {

    val games: MutableMap<String, String> = HashMap()
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
                val id = parsed["id"].asString
                val g : Game = when(names.size) {
                    5 -> FivesRuleset.makeGame(names)
                    7 -> SevensRuleset.makeGame(names)
                    8 -> EightsRuleset.makeGame(names)
                    10 -> TensRuleset.makeGame(names)
                    else -> throw IllegalArgumentException("BAD NAMES: $names")
                }


                val gameResponse : MutableList<Map<String, String>> = ArrayList()
                for (r : Role in g.rolesInGame) {
                    val m  : MutableMap<String, String> = HashMap()

                    m["name"] = r.player.name
                    m["role"] = r.role.role.toString()
                    m["information"] = r.information.toString()

                    gameResponse.add(m)
                }
                println(g)
                val gson = Gson()
                val gameJson = gson.toJson(gameResponse)
                games.put(id, gameJson)
            }

            get("/game/info/{id}") {
                println(games)
                val id : String= call.parameters["id"] ?: throw IllegalArgumentException("Couldn't find param")
                call.respond(games.get(id) ?: throw IllegalArgumentException("BAD ID: " + id))
            }
        }
    }
    server.start(wait = true)
}

