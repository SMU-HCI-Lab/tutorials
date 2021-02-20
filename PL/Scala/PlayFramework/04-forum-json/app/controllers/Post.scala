package controllers

import java.time.OffsetDateTime
import play.api.libs.json.Json
import play.api.libs.json.Writes

case class Post(id: Long, body: String, date: OffsetDateTime)

object Post {
    implicit val writes: Writes[Post] = Json.writes[Post]
    def apply(body: String, date: OffsetDateTime): Post = 
        Post(0, body, date)
}