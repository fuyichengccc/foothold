package utils;

//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaUtils {

//    private static KafkaProducer<String, String> producer;
//    private static Properties props;

//    private static KafkaProducer<String, String> getProducer() {
//        if (producer == null) {
//            props = PropertiesUtils.getProducerConf();
//            producer = new KafkaProducer<>(props);
//        }
//        return producer;
//    }

    public static void sendCarAlarm(String message) {
//        producer = getProducer();
//        producer.send(new ProducerRecord<>(PropertiesUtils.carToTopic, message));
//        producer.flush();
    }

    public static void sendWifiAlarm(String message) {
//        producer = getProducer();
//        producer.send(new ProducerRecord<>(PropertiesUtils.wifiToTopic, message));
//        producer.flush();
    }


}
