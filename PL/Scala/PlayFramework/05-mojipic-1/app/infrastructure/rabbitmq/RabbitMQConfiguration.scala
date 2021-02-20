package infrastructure.rabbitmq

import javax.inject.Inject
import play.api.Configuration

class RabbitMQConfiguration @Inject() (
                                      configuration: Configuration
                                      ) {
  val OriginalPictureQueueName = "original_pictures"
  val HostName = configuration.getOptional[String]("rabbitmq.hostname")
     .getOrElse(throw new IllegalStateException("rabbitmq.hostname is not set."))

}
