package com.huazan.utils;

import java.io.*;
import java.util.Properties;

public class CommonPropertiesUtil {

    private static Properties commonProperties = new Properties();

    static{
        try {
            //
            InputStream sameCityResource = new BufferedInputStream(new FileInputStream("./config/common.properties"));
            commonProperties.load(new InputStreamReader(sameCityResource, "utf-8"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key){
        return commonProperties.getProperty(key);
    }
}
