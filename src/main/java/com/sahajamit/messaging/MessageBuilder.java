package com.sahajamit.messaging;

import com.sahajamit.utils.Utils;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

public class MessageBuilder {
    public static String buildGeoLocationMessage(int id, double latitude, double longitude){
        Message msg = new Message(id,"Emulation.setGeolocationOverride");
        msg.addParam("latitude",latitude);
        msg.addParam("longitude",longitude);
        msg.addParam("accuracy",100);
        String message = msg.toJson();
//        String message = String.format("{\"id\":%s,\"method\":\"Emulation.setGeolocationOverride\",\"params\":{\"latitude\":%s,\"longitude\":%s,\"accuracy\":100}}",id,latitude,longitude);
        return message;
    }

    public static String buildGetResponseBodyMessage(int id, String requestID){
        String message = String.format("{\"id\":%s,\"method\":\"Network.getResponseBody\",\"params\":{\"requestId\":\"%s\"}}",id,requestID);
        return message;
    }

    public static String buildNetWorkEnableMessage(int id){
        String message = String.format("{\"id\":%s,\"method\":\"Network.enable\",\"params\":{\"maxTotalBufferSize\":10000000,\"maxResourceBufferSize\":5000000}}",id);
        return message;
    }

    public static String buildRequestInterceptorPatternMessage(int id, String pattern, String resourceType){
        String message = String.format("{\"id\":%s,\"method\":\"Network.setRequestInterception\",\"params\":{\"patterns\":[{\"urlPattern\":\"%s\",\"resourceType\":\"%s\",\"interceptionStage\":\"HeadersReceived\"}]}}",id,pattern,resourceType);
        return message;
    }

    public static String buildGetResponseBodyForInterceptionMessage(int id, String interceptionId){
        String message = String.format("{\"id\":%s,\"method\":\"Network.getResponseBodyForInterception\",\"params\":{\"interceptionId\":\"%s\"}}",id,interceptionId);
        return message;
    }

    public static String buildGetContinueInterceptedRequestMessage(int id, String interceptionId, String response){
        String encodedResponse = new String(Base64.encodeBase64(response.getBytes()));
        String message = String.format("{\"id\":%s,\"method\":\"Network.continueInterceptedRequest\",\"params\":{\"interceptionId\":\"%s\",\"rawResponse\":\"%s\"}}",id,interceptionId,encodedResponse);
        return message;
    }

    public static String buildGetContinueInterceptedRequestEncodedMessage(int id, String interceptionId, String encodedResponse){
        String message = String.format("{\"id\":%s,\"method\":\"Network.continueInterceptedRequest\",\"params\":{\"interceptionId\":\"%s\",\"rawResponse\":\"%s\"}}",id,interceptionId,encodedResponse);
        return message;
    }

    public static String buildServiceWorkerEnableMessage(int id){
        String message = String.format("{\"id\":%s,\"method\":\"ServiceWorker.enable\"}",id);
        return message;
    }

    public static String buildServiceWorkerInspectMessage(int id, String versionId){
        String message = String.format("{\"id\":%s,\"method\":\"ServiceWorker.inspectWorker\",\"params\":{\"versionId\":\"%s\"}}",id,"versionId");
        return message;
    }

    public static String buildEnableLogMessage(int id){
        String message = String.format("{\"id\":%d,\"method\":\"Log.enable\"}",id);
        return message;
    }

    public static String buildEnableRuntimeMessage(int id){
        String message = String.format("{\"id\":%d,\"method\":\"Runtime.enable\"}",id);
        return message;
    }

    public static String buildSendPushNotificationMessage(int id, String origin, String registrationId, String data){
        String message = String.format("{\"id\":%s,\"method\":\"ServiceWorker.deliverPushMessage\",\"params\":{\"origin\":\"%s\",\"registrationId\":\"%s\",\"data\":\"%s\"}}",id,origin,registrationId,data);
        return message;
    }

    public static String buildObserveBackgroundServiceMessage(int id){
        String message = String.format("{\"id\":%s,\"method\":\"BackgroundService.startObserving\",\"params\":{\"service\":\"%s\"}}",id,"pushMessaging");
        return message;
    }

    public static String buildGetBrowserContextMessage(int id){
        String message = String.format("{\"id\":%d,\"method\":\"Target.getBrowserContexts\"}",id);
        return message;
    }

    public static String buildClearBrowserCacheMessage(int id){
        String message = String.format("{\"id\":%d,\"method\":\"Network.clearBrowserCache\"}",id);
        return message;
    }

    public static String buildClearBrowserCookiesMessage(int id){
        String message = String.format("{\"id\":%d,\"method\":\"Network.clearBrowserCookies\"}",id);
        return message;
    }

    public static String buildClearDataForOriginMessage(int id, String url){
        String message = String.format("{\"id\":%s,\"method\":\"Storage.clearDataForOrigin\",\"params\":{\"origin\":\"%s\",\"storageTypes\":\"all\"}}",id,url);
        return message;
    }

    public static String buildTakeElementScreenShotMessage(int id, long x, long y, long height, long width, int scale){
        String message = String.format("{\"id\":%s,\"method\":\"Page.captureScreenshot\",\"params\":{\"clip\":{\"x\":%s,\"y\":%s,\"width\":%s,\"height\":%s,\"scale\":%s}}}",id,x,y,width,height,scale);
        return message;
    }
    public static String buildTakePageScreenShotMessage(int id){
        Message msg = new Message(id,"Page.captureScreenshot");
        String message = msg.toJson();
//        String message = String.format("{\"id\":%s,\"method\":\"Page.captureScreenshot\"}",id);
        return message;
    }



    private String buildRequestInterceptorEnabledMessage(){
        String message = String.format("{\"id\":4,\"method\":\"Network.setRequestInterception\",\"params\":{\"enabled\":true}}");
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



    private String buildSendObservingPushMessage(){
        int id = Utils.getInstance().getDynamicID();
        String message = String.format("{\"id\":%d,\"method\":\"BackgroundService.clearEvents\",\"params\":{\"service\":\"backgroundFetch\"}}",id);
        System.out.println(message);
        return message;
    }



    private String buildAttachToTargetMessage(String targetId){
        int id = Utils.getInstance().getDynamicID();
        String message = String.format("{\"id\":%d,\"method\":\"Target.attachToTarget\",\"params\":{\"targetId\":\"%s\"}}",id,targetId);
        System.out.println(message);
        return message;
    }
}
