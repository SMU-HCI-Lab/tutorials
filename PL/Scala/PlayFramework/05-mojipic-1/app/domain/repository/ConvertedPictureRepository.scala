package domain.repository

import domain.entity.ConvertedPicture
import domain.entity.PictureId
import scala.concurrent.Future

trait ConvertedPictureRepository {
  def create(converted: ConvertedPicture): Future[Unit]

  def find(pictureId: PictureId): Future[ConvertedPicture]
}
