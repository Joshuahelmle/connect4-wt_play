package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import de.htwg.se.connect4.util.Observer
import javax.inject.{Inject, Singleton}
import play.api.libs.json
import play.api.libs.json.{JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request, WebSocket}

@Singleton
class LobbyController @Inject()(val controllerComponents: ControllerComponents) (implicit system : ActorSystem, mat: Materializer) extends BaseController {

  var games : Map[Int, GameController] = Map.empty[Int, GameController]
  var gameIdx = 0
  object Connect4LobbyWebSocketActorFactory {
    def create(out: ActorRef) = {
      Props(new Connect4LobbyWebSocketActor(out))
    }
  }


  def newGame() = Action {implicit request : Request[AnyContent] =>
    val game = new GameController(controllerComponents)

    games += (gameIdx -> game)
    print(games)
    val oldIdx = gameIdx
    gameIdx += 1
    Redirect(s"/games/$oldIdx")
  }

  def index() = Action { implicit request : Request[AnyContent] =>
    Ok(views.html.games(games))
  }

  def getGame(idx : Int) = Action { implicit request : Request[AnyContent] =>
    val game = games(idx)
    Ok(views.html.connect4.render(game.controller,idx))
  }

  def initGame(idx: Int)  = Action { implicit request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val input = body.asFormUrlEncoded.get("inputField").map(_.toString)
    val game = games(idx)
    game.controller.addPlayer(input.head)
    Redirect(s"/games/$idx")
  }

  def getJson(idx: Int) = Action { implicit request: Request[AnyContent] =>
    val game = games(idx)
    Ok(game.controllerToJson())
  }

  def openSocket(idx: Int)   = {
    val game = games(idx)
    game.socket
  }

  def getGames() = {
    Json.obj(
      "games" -> Json.toJson(
        for {
          game <- games

        } yield {
          Json.obj(
            "player1" -> game._2.controller.getPlayers(0).playerName,
            "player2" -> game._2.controller.getPlayers(1).playerName,
            "id" -> game._1
          )
        }

      )
    )
  }

  def createGame() = {

  }

  def socket = WebSocket.accept[JsValue,JsValue] { request =>
    ActorFlow.actorRef {
      out => Connect4LobbyWebSocketActorFactory.create(out)
    }
  }



  class Connect4LobbyWebSocketActor(out : ActorRef) extends Actor {
    override def receive: Receive = {
      case msg: JsValue =>
        println(msg)
          val _type = (msg \ "_type").as[String]
          _type match {
            case "getGames" => out ! getGames
            case "createGame" => out ! createGame
      }

    }


  }

}
