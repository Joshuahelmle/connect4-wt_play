package controllers

import com.google.inject.{Guice, Injector}
import de.htwg.se.connect4.Connect4Module
import de.htwg.se.connect4.aview.Tui
import de.htwg.se.connect4.controller.controllerComponent.ControllerInterface
import de.htwg.se.connect4.controller.controllerComponent.controllerBaseImpl.State
import de.htwg.se.connect4.model.boardComponent.BoardInterface
import de.htwg.se.connect4.model.fileIoComponent.FileIoInterface
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsNumber, JsString, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import play.mvc.Results.redirect
import play.twirl.api.Html

@Singleton
class GameController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  var gameIdx : Int = 0
  var games : Map[Int, (ControllerInterface, Tui)] = Map.empty[Int, (ControllerInterface, Tui)]

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def handle(idx : Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val tuple = games(idx)
    val tui = tuple._2
    val controller = tuple._1

    if (controller.getPlayers.size == 0){
      val html = views.html.connect4.render(controller,idx)
      Ok(html)
    } else {
      val body: AnyContent = request.body
      val input = body.asFormUrlEncoded.get("inputField").map(_.toString)
      tui.processInputLine(input.head, controller.getBoard)
      Ok(views.html.connect4.render(controller,idx))
  }
  }

  def initGame(idx : Int)  = Action { implicit request: Request[AnyContent] =>
    val tuple = games(idx)
    val tui = tuple._2
    val controller = tuple._1
    val body: AnyContent = request.body
    val input = body.asFormUrlEncoded.get("inputField").map(_.toString)
    tui.processInputLine(input.head, controller.getBoard)
    Redirect(s"/games/$idx")
  }

  def newGame() = Action {implicit request : Request[AnyContent] =>
    val injector = Guice.createInjector(new Connect4Module)
    val controller = injector.getInstance(classOf[ControllerInterface])
    val tui = new Tui(controller)
    games += (gameIdx -> (controller,tui))
    print(games)
    val oldIdx = gameIdx
    gameIdx += 1
    Redirect(s"/games/$oldIdx")
  }

  def index() = Action { implicit request : Request[AnyContent] =>
    Ok(views.html.games(games))
  }

  def getGame(idx : Int) = Action { implicit request : Request[AnyContent] =>
    val tuple = games(idx)
    val tui = tuple._2
    val controller = tuple._1
    Ok(views.html.connect4.render(controller,idx))

  }

  def restartGame(idx : Int) = Action { implicit request : Request[AnyContent] =>
    val tuple = games(idx)
    val tui = tuple._2
    val controller = tuple._1
    controller.createNewBoard(controller.sizeOfRows, controller.sizeOfCols)
    Ok(views.html.connect4.render(controller,idx))
  }

  def undoTurn(idx : Int) = Action { implicit request : Request[AnyContent] =>
    val tuple = games(idx)
    val tui = tuple._2
    val controller = tuple._1
    controller.undo()
    Ok(views.html.connect4.render(controller,idx))
  }

  def redoTurn(idx : Int) = Action { implicit request : Request[AnyContent] =>
    val tuple = games(idx)
    val tui = tuple._2
    val controller = tuple._1
    controller.redo
    Ok(views.html.connect4.render(controller,idx))
  }

  def quitGame(idx: Int) = Action { implicit request : Request[AnyContent] =>
    games -= (idx)
    Redirect(s"/games")
  }

  def getJson(idx: Int)  = Action { implicit request : Request[AnyContent] =>
    val game = games(idx)
    val controller = game._1
    val stateToSave = State(controller.getCurrentPlayerIndex, controller.getPlayers, controller.getState.toString())
    val json = controllerToJson(game._1.getBoard, stateToSave)
    Ok(json)
  }

  def controllerToJson(board: BoardInterface, state: State) = {
    Json.obj(
      "currentPlayerIndex" -> JsNumber(state.currentPlayerIndex),
      "state" -> JsString(state.state),
      "players" -> Json.toJson(
        for {
          index <- state.players.indices

        } yield {
          Json.obj(
            "name" -> state.players(index).playerName,
            "color" -> state.players(index).color,
            "piecesLeft" -> state.players(index).piecesLeft,
          )
        }

      ),
      "board" -> Json.obj(
        "row" -> JsNumber(board.sizeOfRows),
        "col" -> JsNumber(board.sizeOfCols),
        "cells" -> Json.toJson(
          for {
            row <- 0 until board.sizeOfRows
            col <- 0 until board.sizeOfCols
          } yield {
            Json.obj(
              "row" -> row,
              "col" -> col,
              "cell" -> Json.toJson(board.cell(row, col))
            )
          }
        )
      )
    )
  }


}
