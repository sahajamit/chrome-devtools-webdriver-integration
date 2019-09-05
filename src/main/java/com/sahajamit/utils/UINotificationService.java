package com.sahajamit.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class UINotificationService {
    private static final Logger logger = LoggerFactory.getLogger(UINotificationService.class);
    private WebDriver driver;
    private static UINotificationService ourInstance = new UINotificationService();
    private static final String startJsScript = "window.notifications = []; window.DefaultNotification = window.Notification; (function () { function notificationCallback(title, opt) { console.log(\"notification title: \", title); console.log(\"notification body: \", opt.body); console.log(\"notification tag: \", opt.tag); console.log(\"notification icon: \", opt.icon); } const handler = { construct(target, args) { notificationCallback(...args); var notification = new target(...args); window.notifications.push(notification); return notification; } }; const ProxifiedNotification = new Proxy(Notification, handler); window.Notification = ProxifiedNotification; })();";
    private static final String stopJsScript = "window.notifications = []; window.Notification = window.DefaultNotification;";

    public static UINotificationService getInstance(WebDriver driver) {
        ourInstance.driver = driver;
        return ourInstance;
    }

    private UINotificationService() {
    }

    public void startListener(){
        UIUtils.getInstance().executeJavaScript(startJsScript);
    }

    public void stopListener(){
        UIUtils.getInstance().executeJavaScript(stopJsScript);
    }

    public boolean isNotificationPresent(Map<String,String> filter){
        ArrayList<Map> notifications = (ArrayList<Map>) UIUtils.getInstance().executeJavaScript("return window.notifications;");
        AtomicInteger ai = new AtomicInteger(0);
        AtomicBoolean ab = new AtomicBoolean(false);
        notifications.stream()
                .forEach(n->{
                    logger.info("Closing this UI Notification : " + n.toString());
                    String closeScript = String.format("window.notifications[%d].close();",ai.get());
                    ((JavascriptExecutor) driver).executeScript(closeScript);
                    ai.addAndGet(1);
                    if(matchNotification(n,filter)) {
                        ab.set(true);
                        logger.info("Notification found with this criteria: " + n.toString());
                        return;
                    }
                });
        return ab.get();
    }

    private boolean matchNotification(Map<String,String> actual, Map<String,String> expected){
        AtomicBoolean ab = new AtomicBoolean(true);
        expected.entrySet()
                .forEach(e->{
                    if(!actual.get(e.getKey()).equalsIgnoreCase(e.getValue())){
                        ab.set(false);
                        return;
                    }
                });
        return ab.get();
    }
}
