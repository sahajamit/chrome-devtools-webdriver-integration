package com.sahajamit.demo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.sahajamit.messaging.CDTClient;
import com.sahajamit.messaging.MessageBuilder;
import com.sahajamit.messaging.ServiceWorker;
import com.sahajamit.utils.UIUtils;
import com.sahajamit.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.util.Base64;


public class DemoTest {
    private WebDriver driver;
    private WebSocket ws = null;
    private String wsURL;
    private String os;
    private Utils utils;
    private UIUtils uiUtils;
    public static void main(String[] args) throws Exception {
        DemoTest demoTest = new DemoTest();
//        demoTest.doFakeGeoLocation();
//        demoTest.doNetworkTracking();
//        demoTest.doResponseMocking();
//        demoTest.doFunMocking();
//        demoTest.doServerWorkerTesting();
        demoTest.doClearSiteData();
    }

    public DemoTest() {
        this.utils = Utils.getInstance();
        this.uiUtils = UIUtils.getInstance();
    }

    private void doFakeGeoLocation() throws IOException, WebSocketException, InterruptedException {
        driver = utils.launchBrowser();
        wsURL = utils.getWebSocketDebuggerUrl();
        CDTClient cdtClient = new CDTClient(wsURL);
        int id = Utils.getInstance().getDynamicID();
        cdtClient.sendMessage(MessageBuilder.buildGeoLocationMessage(id,"37.422290","-122.084057")); //google HQ
        utils.waitFor(1);
        driver.navigate().to("https://www.google.com.sg/maps");
        uiUtils.findElement(By.cssSelector("div.widget-mylocation-button-icon-common"),3).click();
        utils.waitFor(1);
        uiUtils.takeScreenShot();
        cdtClient.disconnect();
        utils.stopChrome();
    }

    private void doNetworkTracking() throws IOException, WebSocketException, InterruptedException {
        driver = utils.launchBrowser();
        wsURL = utils.getWebSocketDebuggerUrl();
        CDTClient cdtClient = new CDTClient(wsURL);
        int id = Utils.getInstance().getDynamicID();
        cdtClient.sendMessage(MessageBuilder.buildNetWorkEnableMessage(id));
        driver.navigate().to("http://petstore.swagger.io/v2/swagger.json");
        utils.waitFor(3);
        String message =cdtClient.getResponseMessage("Network.requestWillBeSent");
        JSONObject jsonObject = new JSONObject(message);
        String reqId = jsonObject.getJSONObject("params").getString("requestId");
        int id2 = Utils.getInstance().getDynamicID();
        cdtClient.sendMessage(MessageBuilder.buildGetResponseBodyMessage (id2,reqId));
        String networkResponse = cdtClient.getResponseBodyMessage(id2);
        utils.waitFor(1);
        cdtClient.disconnect();
        utils.stopChrome();
    }

    private void doResponseMocking() throws Exception {
        driver = utils.launchBrowser();
        wsURL = utils.getWebSocketDebuggerUrl();
        CDTClient cdtClient = new CDTClient(wsURL);
        int id = Utils.getInstance().getDynamicID();
        cdtClient.sendMessage(MessageBuilder.buildRequestInterceptorPatternMessage (id,"*","Document"));
        cdtClient.mockResponse("This is mocked!!!");
        driver.navigate().to("http://petstore.swagger.io/v2/swagger.json");
        utils.waitFor(3);
        cdtClient.disconnect();
        utils.stopChrome();
    }

    private void doFunMocking() throws Exception {
        byte[] fileContent = FileUtils.readFileToByteArray(new File(System.getProperty("user.dir") + "/data/cat.png"));
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        driver = utils.launchBrowser();
        wsURL = utils.getWebSocketDebuggerUrl();
        CDTClient cdtClient = new CDTClient(wsURL);
        int id = Utils.getInstance().getDynamicID();
        cdtClient.sendMessage(MessageBuilder.buildRequestInterceptorPatternMessage (id,"*","Image"));
        cdtClient.mockFunResponse(encodedString);
//        driver.navigate().to("https://www.seleniumhq.org");
        driver.navigate().to("https://sg.carousell.com/");
        utils.waitFor(300);
        cdtClient.disconnect();
        utils.stopChrome();
    }

    private void doServerWorkerTesting() throws Exception {
        String URL = "https://framework.realtime.co/demo/web-push";
//        String URL = "https://gauntface.github.io/simple-push-demo";
        driver = utils.launchBrowser();
        wsURL = utils.getWebSocketDebuggerUrl();
        CDTClient cdtClient = new CDTClient(wsURL);
        int id = Utils.getInstance().getDynamicID();
        cdtClient.sendMessage(MessageBuilder.buildServiceWorkerEnableMessage(id));
        driver.navigate().to(URL);
        utils.waitFor(2);
//        uiUtils.findElement(By.cssSelector("span.mdl-switch__ripple-container"),5).click();
        utils.waitFor(3);
        ServiceWorker serviceWorker = cdtClient.getServiceWorker(URL);
        String swURL = utils.getSWURL(wsURL,serviceWorker.getTargetId());
        CDTClient swcdtClient = new CDTClient(swURL);
        int id1 = Utils.getInstance().getDynamicID();
        int id2 = Utils.getInstance().getDynamicID();

        cdtClient.sendMessage(MessageBuilder.buildEnableLogMessage(id1));
        cdtClient.sendMessage(MessageBuilder.buildEnableRuntimeMessage(id2));

        cdtClient.sendMessage(MessageBuilder.buildServiceWorkerInspectMessage(id2,serviceWorker.getVersionId()));



//        swcdtClient.sendMessage(MessageBuilder.buildEnableLogMessage(id1));
//        swcdtClient.sendMessage(MessageBuilder.buildEnableRuntimeMessage(id2));

//        int id3 = Utils.getInstance().getDynamicID();
//        cdtClient.sendMessage(MessageBuilder.buildObserveBackgroundServiceMessage(id3));
//        int id4 = Utils.getInstance().getDynamicID();
//        swcdtClient.sendMessage(MessageBuilder.buildObserveBackgroundServiceMessage(id4));

        int id5 = Utils.getInstance().getDynamicID();
        WebElement elem = uiUtils.findElement(By.cssSelector("button#sendButton"),3);
        uiUtils.scrollToElement(elem);
        utils.waitFor(1);
        elem.click();
        utils.waitFor(2);
//        cdtClient.sendMessage(MessageBuilder.buildSendPushNotificationMessage(id5,URL,serviceWorker.getRegistrationId(),"Auto Push 1"));
//        cdtClient.sendMessage(MessageBuilder.buildSendPushNotificationMessage(id5,URL,serviceWorker.getRegistrationId(),"Auto Push 2"));
        utils.waitFor(60);
        Object o = uiUtils.executeJavaScript("return navigator.serviceWorker.ready.then(function(registration) {\n" +
                "              return registration.getNotifications().then(function(notifications) {\n" +
                "                  return notifications;\n" +
                "              })\n" +
                "          });");
        cdtClient.disconnect();
        utils.stopChrome();
    }

    private void doClearSiteData() throws Exception {
        String URL = "https://framework.realtime.co/demo/web-push";
        driver = utils.launchBrowser();
        wsURL = utils.getWebSocketDebuggerUrl();
        CDTClient cdtClient = new CDTClient(wsURL);
        driver.navigate().to(URL);
        int id = Utils.getInstance().getDynamicID();
        cdtClient.sendMessage(MessageBuilder.buildClearBrowserCookiesMessage(id));
        cdtClient.sendMessage(MessageBuilder.buildClearDataForOriginMessage(id,"https://framework.realtime.co"));
        utils.waitFor(3);
        cdtClient.disconnect();
        utils.stopChrome();
    }
}
