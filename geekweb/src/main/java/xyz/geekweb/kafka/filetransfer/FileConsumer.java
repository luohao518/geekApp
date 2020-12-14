package xyz.geekweb.kafka.filetransfer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

public class FileConsumer {
    public static void main(String[] args) throws Exception {

        //Kafka consumer configuration settings
        String topicName = "topic2";
        Properties props = new Properties();

        props.put("bootstrap.servers", "192.168.1.9:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");

        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "xyz.geekweb.kafka.filetransfer.FileMapDeserializer");
        KafkaConsumer<String, Map<String,byte[]>> consumer = new KafkaConsumer<>(props);

        //Kafka Consumer subscribes list of topics here.
        consumer.subscribe(Arrays.asList(topicName));

        //print the topic name
        System.out.println("Subscribed to topic " + topicName);
        int i = 0;

        while (true) {
            ConsumerRecords<String, Map<String,byte[]>> records = consumer.poll(1000);
            for (ConsumerRecord<String, Map<String,byte[]>> record : records) {

                // print the offset,key and value for the consumer records.
                System.out.printf("offset = %d, key = %s, value = %s\n",
                        record.offset(), record.key(), record.value().toString());
                byte[] bytes1 = record.value().get("c:\\tmp\\abc1.txt");
                byte[] bytes2 = record.value().get("c:\\tmp\\abc2.txt");
                System.out.println(new String(bytes1,"utf-8"));
                System.out.println(new String(bytes2,"utf-8"));
                System.out.println("end");
            }
        }
    }
}
