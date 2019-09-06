package com.sahajamit;

import com.sahajamit.utils.UINotificationService;
import com.sahajamit.utils.UIUtils;
import com.sahajamit.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationTests {

    private WebDriver driver;
    private String wsURL;    private Utils utils;
    private UIUtils uiUtils;
    private ChromeDriverService chromeDriverService;

    @Before
    public void beforeTest(){
        this.utils = Utils.getInstance();
        this.uiUtils = UIUtils.getInstance();
    }

    @After
    public void afterTest(){
        utils.stopChrome();
        if(!Objects.isNull(chromeDriverService))
            chromeDriverService.stop();
    }
    @Test
    public void doWebNotificationTesting() throws Exception {
        driver = utils.launchBrowser();
        driver.navigate().to("https://pushjs.org/#");
        UINotificationService uiNotificationService = UINotificationService.getInstance(driver);
        uiNotificationService.startWebNotificationListener();
        driver.findElement(By.id("demo_button")).click();
        utils.waitFor(2);

        Map<String,String> notificationFilter = new HashMap<>();
        notificationFilter.put("title", "Hello world!");
        boolean flag = uiNotificationService.isNotificationPresent(notificationFilter,"web");
        uiNotificationService.stopWebNotificationListener();
    }

    @Test
    public void doWebPushNotificationTesting() throws Exception {
        driver = utils.launchBrowser();
        driver.navigate().to("https://framework.realtime.co/demo/web-push");
        UINotificationService uiNotificationService = UINotificationService.getInstance(driver);
        uiNotificationService.startPushNotificationListener();
        driver.findElement(By.cssSelector("#sendButton")).click();
        utils.waitFor(4);

        Map<String,String> notificationFilter = new HashMap<>();
        notificationFilter.put("title", "Web Push Notification");

        boolean flag = uiNotificationService.isNotificationPresent(notificationFilter,"push");
        uiNotificationService.stopPushNotificationListener();

    }

}
