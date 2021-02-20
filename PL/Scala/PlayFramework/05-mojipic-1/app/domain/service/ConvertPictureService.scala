package domain.service

import domain.entity.OriginalPicture
import scala.concurrent.Future

trait ConvertPictureService {
  def convert(original: OriginalPicture): Future[Unit]
}
