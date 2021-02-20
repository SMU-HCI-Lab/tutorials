package controllers

import java.time.Clock
import java.time.LocalDateTime

import javax.inject.Inject
import com.google.common.io.Files
import com.google.common.net.MediaType
import domain.entity.PictureId
import domain.entity.PictureProperty
import domain.entity.TwitterId
import domain.exception.ConversionFailureException
import domain.exception.ConvertingException
import domain.exception.DatabaseException
import domain.exception.InvalidContentTypeException
import domain.exception.PictureNotFoundException
import domain.service.GetPictureService
import domain.service.PostPictureService
import play.api.cache.SyncCacheApi
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{AbstractController, Action, ControllerComponents, MultipartFormData}
import play.api.mvc.Results.BadRequest
import play.api.mvc.MultipartFormData.FilePart

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class PicturesController @Inject() (
                                   cc: ControllerComponents,
                                   postPictureService: PostPictureService,
                                   getPictureService: GetPictureService,
                                   executionContext: ExecutionContext
                                   ) extends AbstractController(cc) {

  implicit val ec = executionContext

  private[this] def createPictureProperty(
                                         file: FilePart[TemporaryFile],
                                         form: MultipartFormData[TemporaryFile]
                                         ): PictureProperty.Value = {
    val overlayText = form.dataParts.get("overlaytext").flatMap(_.headOption).getOrElse("")
    val overlayTextSize = form.dataParts.get("overlaytextsize").flatMap(_.headOption).getOrElse("60").toInt
    val contentType = MediaType.parse(file.contentType.getOrElse("application/octet-stream"))
    PictureProperty.Value(
      PictureProperty.Status.Converting,
      file.filename,
      contentType,
      overlayText,
      overlayTextSize,
      LocalDateTime.now()
    )
  }

  def post = Action.async { request =>
    request.body.asMultipartFormData match {
      case Some(form) =>
        form.file("file") match {
          case Some(file) =>
            val property = createPictureProperty(file, form)
            postPictureService
              .post(Files.toByteArray(file.ref.file), property)
              .map(_ => Ok)
          case None =>
             Future.successful(BadRequest("File parameter is not found"))
        }
      case _ =>
        Future.successful(BadRequest("Body is not found"))
    }
  }

  def get(pictureId: Long) = Action.async { request =>
    (for {
      (converted, property) <- getPictureService.get(PictureId(pictureId))
    } yield Ok(converted.binary).as(property.value.contentType.toString))
  }

}
