package com.huazan.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SameCitySystemPropertiesUtil {

    private static Properties sameCityProperties = new Properties();

    static{
        InputStream sameCityResource = null;
        try {
            //
            //InputStream sameCityResource = new BufferedInputStream(new FileInputStream("./config/same_city.properties"));
            sameCityResource = SameCitySystemPropertiesUtil.class.getResourceAsStream("/config/same_city.properties");
            sameCityProperties.load(sameCityResource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sameCityResource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String get(String key){
        return sameCityProperties.getProperty(key);
    }


}
