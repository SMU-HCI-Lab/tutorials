package domain.entity

import play.api.libs.json.JsString
import play.api.libs.json.Writes

case class PictureId(value: Long)

object PictureId {
  implicit val writes: Writes[PictureId] = Writes(id => JsString(id.value.toString))
}