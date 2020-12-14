package xyz.geekweb.kafka.filetransfer;

//import util.properties packages

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//import simple producer packages
//import KafkaProducer packages
//import ProducerRecord packages

/**
 * @author luohao
 * @date 2019/10/31
 */
public class FileProducer {

    public static void main(String[] args) throws Exception{


        //Assign topicName to string variable
        String topicName = "topic2";

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers","192.168.1.9:9092");

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

                //If the request fails, the producer can automatically retry,
                props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "xyz.geekweb.kafka.filetransfer.FileMapSerializer");

        Producer<String, Map> producer = new KafkaProducer<>(props);

        Map<String, byte[]> messageMap = new HashMap<>();

        for(int i = 0; i < 1; i++){
//            producer.send(new ProducerRecord<String, String>(topicName,
//                    Integer.toString(i), Integer.toString(i)));
//

            messageMap.put("c:\\tmp\\abc1.txt", "aaabb中文bccc111".getBytes("utf-8"));
            messageMap.put("c:\\tmp\\abc2.txt", "aaabb中文bccc222".getBytes("utf-8"));
            producer.send(new ProducerRecord<String, Map>
                    ( topicName,Integer.toString(i), messageMap));
        }


        System.out.println("Message sent successfully");
        producer.close();
    }
}