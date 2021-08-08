package com.huazan.utils;

import java.io.*;
import java.util.Properties;

public class DeepUserPropertiesUtil {

    private static Properties deepProperties = new Properties();

    static{
        try {
            InputStream deepUserResource = new BufferedInputStream(new FileInputStream("./config/deep.properties"));
            deepProperties.load(deepUserResource);
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
