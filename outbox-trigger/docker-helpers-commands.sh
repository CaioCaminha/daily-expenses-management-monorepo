
#retrieves messages for given topic specifying partition and offset
docker exec -it kafka-java-daily-expenses /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic outbox-trigger \
  --partition 0 \
  --offset 0 \
  --max-messages 5


#get the partition for given topic
docker exec -it kafka-java-daily-expenses find /var/lib/kafka/data -type d -name "outbox-trigger-0"

#partition where kafka container is storing logs
tmp/kafka-logs/outbox-trigger-0

#folder where kafka stores server settings
etc/kafka/docker/ -- server.properties