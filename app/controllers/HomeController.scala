package controllers

import com.google.inject.Guice
import de.htwg.se.connect4.Connect4Module
import de.htwg.se.connect4.aview.Tui
import de.htwg.se.connect4.aview.gui.SwingGui
import de.htwg.se.connect4.controller.controllerComponent.ControllerInterface
import de.htwg.se.connect4.controller.controllerComponent.controllerBaseImpl.InitializationState
import de.htwg.se.connect4.model.boardComponent.BoardInterface
import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {


  val injector = Guice.createInjector(new Connect4Module)
  val controller = injector.getInstance(classOf[ControllerInterface])
  var board = injector.getInstance(classOf[BoardInterface])






  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    val string = controller.getWelcomeString
    Ok(views.html.index(string))
  }

  def rules() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.rules())
  }


}
