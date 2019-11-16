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
    private static final String startWebNotificationJsScript = "window.notifications = []; window.DefaultNotification = window.Notification; (function () { function notificationCallback(title, opt) { console.log(\"notification title: \", title); console.log(\"notification body: \", opt.body); console.log(\"notification tag: \", opt.tag); console.log(\"notification icon: \", opt.icon); } const handler = { construct(target, args) { notificationCallback(...args); var notification = new target(...args); window.notifications.push(notification); return notification; } }; const ProxifiedNotification = new Proxy(Notification, handler); window.Notification = ProxifiedNotification; })();";
    private static final String stopWebNotificationJsScript = "window.notifications = []; window.Notification = window.DefaultNotification;";

    private static final String startPushNotificationJsScript = "window.notificationsMap = Object.create(null); async function getServiceWorkerRegistration(){ window.myServiceWorkerRegistration = await navigator.serviceWorker.getRegistration(\"%s\"); return window.myServiceWorkerRegistration;}; async function getNotifications() { window.myNotifications = await window.myServiceWorkerRegistration.getNotifications();}; window.notificationListener = setInterval(async function() { console.log(\"checking for notifications...\"); await getServiceWorkerRegistration(); await getNotifications(); for(var key in window.myNotifications){ window.notificationsMap[window.myNotifications[key].tag] = window.myNotifications[key]; }; }, 2000);";
    private static final String stopPushNotificationJsScript = "clearInterval(window.notificationListener); window.notificationsMap = Object.create(null); window.notifications = [] ;";
    private static final String getPushNotificationsJsScript = "function getCollectedNotifications(){ window.notifications = [] ; var count = 0; for (var prop in window.notificationsMap) { count++; window.notifications.push(window.notificationsMap[prop]); } console.log(\"Total notifications count is: \" + count); return window.notifications;}; return getCollectedNotifications();";

    public static UINotificationService getInstance(WebDriver driver) {
        ourInstance.driver = driver;
        return ourInstance;
    }

    private UINotificationService() {
    }

    public void startWebNotificationListener(){
        UIUtils.getInstance().executeJavaScript(startWebNotificationJsScript);
    }

    public void startPushNotificationListener(String notificationServiceURL){
        UIUtils.getInstance().executeJavaScript(String.format(startPushNotificationJsScript,notificationServiceURL));
    }

    public void stopWebNotificationListener(){
        UIUtils.getInstance().executeJavaScript(stopWebNotificationJsScript);
    }
    public void stopPushNotificationListener(){
        UIUtils.getInstance().executeJavaScript(stopPushNotificationJsScript);
    }


    public boolean isNotificationPresent(Map<String,String> filter, String notificationType){
        ArrayList<Map> notifications;
        if(notificationType.equalsIgnoreCase("web"))
            notifications = (ArrayList<Map>) UIUtils.getInstance().executeJavaScript("return window.notifications;");
        else
            notifications = (ArrayList<Map>) UIUtils.getInstance().executeJavaScript(getPushNotificationsJsScript);

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
