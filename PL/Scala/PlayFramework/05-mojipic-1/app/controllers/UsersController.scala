package controllers

import java.time.LocalDateTime
import javax.inject.Inject
import domain.exception.DatabaseException
import domain.service.GetPicturePropertiesService
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.ExecutionContext

class UsersController @Inject ()(
                                cc: ControllerComponents,
                                getPicturePropertiesService: GetPicturePropertiesService,
                                executionContext: ExecutionContext
                                ) extends AbstractController(cc) {

  implicit val ec = executionContext

  def getProperties(lastCreatedTime: Option[String]) = Action.async {
    val localDateTime = lastCreatedTime.map(LocalDateTime.parse).getOrElse(LocalDateTime.parse("0000-01-01T00:00:00"))
    (for {
      properties <- getPicturePropertiesService.getAll(localDateTime)
    } yield {
      Ok(Json.toJson(properties)).as("application/json")
    })
  }
}
