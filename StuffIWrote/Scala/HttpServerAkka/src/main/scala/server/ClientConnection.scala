package server

import java.io.{PrintStream, InputStreamReader, BufferedReader}
import java.net.Socket

import akka.actor.{Props, Actor}
import server.HTTP._

import scala.collection.mutable

/**
 * Ethan Petuchowski
 * 9/30/15
 */
class ClientConnection(socket: Socket) extends Actor {
    override def receive = { case x => println(s"received something?: $x") }
    val readIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val writeOut = new PrintStream(socket.getOutputStream)
    def readRequest(): Option[Request] = {
        println("reading request")

        val (method, path) = {
            val requestLine = readIn.readLine()
            if (requestLine == null) {
                context.stop(self)
                return None
            }
            val firstWord = requestLine.takeWhile(_ != ' ')
            val method = Method.parse(firstWord)
            // \s is whitespace, \S is non-whitespace
            val pathPattern = """^\S+\s+(\S+)""".r
            val pathOpt = pathPattern
                .findFirstMatchIn(requestLine)
                .map(_.group(1))
                .getOrElse("/index.html")
            method → pathOpt
        }

        val headers = {
            var line = readIn.readLine()
            val headers = new Headers
            while (line != null && !line.isEmpty) {
                headers.addParsed(line)
                line = readIn.readLine()
            }
            headers
        }

        val body = method match {
            case Post =>
                val bodyBuilder = new StringBuilder
                var line = readIn.readLine()
                while (line != null && !line.isEmpty) {
                    bodyBuilder.append(s"$line\n")
                    line = readIn.readLine()
                }
                bodyBuilder.toString()
            case _ => ""
        }

        Some(Request(method, path, headers, body))
    }

    def respond(request: Option[Request]): Unit = {
        if (request.isEmpty) return
        val statusCode = "200 OK"
        val responseStatus = s"HTTP/1.1 $statusCode"
        val responseHeaders = {
            new Headers(mutable.Map[String, String](
                "Content-Type" → "text/html; charset=UTF-8",
                "Accept-Ranges" → "bytes",
                "Connection" → "close" // no persistence at this time.
            ))
        }
        val body = "<h1>aww yeah.</h1>"
        responseHeaders.addPair("Content-Length" → body.length.toString)
        val response = Seq(
            responseStatus,
            responseHeaders.httpString,
            body
        ).mkString("", CRLF, CRLF)
        println(response)
        writeOut.print(response)

        // TODO maybe not so fast? (consider the effect of sending the Content-Length)
        writeOut.close()
    }

    val request = readRequest()
    respond(request)
    socket.close() // not sure how to implement persistence yet.
    context.stop(self)
}

object ClientConnection {
    def props(socket: Socket) = Props(classOf[ClientConnection], socket)
}


case class Request(
    method: HTTP.Method,
    path: String,
    headers: Headers,
    body: String
)