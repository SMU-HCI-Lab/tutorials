package domain.service

import java.time.LocalDateTime

import javax.inject.Inject
import domain.entity.PictureProperty
import domain.entity.TwitterId
import domain.repository.PicturePropertyRepository
import infrastructure.repository.PicturePropertyRepositoryImpl

import scala.concurrent.Future

class GetPicturePropertiesService @Inject() (
                                            picturePropertyRepository: PicturePropertyRepositoryImpl
                                            ) {
//  def getAllByTwitterId(twitterId: TwitterId, lastCreatedTime: LocalDateTime): Future[Seq[PictureProperty]] =
//    picturePropertyRepository.findAllByTwitterIdAndDateTime(twitterId, lastCreatedTime)

  def getAll(lastCreatedTime: LocalDateTime): Future[Seq[PictureProperty]] =
    picturePropertyRepository.findAllByDateTime(lastCreatedTime)
}
