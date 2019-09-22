package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtils {
    private static Properties props;

    static {
        loadProps();
    }

    public static String carFromTopic = props.getProperty("carFromTopic");
    public static String wifiFromTopic = props.getProperty("wifiFromTopic");
    public static String carToTopic = props.getProperty("carToTopic");
    public static String wifiToTopic = props.getProperty("wifiToTopic");
    public final static String WIFI_SUR_SQL = props.getProperty("wifi.sur.sql");
    public final static String CAR_SUR_SQL = props.getProperty("car.sur.sql");
    public final static String JDBC_DRIVER = props.getProperty("jdbc.driver");
    public final static String JDBC_URL = props.getProperty("jdbc.url");
    public final static String JDBC_USER = props.getProperty("jdbc.user");
    public final static String JDBC_PASSWORD = props.getProperty("jdbc.password");
    public final static boolean JDBC_IS_CLOSE_CONNECTION_WATCH = "true".equals(props.getOrDefault("jdbc.isCloseConnectionWatch","false"));

    private static void loadProps() {
        props = new Properties();
        try {
            props.load(new FileInputStream("/home/whs/surveillance/conf/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Properties getProducerConf(){
        Properties producerConf = new Properties();
        Set<String> propertyNames = props.stringPropertyNames();

        for (String propertyName : propertyNames) {
            if (propertyName.contains("kafka.producer.")){
                producerConf.setProperty(propertyName.replace("kafka.producer.",""),props.getProperty(propertyName));
            }
        }

        return producerConf;
    }

    public static Properties getConsumerConf(){
        Properties producerConf = new Properties();
        Set<String> propertyNames = props.stringPropertyNames();

        for (String propertyName : propertyNames) {
            if (propertyName.contains("kafka.consumer.")){
                producerConf.setProperty(propertyName.replace("kafka.consumer.",""),props.getProperty(propertyName));
            }
        }

        return producerConf;
    }

}

