package infrastructure.repository

import domain.entity.ConvertedPicture
import domain.entity.PictureId
import domain.exception.DatabaseException
import domain.exception.DomainException
import domain.exception.PictureNotFoundException
import domain.repository.ConvertedPictureRepository
import scalikejdbc._
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Failure
import scala.util.Try
import scala.util.control.NonFatal

class ConvertedPictureRepositoryImpl extends ConvertedPictureRepository {

  def create(picture: ConvertedPicture): Future[Unit] =
    Future.fromTry(Try {
      using(DB(ConnectionPool.borrow())) { db =>
        db.localTx { implicit session =>
          val sql =
            sql"""INSERT INTO "converted_pictures" (
                  | "picture_id",
                  | "binary"
                  | ) VALUES (
                  | ${picture.id.value},
                  | ${picture.binary}
                  | )
               """.stripMargin
          sql.update().apply()
          ()
        }
      }
    }.recoverWith {
      case NonFatal(e) => Failure(DatabaseException(s"ConvetedPictureRepository failed to create. PictureId: ${picture.id.value}"))
    })

  def find(pictureId: PictureId): Future[ConvertedPicture] =
    Future.fromTry(Try {
      using(DB(ConnectionPool.borrow())) { db =>
        db.readOnly { implicit session =>
          val sql =
            sql"""SELECT "binary" FROM "converted_pictures" WHERE "picture_id" = ${pictureId.value}"""
            .map(rs => ConvertedPicture(pictureId, rs.bytes("binary")))
          sql.single().apply().getOrElse(throw PictureNotFoundException(s"Picture is not found. PictureId: ${pictureId.value}"))
        }
      }
    })
}