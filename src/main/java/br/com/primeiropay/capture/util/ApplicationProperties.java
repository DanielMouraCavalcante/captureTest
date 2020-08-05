package br.com.primeiropay.capture.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    private static Properties appProps;

    private ApplicationProperties() {
    }

    static {
        String appConfigPath = "classes/application.properties";
        appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String propertyName) {
        return appProps.getProperty(propertyName);
    }

}
