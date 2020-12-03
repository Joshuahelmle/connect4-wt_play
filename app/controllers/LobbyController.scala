package controllers

import com.google.inject.{Guice, Injector}
import de.htwg.se.connect4.Connect4Module
import de.htwg.se.connect4.aview.Tui
import de.htwg.se.connect4.controller.controllerComponent.ControllerInterface
import de.htwg.se.connect4.controller.controllerComponent.controllerBaseImpl.State
import de.htwg.se.connect4.model.boardComponent.{BoardInterface, CellInterface}
import de.htwg.se.connect4.model.fileIoComponent.FileIoInterface
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsNumber, JsString, Json, Writes}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import play.mvc.Results.redirect
import play.twirl.api.Html

@Singleton
class LobbyController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

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

}
