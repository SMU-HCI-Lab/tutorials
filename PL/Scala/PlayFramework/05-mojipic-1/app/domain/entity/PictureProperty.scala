package domain.entity

import java.time.LocalDateTime
import com.google.common.net.MediaType
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.Writes

case class PictureProperty(id: PictureId, value: PictureProperty.Value)

object PictureProperty {
  sealed abstract class Status(val value: String)

  object Status {
    case object Success extends Status("success")
    case object Failure extends Status("failed")
    case object Converting extends Status("converting")

    def parse(value: String): Option[Status] =
      value match {
        case Success.value => Some(Success)
        case Failure.value => Some(Failure)
        case Converting.value => Some(Converting)
        case _ => None
      }
    implicit val writes: Writes[Status] = Writes(s => JsString(s.toString))
  }

  case class Value(
                  status: Status,
                  fileName: String,
                  contentType: MediaType,
                  overlayText: String,
                  overlayTextSize: Int,
                  createdTime: LocalDateTime
                  )

  object Value {
    implicit val mediaTypeWrites: Writes[MediaType] = Writes(s => JsString(s.toString))
    implicit val writes: Writes[Value] = Json.writes[Value]
  }

  implicit val writes: Writes[PictureProperty] = Json.writes[PictureProperty]
}
