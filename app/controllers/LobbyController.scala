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

  def createNewGame(player1: String, player2: String) {
    val game = new GameController(controllerComponents)

    games += (gameIdx -> game)
    print(games)
    val oldIdx = gameIdx
    gameIdx += 1

  }

  def createGame(player1:String, player2:String) = {
    createNewGame(player1, player2)

    Json.obj(
      "game" -> Json.toJson(
          Json.obj(
            "test" -> "test"
          )
      )
    )

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
            case "createGame" => {
              val player1 = (msg \ "player1").as[String]
              val player2 = (msg \ "player2").as[String]
              out ! createGame(player1, player2)
            }
      }

    }


  }

}
