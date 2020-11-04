package controllers

import com.google.inject.{Guice, Injector}
import de.htwg.se.connect4.Connect4Module
import de.htwg.se.connect4.aview.Tui
import de.htwg.se.connect4.controller.controllerComponent.ControllerInterface
import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import play.mvc.Results.redirect
import play.twirl.api.Html

@Singleton
class GameController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {


  val injector: Injector = Guice.createInjector(new Connect4Module)

  val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  val tui = new Tui(controller)

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def handle(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val input = body.asFormUrlEncoded.get("inputField").map(_.toString)
    tui.processInputLine(input.head, controller.getBoard)

    if (controller.getPlayers.size != 2) {
      val html = views.html.index.render(controller.stateString.replace("\n", "<br>"))
      Ok(html)
    } else {
      Ok(views.html.connect4.render(controller))
  }
  }

  def set(row: Int, col: Int) = Action { implicit request: Request[AnyContent] =>
   Ok(controller.set(row, col));

  }


}
