package infrastructure.rabbitmq

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, IOException, ObjectInputStream, ObjectOutputStream}
import java.util.concurrent.Executors

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import akka.actor.ActorRef
import com.google.inject.AbstractModule
import com.rabbitmq.client.{AMQP, ConnectionFactory, DefaultConsumer, Envelope, ShutdownSignalException}
import domain.entity.OriginalPicture
import infrastructure.actor.ConvertPictureActor
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.util.control.NonFatal

@Singleton
class RabbitMQConsumer @Inject() (
                                 worker: RabbitMQConsumeWorker,
                                 applicationLifecycle: ApplicationLifecycle
                                 ) {
  private[this] val executor = Executors.newFixedThreadPool(1)
  executor.submit(worker)
  applicationLifecycle.addStopHook { () =>
    Future.successful(worker.close())
  }
}

class RabbitMQConsumeWorker @Inject() (
                                      rabbitMQConfiguration: RabbitMQConfiguration,
                                      @Named("convert-picture-actor") convertPictureActor: ActorRef
                                      ) extends Runnable {
  val factory = new ConnectionFactory()
  factory.setHost(rabbitMQConfiguration.HostName)
  val connection = factory.newConnection()
  val channel = connection.createChannel()


  override def run(): Unit = {
    channel.queueDeclare(rabbitMQConfiguration.OriginalPictureQueueName, false, false, false, null)
    val consumer = new DefaultConsumer(channel) {

      // https://www.rabbitmq.com/confirms.html
      @throws(classOf[IOException])
      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope, properties:AMQP.BasicProperties,
                                  body: Array[Byte]): Unit = {
        def deliveryTag = envelope.getDeliveryTag()
        val original = deserialise(body).asInstanceOf[OriginalPicture]
        convertPictureActor ! ConvertPictureActor.Convert(original)
      }
    }
    channel.basicConsume(rabbitMQConfiguration.OriginalPictureQueueName, true, consumer)
    Logger.logger.info(s"Started monitoring a RabbitMQ. Thread: ${Thread.currentThread().getName}")
//    messageLoop(consumer)
  }

//  @tailrec private def messageLoop(consumer: DefaultConsumer): Unit = {
//    Try(consumer.nextDelivery()) match {
//      case Success(delivery) =>
//        val original = deserialise(delivery.getBody)
//        convertPictureActor ! ConvertPictureActor.Convert(original)
//        messageLoop(consumer)
//      case Failure(e: ShutdownSignalException) =>
//        // channel が close されたときに発生するため、正常系の一部
//        Logger.logger.info("RabbitMQの監視を終了します")
//      case Failure(NonFatal(e)) =>
//        Logger.logger.warn("RabbitMQの監視が中断されました", e)
//    }
//  }

  def close(): Unit = {
    println("closing...")
    channel.close()
    connection.close(5000)
  }

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

class RabbitMQConsumerModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[RabbitMQConsumer]).asEagerSingleton()
  }
}
