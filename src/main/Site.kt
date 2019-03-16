package main
import com.google.gson.JsonParser
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
import thavalon.*
import java.io.File
import java.lang.IllegalArgumentException

data class Names(val names : List<String>)

fun main(args: Array<String>) {
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
                val g : Game = when(names.size) {
                    5 -> FivesRuleset.makeGame(names)
                    7 -> SevensRuleset.makeGame(names)
                    8 -> EightsRuleset.makeGame(names)
                    10 -> TensRuleset.makeGame(names)
                    else -> throw IllegalArgumentException("BAD NAMES: $names")
                }
                val status = g.setUp()
                if(!status) {
                    throw IllegalArgumentException("GAME NOT SETUP")
                }

            }
        }
    }
    server.start(wait = true)
}

