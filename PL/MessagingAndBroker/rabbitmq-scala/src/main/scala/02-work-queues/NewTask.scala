import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel

/**
 * https://www.rabbitmq.com/tutorials/tutorial-two-java.html
 */
object NewTask {
    val QUEUE_NAME = "hello"
    
    def main(args: Array[String]): Unit = {
        val factory = new ConnectionFactory
        factory.setHost("localhost")
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        val message = args.mkString(" ")
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes())
        System.out.println(" [x] Sent '" + message + "'")
    }
}