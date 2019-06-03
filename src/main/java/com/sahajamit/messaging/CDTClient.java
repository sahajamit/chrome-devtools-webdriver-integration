package com.sahajamit.messaging;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.sahajamit.utils.SSLUtil;
import com.sahajamit.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;

public class CDTClient {
    private String wsUrl;
    private WebSocket ws = null;
    private WebSocketFactory factory;
    private BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<String>();
    public CDTClient(String wsURL){
        factory = new WebSocketFactory();
        SSLUtil.turnOffSslChecking(factory);
        factory.setVerifyHostname(false);
        this.wsUrl = wsURL;
    }

    private void connect() throws IOException, WebSocketException {
        if(Objects.isNull(ws)){
            System.out.println("Making the new WS connection to: " + wsUrl);
            ws = factory
                    .createSocket(wsUrl)
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket ws, String message) {
                            // Received a response. Print the received message.
                            System.out.println("Received this ws message: "+message);
                            blockingQueue.add(message);
                        }
                    })
                    .connect();
        }
    }

    public void sendMessage(String message) throws IOException, WebSocketException {
        if(Objects.isNull(ws))
            this.connect();
        System.out.println("Sending this ws message: " + message);
        ws.sendText(message);
    }

    public String getResponseMessage(String jsonPath, String expectedValue) throws InterruptedException {
        while(true){
            String message = blockingQueue.poll(5, TimeUnit.SECONDS);
            if(Objects.isNull(message))
                return null;
            DocumentContext parse = JsonPath.parse(message);
            String value = parse.read(jsonPath.trim()).toString();
            if(value.equalsIgnoreCase(expectedValue))
                return message;
        }
    }

    public String getResponseMessage(String methodName) throws InterruptedException {
        return getResponseMessage(methodName,5);
    }

    public String getResponseMessage(String methodName, int timeoutInSecs) throws InterruptedException {
        try{
            while(true){
                String message = blockingQueue.poll(timeoutInSecs, TimeUnit.SECONDS);
                if(Objects.isNull(message))
                    throw new RuntimeException(String.format("No message received with this method name : '%s'",methodName));
                JSONObject jsonObject = new JSONObject(message);
                try{
                    String method = jsonObject.getString("method");
                    if(method.equalsIgnoreCase(methodName)){
                        return message;
                    }
                }catch (JSONException e){
                    //do nothing
                }
            }
        }catch (Exception e1){
            throw e1;
        }
    }

    public String getResponseBodyMessage(int id) throws InterruptedException {
        try{
            while(true){
                String message = blockingQueue.poll(5, TimeUnit.SECONDS);
                if(Objects.isNull(message))
                    throw new RuntimeException(String.format("No message received with this id : '%s'",id));
                JSONObject jsonObject = new JSONObject(message);
                try{
                    int methodId = jsonObject.getInt("id");
                    if(id == methodId){
                        return jsonObject.getJSONObject("result").getString("body");
                    }
                }catch (JSONException e){
                    //do nothing
                }
            }
        }catch (Exception e1){
            throw e1;
        }
    }

    public void mockResponse(String mockMessage){
        new Thread(() -> {
            try{
                String message = this.getResponseMessage("Network.requestIntercepted",5);
                JSONObject jsonObject = new JSONObject(message);
                String interceptionId = jsonObject.getJSONObject("params").getString("interceptionId");
                int id = Utils.getInstance().getDynamicID();
                this.sendMessage(MessageBuilder.buildGetContinueInterceptedRequestMessage(id,interceptionId,mockMessage));
                return;
            }catch (Exception e){
                //do nothing
            }
        }).start();
    }

    public void disconnect(){
        ws.disconnect();
    }
}
