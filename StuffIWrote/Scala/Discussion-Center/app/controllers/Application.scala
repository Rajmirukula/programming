package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.{Json, JsString, JsObject, JsValue}
import scala.concurrent.ExecutionContext
import play.api.libs.iteratee._
import ExecutionContext.Implicits.global
import scala.Some
import actors.{DiscussionApp, Join}

object Application extends Controller {

  /**
   * '/' was accessed
   */
  def index = Action { implicit request =>
    Ok(views.html.index(Some("Root URL")))
  }

  /**
   * Either a user is signed in, or redirect to the homepage
   */
  def chatRoom(username: Option[String]) = Action { implicit request =>
    username match {
      case Some(string) => Ok(views.html.index(Some(string)))
      case _ => Redirect(routes.Application.index())
    }
  }

  /**
   * This is where we define how to handle information coming
   * in and out of the WebSocket
   */
  def chatSocket(username: String) = WebSocket.using[JsValue] { implicit request =>
    println(s"\n$username sent the following message through the chatSocket:")

    // create Enumerator to send through client socket
//    val (out, channel) = Concurrent.broadcast[JsValue]

    // temporarily: send one item through websocket and leave it open
    val out = Enumerator[JsValue](JsObject(Seq("username" -> JsString(username))))

    // create Iteratee to receive from client socket
//    val in = Iteratee.foreach[JsValue] { message => channel.push(message) }

    // temporarily: just pretty-print any received JSON to the console
    val in = Iteratee.foreach[JsValue] { message => println(Json.prettyPrint(message)) }
    (in, out)
  }

  def chatRoomJs(username: String) = Action { implicit request =>
    Ok(views.js.chatRoom(username))
  }
}
