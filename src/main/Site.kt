package main
import io.ktor.application.call;
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File


fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 4444) {
        routing {
            static("/static") {
                staticRootFolder = File("src/public/build/static")
                static("js") {
                    files("js")
                }

            }
            get("/") {
                call.respondFile(File("src/public/build/index.html"))
            }
        }
    }
    server.start(wait = true)
}