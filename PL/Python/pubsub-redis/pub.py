# pub.py
import json
import redis

redis_connection = redis.Redis(host='redis', port=6379)


def pub():
    data = {
        "message": "Hello",
        "from": "Kotaro",
        "to": "Me"
    }
    redis_connection.publish("broadcast", json.dumps(data))
    return


if __name__ == "__main__":
    pub()
