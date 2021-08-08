package com.huazan.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DeepSystemPropertiesUtil {

    private static Properties deepProperties = new Properties();

    static{
        try {
            InputStream sameCityResource = DeepSystemPropertiesUtil.class.getResourceAsStream("/config/deep.properties");
            deepProperties.load(sameCityResource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key){
        return deepProperties.getProperty(key);
    }

}
