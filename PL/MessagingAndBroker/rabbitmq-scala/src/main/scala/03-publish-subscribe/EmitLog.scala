import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel

object EmitLog {
    private val EXCHANGE_NAME = "logs"

    def main(args: Array[String]): Unit = {
        val factory = new ConnectionFactory
        factory.setHost("localhost")

        val connection = factory.newConnection()
        val channel = connection.createChannel()
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        
        val message = if (args.length < 1) { 
            "info: Hello World!"
        } else {
            args.mkString(" ")
        }

        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"))
        println(s" [x] Sent '$message'")
    }
}