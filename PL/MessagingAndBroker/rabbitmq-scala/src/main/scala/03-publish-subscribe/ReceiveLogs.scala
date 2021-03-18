import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback

object ReceiveLogs {
    private val EXCHANGE_NAME = "logs"

    @throws(classOf[Exception])
    def main(args: Array[String]) {
        val factory = new ConnectionFactory
        factory.setHost("localhost")
        val connection = factory.newConnection()
        val channel = connection.createChannel()

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout")

        val queueName = channel.queueDeclare().getQueue()
        channel.queueBind(queueName, EXCHANGE_NAME, "")

        println(" [*] Waiting for messages. To exit press CTRL+C")

        val deliverCallback: DeliverCallback = (consumerTag, delivery) => {
            val message = new String(delivery.getBody, "UTF-8")
            println(s" [x] Received '$message'")
        }
        channel.basicConsume(queueName, true, deliverCallback, consumerTag => {})
    }
}