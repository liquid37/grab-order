package com.huazan.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebDriverUtil {

    private static final int MAX_RETRY_TIME = 20;

    private static final int WAIT_TIME = 300;

    //判断是否存在元素
    public static WebElement isElementPresent(WebDriver driver,By by){
        return WebDriverUtil.getElement(driver,by);
    }

    public static WebElement getElement(WebDriver driver, By by){
        return getElement(driver, by,0);
    }

    public static List<WebElement> getElements(WebDriver driver, By by){
        return getElements(driver,by,0);
    }

    public static List<WebElement> getElementsFromElement(WebElement element,By by){
        return getElementsFromElement(element,by,0);
    }

    public static WebElement getElementFromElement(WebElement element,By by){
        return getElementFromElement(element,by,0);
    }

    private static WebElement getElementFromElement(WebElement element,By by,int retryTime){
        WebElement result = null;
        while (retryTime < MAX_RETRY_TIME){
            try {
                if(retryTime > 0) {
                    TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
                }
                result = element.findElement(by);
                if(result == null){
                    retryTime++;
                    continue;
                }
                break;
            }catch (Exception e){
                retryTime++;
                continue;
            }
        }

        return result;
    }

    private static List<WebElement> getElementsFromElement(WebElement element,By by,int retryTime){
        List<WebElement> elements = null;
        while (retryTime < MAX_RETRY_TIME){
            try {
                if(retryTime > 0) {
                    TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
                }
                elements = element.findElements(by);
                if(elements == null){
                    retryTime++;
                    continue;
                }
                break;
            }catch (Exception e){
                retryTime++;
                continue;
            }
        }

        return elements;
    }

    private static List<WebElement> getElements(WebDriver driver, By by,int retryTime){
        List<WebElement> elements = null;
        while (retryTime < MAX_RETRY_TIME){
            try {
                if(retryTime > 0) {
                    TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
                }
                elements = driver.findElements(by);
                if(elements == null){
                    retryTime++;
                    continue;
                }
                break;
            }catch (Exception e){
                retryTime++;
                continue;
            }
        }

        return elements;
    }

    private static WebElement getElement(WebDriver driver, By by,int retryTime){
        WebElement element = null;
        while (retryTime < MAX_RETRY_TIME){
            try {
                if(retryTime > 0) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                element = driver.findElement(by);
                if(element == null){
                    retryTime++;
                    continue;
                }
                break;
            }catch (Exception e){
                retryTime++;
                continue;
            }
        }
        return element;
    }


}
