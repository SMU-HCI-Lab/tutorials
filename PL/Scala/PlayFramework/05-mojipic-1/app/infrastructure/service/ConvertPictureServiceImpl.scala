package infrastructure.service

import javax.inject.Inject
import com.rabbitmq.client.ConnectionFactory
import domain.entity.OriginalPicture
import domain.exception.ConversionFailureException
import domain.service.ConvertPictureService
import infrastructure.rabbitmq.RabbitMQConfiguration
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Try
import scala.util.control.NonFatal

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

class ConvertPictureServiceImpl @Inject() (
                                          rabbitMQConfiguration: RabbitMQConfiguration
                                          ) extends ConvertPictureService {
  val factory = new ConnectionFactory
  factory.setHost(rabbitMQConfiguration.HostName)

  def convert(original: OriginalPicture): Future[Unit] =
    Future.fromTry(Try {
      val connection = factory.newConnection()
      val channel = connection.createChannel()
      channel.queueDeclare(rabbitMQConfiguration.OriginalPictureQueueName, false, false, false, null)
      val serializedOriginal = serialise(original)
      channel.basicPublish("", rabbitMQConfiguration.OriginalPictureQueueName, null, serializedOriginal)
    }.recoverWith {
      case NonFatal(e) => Failure(ConversionFailureException(s"It failed to send a picture to RabbitMQ. PictureId: ${original.id.value}"))
    })

  /**
   * https://stackoverflow.com/questions/39369319/convert-any-type-in-scala-to-arraybyte-and-back
   */
  def serialise(value: Any): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close()
    stream.toByteArray
  }

  /**
   * https://stackoverflow.com/questions/39369319/convert-any-type-in-scala-to-arraybyte-and-back
   * @param bytes
   * @return
   */
  def deserialise(bytes: Array[Byte]): Any = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject
    ois.close()
    value
  }

}
