package infrastructure.actor

import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import akka.actor.Actor
import akka.event.Logging
import com.google.inject.AbstractModule
import domain.entity.ConvertedPicture
import domain.entity.OriginalPicture
import domain.entity.PictureProperty
import domain.repository.ConvertedPictureRepository
import domain.repository.PicturePropertyRepository
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import play.api.Configuration
import play.api.libs.concurrent.AkkaGuiceSupport
import scala.concurrent.Future
import scala.util.Try
import scala.util.control.NonFatal

object ConvertPictureActor {
  case class Convert(original: OriginalPicture)
}

class ConvertPictureActor @Inject() (
                                    convertedPictureRepository: ConvertedPictureRepository,
                                    picturePropertyRepository: PicturePropertyRepository,
                                    configuration: Configuration
                                    ) extends Actor {
  import context.dispatcher
  import ConvertPictureActor.Convert

  val log = Logging(context.system, this)

  val imageMagickPath = configuration.getOptional[String]("imagemagick.path")
    .getOrElse("`imagemagick.path` is not specified.")
  val imageMagickFontPath = configuration.getOptional[String]("imagemagick.fontpath")
    .getOrElse("`imagemagick.fontpath` is not specified.")

  def receive = {
    case Convert(original) =>
      log.info(s"Conversion started. PictureId: ${original.id.value}")
      (for {
        property <- picturePropertyRepository.find(original.id)
        converted <- convert(original, property)
        _ <- convertedPictureRepository.create(converted)
        _ <- picturePropertyRepository.updateStatus(original.id, PictureProperty.Status.Success)
      } yield {
        log.info(s"Conversion finished. PictureId: ${original.id.value}")
      }).onFailure {
        case NonFatal(e) =>
          log.error(e, s"Conversion failed. PictureId: ${original.id.value}")
          picturePropertyRepository.updateStatus(original.id, PictureProperty.Status.Failure)
      }
  }

  private[this] def convert(original: OriginalPicture, property: PictureProperty): Future[ConvertedPicture] = {
    Future.fromTry(Try {
      val originalPath = Files.createTempFile("mojipic", ".tmp")
      val convertedPath = Files.createTempFile("mojipic", ".converted")
      Files.write(originalPath, original.binary)

      invokeCmd(original, property, originalPath, convertedPath)

      val converted = ConvertedPicture(original.id, Files.readAllBytes(convertedPath))

      Files.delete(originalPath)
      Files.delete(convertedPath)

      converted
    })
  }

  private[this] def invokeCmd(original: OriginalPicture, property: PictureProperty, originalPath: Path, convertedPath: Path): Unit = {
    val cmd = new ConvertCmd()
    cmd.setSearchPath(imageMagickPath)
    val op = new IMOperation()
    op.addImage(originalPath.toAbsolutePath.toString)
    op.gravity("south")
    op.font(imageMagickFontPath)
    op.pointsize(property.value.overlayTextSize)
    op.stroke("#000C")
    op.strokewidth(2)
    op.annotate(0, 0, 0, 0, property.value.overlayText)
    op.stroke("none")
    op.fill("white")
    op.annotate(0, 0, 0, 0, property.value.overlayText)
    op.addImage(convertedPath.toAbsolutePath.toString)
    cmd.run(op)
  }
}

class ConvertPictureActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[ConvertPictureActor]("convert-picture-actor")
  }
}
