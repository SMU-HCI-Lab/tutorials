package infrastructure.repository

import java.time.LocalDateTime
import com.google.common.net.MediaType
import domain.entity.PictureId
import domain.entity.PictureProperty
import domain.exception.DatabaseException
import domain.exception.DomainException
import domain.exception.PictureNotFoundException
import domain.repository.PicturePropertyRepository
import scalikejdbc._
import scalikejdbc.jsr310._
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Try
import scala.util.control.NonFatal

class PicturePropertyRepositoryImpl extends PicturePropertyRepository {

  def create(value: PictureProperty.Value): Future[PictureId] =
    Future.fromTry(Try {
      using(DB(ConnectionPool.borrow())) { db =>
        db.localTx { implicit session =>
          val sql =
            sql"""INSERT INTO "picture_properties" (
                  | "status",
                  | "file_name",
                  | "content_type",
                  | "overlay_text_size",
                  | "created_time"
                  | ) VALUES (
                  | ${value.status.value},
                  | ${value.fileName},
                  | ${value.contentType.toString},
                  | ${value.overlayText},
                  | ${value.overlayTextSize},
                  | ${value.createdTime}
                  | )
               """.stripMargin
          PictureId(sql.updateAndReturnGeneratedKey().apply())
        }
      }
    }.recoverWith {
      case NonFatal(e) =>
        Failure(DatabaseException("PicturePropertyRepository failed to create"))
    })

  def updateStatus(pictureId: PictureId, status: PictureProperty.Status): Future[Unit] =
    Future.fromTry(Try {
      using(DB(ConnectionPool.borrow())) { db =>
        db.localTx { implicit session =>
          val sql =
            sql"""UPDATE "picture_properties" SET "status" = ${status.value} WHERE "picture_id" = ${pictureId.value}"""
          sql.update().apply()
          ()
        }
      }
    }.recoverWith {
      case NonFatal(e) => Failure(DatabaseException(s"PicturePropertyRepository failed to findAllByTwitterIdAndDateTime. PictureId: ${pictureId.value}"))
    })

  def find(pictureId: PictureId): Future[PictureProperty] =
    Future.fromTry(Try {
      using(DB(ConnectionPool.borrow())) { db =>
        db.readOnly { implicit session =>
          val sql =
            sql"""SELECT
                 | "picture_id",
                 | "status",
                 | "file_name",
                 | "content_type",
                 | "overlay_text",
                 | "overlay_text_size",
                 | "create_time"
                 | FROM "picture_properties" WHERE "picture_id" = ${pictureId.value}
               """.stripMargin
          sql.map(resultSetToPictureProperty).single().apply()
            .getOrElse(throw PictureNotFoundException(s"Picture is not found. Picture Id: ${pictureId.value}"))
        }
      }
    })

//  def findAllByTwitterIdAndDateTime(twitterId: TwitterId, toDateTime: LocalDateTime): Future[Seq[PictureProperty]] =
//    Future.fromTry(Try {
//      using(DB(ConnectionPool.borrow())) { db =>
//        db.readOnly { implicit session =>
//          val sql =
//            sql"""SELECT
//                 | "picture_id",
//                 | "status",
//                 | "twitter_id",
//                 | "file_name",
//                 | "content_type",
//                 | "overlay_text",
//                 | "overlay_text_size",
//                 | "created_time"
//                 | FROM "picture_properties"
//                 | WHERE "twitter_id" = ${twitterId.value} AND "created_time" > $toDateTime ORDER BY "created_time" DESC
//              """.stripMargin
//          sql.map(resultSetToPictureProperty).list().apply()
//        }
//      }
//    }.recoverWith {
//      case NonFatal(e) => Failure(DatabaseException(s"PicturePropertyRepository failed to findAllByTwitterIdAndDateTime. TwitterId: ${twitterId.value}"))
//    })

  def findAllByDateTime(toDateTime: LocalDateTime): Future[Seq[PictureProperty]] =
    Future.fromTry(Try {
      using(DB(ConnectionPool.borrow())) { db =>
        db.readOnly { implicit session =>
          val sql =
            sql"""SELECT
                 | "picture_id",
                 | "status",
                 | "file_name",
                 | "content_type",
                 | "overlay_text",
                 | "overlay_text_size",
                 | "created_time"
                 | FROM "picture_properties" WHERE "created_time" > $toDateTime ORDER BY "created_time" DESC
              """.stripMargin
          sql.map(resultSetToPictureProperty).list().apply()
        }
      }
    }.recoverWith {
      case NonFatal(e) => Failure(DatabaseException("PicturePropertyRepository failed to findAllByDateTime"))
    })

  private[this] def resultSetToPictureProperty(rs: WrappedResultSet): PictureProperty = {
    val value =
      PictureProperty.Value(
        PictureProperty.Status.parse(rs.string("status")).get,
        rs.string("file_name"),
        MediaType.parse(rs.string("content_type")),
        rs.string("overlay_text"),
        rs.int("overlay_text_size"),
        rs.localDateTime("created_time")
      )
    PictureProperty(PictureId(rs.long("picture_id")), value)
  }
}
