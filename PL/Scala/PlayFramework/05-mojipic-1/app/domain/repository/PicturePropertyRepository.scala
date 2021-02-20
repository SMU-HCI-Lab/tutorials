package domain.repository

import java.time.LocalDateTime
import domain.entity.PictureId
import domain.entity.PictureProperty
import domain.entity.TwitterId
import scala.concurrent.Future

trait PicturePropertyRepository {
  def create(value: PictureProperty.Value): Future[PictureId]
  def updateStatus(pictureId: PictureId, status: PictureProperty.Status): Future[Unit]
  def find(pictureId: PictureId): Future[PictureProperty]
//  def findAllByTwitterIdAndDateTime(twitterId: TwitterId, lastCreatedTime: LocalDateTime): Future[Seq[PictureProperty]]
  def findAllByDateTime(lastCreatedTime: LocalDateTime): Future[Seq[PictureProperty]]
}
