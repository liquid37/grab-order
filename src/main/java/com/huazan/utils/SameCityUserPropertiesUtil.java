package com.huazan.utils;

import java.io.*;
import java.util.Properties;

public class SameCityUserPropertiesUtil {

    private static Properties sameCityProperties = new Properties();

    static{
        try {
            //
            InputStream sameCityResource = new BufferedInputStream(new FileInputStream("./config/same_city.properties"));
            sameCityProperties.load(sameCityResource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key){
        return sameCityProperties.getProperty(key);
    }

    public static void write(String key,String value) throws Exception {
        sameCityProperties.setProperty(key,value);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("./config/same_city.properties"));
        sameCityProperties.store(outputStream,"");
    }

}
