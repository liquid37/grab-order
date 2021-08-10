package com.huazan.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DingTalkPropertiesUtil {

    private static Properties dingTalkProperties = new Properties();

    static{
        try {
            InputStream sameCityResource = DingTalkPropertiesUtil.class.getResourceAsStream("/config/ding_talk.properties");
            dingTalkProperties.load(sameCityResource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key){
        return dingTalkProperties.getProperty(key);
    }

}
