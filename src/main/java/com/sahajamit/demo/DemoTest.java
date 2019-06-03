package com.sahajamit.demo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.sahajamit.messaging.CDTClient;
import com.sahajamit.messaging.MessageBuilder;
import com.sahajamit.utils.UIUtils;
import com.sahajamit.utils.Utils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;


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

        demoTest.doResponseMocking();
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
}
