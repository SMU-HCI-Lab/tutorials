package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request

@Singleton
class HelloController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

    val logger = play.api.Logger("hello")

    def get(name: Option[String]) = 
        Action { implicit request: Request[AnyContent] =>
            logger.info(s"name parameter: $name")

            Ok {
                name
                    .map(s => s"Hello, $s!")
                    .getOrElse("""Please give a name as a query parameter named "name".""")
            }
        }
}
