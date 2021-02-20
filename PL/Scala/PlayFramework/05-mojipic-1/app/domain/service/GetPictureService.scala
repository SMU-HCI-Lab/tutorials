package domain.service

import javax.inject.Inject
import domain.entity.ConvertedPicture
import domain.entity.PictureId
import domain.entity.PictureProperty
import domain.exception.ConversionFailureException
import domain.exception.ConvertingException
import domain.repository.ConvertedPictureRepository
import domain.repository.PicturePropertyRepository
import infrastructure.repository.{ConvertedPictureRepositoryImpl, PicturePropertyRepositoryImpl}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class GetPictureService @Inject() (
                                    convertedPictureRepository: ConvertedPictureRepositoryImpl,
                                    picturePropertyRepository: PicturePropertyRepositoryImpl,
                                    executionContext: ExecutionContext
                                  ) {
  implicit val ec = executionContext

  def get(pictureId: PictureId): Future[(ConvertedPicture, PictureProperty)] = {
    for {
      property <- picturePropertyRepository.find(pictureId)
      picture <- property.value.status match {
        case PictureProperty.Status.Success =>
          convertedPictureRepository.find(pictureId)
        case PictureProperty.Status.Failure =>
          Future.failed(new ConversionFailureException(s"Picture conversion is failed. Picture Id: ${pictureId.value}"))
        case PictureProperty.Status.Converting =>
          Future.failed(new ConvertingException(s"Picture is converting. Picture Id: ${pictureId.value}"))
      }
    } yield (picture, property)
  }

}
