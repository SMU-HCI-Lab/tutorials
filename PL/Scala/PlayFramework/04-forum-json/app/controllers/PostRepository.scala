package controllers

import scalikejdbc._
import scalikejdbc.jsr310._

object PostRepository {

    def findAll: Seq[Post] = DB readOnly { implicit session =>
        sql"SELECT id, body, date FROM posts".map { rs =>
            Post(rs.long("id"), rs.string("body"), rs.offsetDateTime("date"))
        }.list().apply()
    }

    def add(post: Post): Unit = DB localTx { implicit session =>
        sql"INSERT INTO posts (body, date) VALUES (${post.body}, ${post.date})".update().apply()
    }
}