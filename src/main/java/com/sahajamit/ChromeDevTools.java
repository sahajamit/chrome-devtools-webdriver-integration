package com.sahajamit;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;


public class ChromeDevTools {

    private WebDriver driver;
    private WebSocket ws = null;
    final Object waitCoordinator = new Object();
    final int messageTimeoutInSecs = 2;
    private String reqId = "";
    private String interceptionId = "";
    private int interceptionReqId;
    private String wsURL;


    public static void main(String[] args) throws IOException, WebSocketException, InterruptedException {
        ChromeDevTools chromeDevTools = new ChromeDevTools();
        chromeDevTools.launchBrowser();
    }

    private void launchBrowser() throws IOException, WebSocketException, InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments(Arrays.asList("--start-maximized"));
//        options.setBinary("<chromebinary path>");

        DesiredCapabilities crcapabilities = DesiredCapabilities.chrome();
        crcapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        crcapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, System.getProperty("user.dir") + "/target/chromedriver.log");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, System.getProperty("user.dir") + "/driver/chromedriver");
        ChromeDriverService service = new ChromeDriverService.Builder()
            .usingAnyFreePort()
            .withVerbose(true)
            .build();
        service.start();

        driver = new RemoteWebDriver(service.getUrl(),crcapabilities);
        wsURL = this.getWebSocketDebuggerUrl();
//        this.sendWSMessage(wsURL,this.buildGeoLocationMessage("27.1752868","78.040009"));  Agra
//        this.sendWSMessage(wsURL,this.buildGeoLocationMessage("37.422290","-122.084057"));  google HQ
        this.sendWSMessage(wsURL,this.buildNetWorkEnableMessage());
//        this.sendWSMessage(wsURL,this.buildBasicHttpAuthenticationMessage("admin","admin"));
//        this.sendWSMessage(wsURL,this.buildRequestInterceptorEnabledMessage());
        this.sendWSMessage(wsURL,this.buildRequestInterceptorPatternMessage("*","Document"));



//        driver.navigate().to("https://the-internet.herokuapp.com/basic_auth");
//        this.sendWSMessage(wsURL,this.buildBasicHttpAuthenticationMessage("admin","admin"));
//        driver.navigate().to("https://the-internet.herokuapp.com/basic_auth");

        driver.navigate().to("http://petstore.swagger.io/v2/swagger.json");

        this.sendWSMessage(wsURL,this.buildGetResponseBodyMessage(reqId));

//        driver.navigate().to("https://www.google.com.sg/maps");
        Thread.sleep(3000);
//        driver.findElement(By.cssSelector("div.widget-mylocation-button-icon-common")).click();

//        this.waitFor(5000);

        ws.disconnect();

        driver.close();
        driver.quit();

        service.stop();
    }

    private String getWebSocketDebuggerUrl() throws IOException {
        String webSocketDebuggerUrl = "";
        File file = new File(System.getProperty("user.dir") + "/target/chromedriver.log");
        try {

            Scanner sc = new Scanner(file);
            String urlString = "";
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(line.contains("DevTools HTTP Request: http://localhost")){
                    urlString = line.substring(line.indexOf("http"),line.length()).replace("/version","");
                    break;
                }
            }
            sc.close();

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = org.apache.commons.io.IOUtils.toString(reader);
            JSONArray jsonArray = new JSONArray(json);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("type").equals("page")){
                    webSocketDebuggerUrl = jsonObject.getString("webSocketDebuggerUrl");
                    break;
                }
            }
        }
        catch (FileNotFoundException e) {
            throw e;
        }
        if(webSocketDebuggerUrl.equals(""))
            throw new RuntimeException("webSocketDebuggerUrl not found");
        return webSocketDebuggerUrl;
    }

    private void sendWSMessage(String url,String message) throws IOException, WebSocketException, InterruptedException {
        JSONObject jsonObject = new JSONObject(message);
        final int messageId = jsonObject.getInt("id");
        if(ws==null){
            System.out.println("Starting the web socket connection");
            ws = new WebSocketFactory()
                    .createSocket(url)
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket ws, String message) {
                            System.out.println(message);
                            getRequestId(message);
                            getInterceptionId(message);
                            // Received a response. Print the received message.
                            if(new JSONObject(message).getInt("id")==messageId){
                                synchronized (waitCoordinator) {
                                    waitCoordinator.notifyAll();
                                }
                            }
                            if(new JSONObject(message).getInt("id")==interceptionReqId){
                                JSONObject jsonObject = new JSONObject(message);
                                String encodedBody = jsonObject.getJSONObject("result").getString("body");
                                String decodedBody = new String(Base64.decodeBase64(encodedBody));
                                System.out.println("Intercepted Response is:");
                                System.out.printf(decodedBody);
                                overRideResponse(decodedBody);
                            }
                        }
                    })
                    .connect();
        }
        ws.sendText(message);
        synchronized (waitCoordinator) {
            waitCoordinator.wait(messageTimeoutInSecs*1000);
        }
    }

    private void getRequestId(String message){
        if(!reqId.equalsIgnoreCase(""))
            return;
        try{
            JSONObject jsonObject = new JSONObject(message);
            String method = jsonObject.getString("method");
            if(method.equalsIgnoreCase("Network.requestWillBeSent")){
                reqId = jsonObject.getJSONObject("params").getString("requestId");
                System.out.println("Extracted Request ID is: " + reqId);
            }
        }catch (Exception e){
            throw new RuntimeException("Error in reading the message: ", e);
        }
    }

    private void getInterceptionId(String message){
        if(!interceptionId.equalsIgnoreCase(""))
            return;
        try{
            JSONObject jsonObject = new JSONObject(message);
            String method = jsonObject.getString("method");
            if(method.equalsIgnoreCase("Network.requestIntercepted")){
                interceptionId = jsonObject.getJSONObject("params").getString("interceptionId");
                System.out.println("Interception ID is: " + interceptionId);
                interceptionReqId = getDynamicID(100,1000);
                this.sendWSMessage(wsURL,this.buildGetResponseBodyForInterceptionMessage(interceptionReqId,interceptionId));
            }
        }catch (Exception e){
            throw new RuntimeException("Error in reading the message: ", e);
        }
    }

    private void overRideResponse(String response){
        try{
            response = "This is dummy response";
            this.sendWSMessage(wsURL,this.buildGetContinueInterceptedRequestMessage(interceptionReqId,interceptionId,response));
        }catch (Exception e){
            throw new RuntimeException("Error in sending the message: ", e);
        }

    }

    private String buildNetWorkEnableMessage(){
        String message = "{\"id\":1,\"method\":\"Network.enable\",\"params\":{\"maxTotalBufferSize\":10000000,\"maxResourceBufferSize\":5000000}}";
        System.out.println(message);
        return message;
    }

    private String buildGeoLocationMessage(String latitude, String longitude){
        String message = String.format("{\"id\":3,\"method\":\"Emulation.setGeolocationOverride\",\"params\":{\"latitude\":%s,\"longitude\":%s,\"accuracy\":100}}",latitude,longitude);
        System.out.println(message);
        return message;
    }

    private String buildRequestInterceptorEnabledMessage(){
        String message = String.format("{\"id\":4,\"method\":\"Network.setRequestInterception\",\"params\":{\"enabled\":true}}");
        System.out.println(message);
        return message;
    }

    private String buildRequestInterceptorPatternMessage(String pattern, String documentType){
        String message = String.format("{\"id\":5,\"method\":\"Network.setRequestInterception\",\"params\":{\"patterns\":[{\"urlPattern\":\"%s\",\"resourceType\":\"%s\",\"interceptionStage\":\"HeadersReceived\"}]}}",pattern,documentType);
        System.out.println(message);
        return message;
    }

    private String buildBasicHttpAuthenticationMessage(String username,String password){
        byte[] encodedBytes = Base64.encodeBase64(String.format("%s:%s",username,password).getBytes());
        String base64EncodedCredentials = new String(encodedBytes);
         String message = String.format("{\"id\":2,\"method\":\"Network.setExtraHTTPHeaders\",\"params\":{\"headers\":{\"Authorization\":\"Basic %s\"}}}",base64EncodedCredentials);
        System.out.println(message);
        return message;
    }

    private String buildGetResponseBodyMessage(String requestID){
        String message = String.format("{\"id\":45,\"method\":\"Network.getResponseBody\",\"params\":{\"requestId\":\"%s\"}}",requestID);
        System.out.println(message);
        return message;
    }

    private String buildGetResponseBodyForInterceptionMessage(int id, String interceptionId){
        String message = String.format("{\"id\":%s,\"method\":\"Network.getResponseBodyForInterception\",\"params\":{\"interceptionId\":\"%s\"}}",id,interceptionId);
        System.out.println(message);
        return message;
    }

    private String buildGetContinueInterceptedRequestMessage(int id, String interceptionId, String response){
        String encodedResponse = new String(Base64.encodeBase64(response.getBytes()));
        String message = String.format("{\"id\":%s,\"method\":\"Network.continueInterceptedRequest\",\"params\":{\"interceptionId\":\"%s\",\"rawResponse\":\"%s\"}}",id,interceptionId,encodedResponse);
        System.out.println(message);
        return message;
    }

    private void waitFor(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getDynamicID(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
