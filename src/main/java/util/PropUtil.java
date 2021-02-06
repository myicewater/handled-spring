package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropUtil {

    public String getPropertyByKey(String key){
        Properties properties = new Properties();
        InputStream resourceAsStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
//        ClassLoader classLoader = properties.getClass().getClassLoader();
//
//        InputStream resourceAsStream = classLoader.getResourceAsStream("application.properties");

        try {
            properties.load(resourceAsStream1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String property = properties.getProperty(key);
        return property;

    }

    public static void main(String[] args) {
        PropUtil propUtil = new PropUtil();
//        InputStream fileInputStream = new FileInputStream("d:\\code\\application.properties");
        InputStream resourceAsStream = PropUtil.class.getClassLoader().getResourceAsStream("application.properties");
//        InputStream resourceAsStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String basePackege = properties.getProperty("basePackege");
        System.out.println(basePackege);
    }
}
