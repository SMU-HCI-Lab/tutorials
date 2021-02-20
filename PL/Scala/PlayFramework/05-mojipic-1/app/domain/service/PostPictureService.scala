package domain.service

import javax.inject.Inject
import com.google.common.net.MediaType
import domain.entity.OriginalPicture
import domain.entity.PictureProperty
import domain.exception.ConversionFailureException
import domain.exception.InvalidContentTypeException
import domain.repository.PicturePropertyRepository
import infrastructure.repository.PicturePropertyRepositoryImpl
import infrastructure.service.ConvertPictureServiceImpl

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class PostPictureService @Inject() (
                                     convertPictureService: ConvertPictureServiceImpl,
                                     picturePropertyRepository: PicturePropertyRepositoryImpl,
                                     executionContext: ExecutionContext
                                   ) {
  val availableMediaTypes = Seq(MediaType.JPEG, MediaType.PNG, MediaType.GIF, MediaType.BMP)

  implicit val ec = executionContext

  def post(binary: Array[Byte], property: PictureProperty.Value): Future[Unit] = {
    if (availableMediaTypes.contains(property.contentType)) {
      for {
        id <- picturePropertyRepository.create(property)
        _ <- convertPictureService.convert(OriginalPicture(id, binary)).recoverWith {
          case e: ConversionFailureException =>
            picturePropertyRepository
              .updateStatus(id, PictureProperty.Status.Failure)
              .flatMap(_ => Future.failed(e))
        }
      } yield ()
    } else {
      Future.failed(InvalidContentTypeException(s"Invalid content type: ${property.contentType}"))
    }
  }

}
