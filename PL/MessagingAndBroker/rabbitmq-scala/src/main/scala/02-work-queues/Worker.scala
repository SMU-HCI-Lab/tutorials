import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback

/**
 * https://www.rabbitmq.com/tutorials/tutorial-two-java.html
 */
object Worker {
    val QUEUE_NAME = "hello"

    def main(args: Array[String]): Unit = {
        val factory = new ConnectionFactory
        factory.setHost("localhost")
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        println(" [x] Waiting for messages. To exit press CTRL+C")
   
        val deliverCallback: DeliverCallback = (consumerTag, delivery) => {
            val message = new String(delivery.getBody, "UTF-8")
            println(s" [x] Received '$message'")

            try { 
                doWork(message)
            } finally {
                println(" [x] Done")
            }
        }
        val autoAck = true
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag => {})
    }

    @throws(classOf[InterruptedException])
    def doWork(task: String): Unit = {
        for (ch <- task) {
            if (ch == '.') {
                Thread.sleep(1000)
            }
        }
    }
}