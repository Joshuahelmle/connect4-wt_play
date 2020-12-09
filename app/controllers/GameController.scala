package controllers

import akka.actor._
import com.google.inject.{Guice, Injector}
import de.htwg.se.connect4.Connect4Module
import de.htwg.se.connect4.aview.Tui
import de.htwg.se.connect4.controller.controllerComponent.ControllerInterface
import de.htwg.se.connect4.controller.controllerComponent.controllerBaseImpl.State
import de.htwg.se.connect4.model.boardComponent.{BoardInterface, CellInterface}
import de.htwg.se.connect4.model.fileIoComponent.FileIoInterface
import de.htwg.se.connect4.util.Observer
import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, Request, WebSocket}
import play.mvc.Results.redirect
import play.twirl.api.Html
import akka.actor._
import akka.stream.Materializer
import play.api.Play.materializer
import play.api.libs.json
import play.api.libs.streams.ActorFlow



@Singleton
class GameController (cc: ControllerComponents) (implicit system : ActorSystem, mat: Materializer) extends AbstractController(cc) {
  val injector = Guice.createInjector(new Connect4Module)
  val controller = injector.getInstance(classOf[ControllerInterface])
  object Connect4WebSocketActorFactory {
    def create(out: ActorRef) = {
      Props(new Connect4WebSocketActor(out))
    }
  }



  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
    /*
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
 */
  implicit val cellWrites = new Writes[CellInterface] {
    def writes(cell: CellInterface) = Json.obj(
      "isSet" -> cell.isSet,
      "color" -> cell.color

    )
  }

  def setCol(col : Int) = Action { implicit request : Request[AnyContent] =>
    controller.setCol(col)
    val json = controllerToJson()
    Ok(json)
  }
 /* SHOULD BE DONE IN LOBBY
  def initGame()  = Action { implicit request: Request[AnyContent] =>
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
*/
  def restartGame() {
    controller.createNewBoard(controller.sizeOfRows, controller.sizeOfCols)
   //TODO: send new state to client
  }

  def undoTurn()  {
    controller.undo()
    //TODO: send new state to client
  }

  def redoTurn()  {
    controller.redo
    //TODO: send new state to client
  }

  def quitGame(){
    //TODO: checkout after Lobby implemention
    Redirect(s"/games")
  }

  def getJson()  = Action { implicit request : Request[AnyContent] =>
    val json = controllerToJson()
    Ok(json)
  }

  def controllerToJson() = {
    val board = controller.getBoard
    val state = State(controller.getCurrentPlayerIndex, controller.getPlayers, controller.getState.toString())
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

  def socket = WebSocket.accept[String,String] { request =>
    ActorFlow.actorRef {
      out => Connect4WebSocketActorFactory.create(out)
    }
  }

class Connect4WebSocketActor(out : ActorRef) extends Actor with Observer {
  controller.add(this)
  override def receive: Receive = {
    case msg: String =>
      println(msg)
      val json: JsValue = Json.parse(msg)
      val _type = (json \ "_type").as[String]
      val _msg = (json \ "_msg").as[String]
      _type match {
        case "playTurn" => {
          val _col = (json \ "_col").as[Int]
          controller.setCol(_col)
        }
        case "undo" => controller.undo
        case "redo" => controller.redo
        case "quit" => {

          out ! ("quitGame")
        }
        case "restart" => restartGame()
        case _ => println("default case")

      }

  }

  override def update = {
    out ! ("done")
  }
}


}
