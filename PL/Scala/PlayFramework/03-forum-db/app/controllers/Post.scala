package controllers

import java.time.OffsetDateTime

case class Post(id: Long, body: String, date: OffsetDateTime)

object Post {
    def apply(body: String, date: OffsetDateTime): Post = 
        Post(0, body, date)
}