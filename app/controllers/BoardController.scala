package controllers

import com.google.inject.Guice
import de.htwg.se.connect4.Connect4Module
import de.htwg.se.connect4.aview.Tui
import de.htwg.se.connect4.controller.controllerComponent.ControllerInterface
import de.htwg.se.connect4.model.boardComponent.BoardInterface
import de.htwg.se.connect4.model.boardComponent.boardBaseImpl.Color
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class BoardController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {


  val injector = Guice.createInjector(new Connect4Module)

  val controller = injector.getInstance(classOf[ControllerInterface])

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(controller.getBoard.getBoardAsString(controller.getBoard.getCells))
  }

  def set(row: Int, col: Int) = Action { implicit request: Request[AnyContent] =>
    Ok(controller.set(row, col))

  }


}
