package com.sahajamit;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.Scanner;


public class ChromeDevTools {

    WebDriver driver;
    WebSocket ws = null;
    final Object waitCoordinator = new Object();
    final int messageTimeoutInSecs = 5;

    public static void main(String[] args) throws IOException, WebSocketException, InterruptedException {
        ChromeDevTools chromeDevTools = new ChromeDevTools();
        chromeDevTools.launchBrowser();
    }

    private void launchBrowser() throws IOException, WebSocketException, InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments(Arrays.asList("--start-maximized"));
        options.setBinary("C:/Users/1531411/Desktop/TechnicalStuff/Chromium/latest/499951-63/chrome-win32/chrome.exe");

        DesiredCapabilities crcapabilities = DesiredCapabilities.chrome();
        crcapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        crcapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);


        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, System.getProperty("user.dir") + "/target/chromedriver.log");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, System.getProperty("user.dir") + "/driver/chromedriver.exe");
        ChromeDriverService service = new ChromeDriverService.Builder()
            .usingAnyFreePort()
            .withVerbose(true)
            .build();
        service.start();

        driver = new RemoteWebDriver(service.getUrl(),crcapabilities);
        String wsURL = this.getWebSocketDebuggerUrl();
        this.sendWSMessage(wsURL,this.buildGeoLocationMessage("27.1752868","78.040009"));
        this.sendWSMessage(wsURL,this.buildNetWorkEnableMessage());
        this.sendWSMessage(wsURL,this.buildBasicHttpAuthenticationMessage("admin","admin"));
        driver.navigate().to("https://maps.google.com");
        driver.navigate().to("https://the-internet.herokuapp.com/basic_auth");

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
                if(line.contains("DevTools request: http://localhost")){
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
            ws = new WebSocketFactory()
                    .createSocket(url)
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket ws, String message) {
                            System.out.println(message);
                            // Received a response. Print the received message.
                            if(new JSONObject(message).getInt("id")==messageId){
                                synchronized (waitCoordinator) {
                                    waitCoordinator.notifyAll();
                                }
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
    private String buildBasicHttpAuthenticationMessage(String username,String password){
        byte[] encodedBytes = Base64.encodeBase64(String.format("%s:%s",username,password).getBytes());
        String base64EncodedCredentials = new String(encodedBytes);
         String message = String.format("{\"id\":2,\"method\":\"Network.setExtraHTTPHeaders\",\"params\":{\"headers\":{\"Authorization\":\"Basic %s\"}}}",base64EncodedCredentials);
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

}
