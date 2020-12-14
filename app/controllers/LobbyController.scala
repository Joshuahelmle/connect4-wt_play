package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

@Singleton
class LobbyController @Inject()(val controllerComponents: ControllerComponents) (implicit system : ActorSystem, mat: Materializer) extends BaseController {

  var games : Map[Int, GameController] = Map.empty[Int, GameController]
  var gameIdx = 0



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

}
