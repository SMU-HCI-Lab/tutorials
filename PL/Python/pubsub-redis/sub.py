# sub.py
import logging
import redis
from multiprocessing import Process


logging.basicConfig(level=logging.DEBUG)
redis_connection = redis.Redis(host='redis', port=6379)


def sub(name: str):
    pubsub = redis_connection.pubsub()
    pubsub.subscribe("broadcast")
    for message in pubsub.listen():
        if message.get("type") == "message":
            log_message = "{}: {}".format(name, message)
            logging.info(log_message)


if __name__ == "__main__":
    Process(target=sub, args=("reader1",)).start()
