package com.sahajamit.messaging;

import com.sahajamit.utils.Utils;

import org.apache.commons.codec.binary.Base64;

import static java.lang.System.err;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import com.google.gson.Gson;

public class MessageBuilder {

    private static String method = null;
    private static Message message = null;
    private static Map<String, Object> params = new HashMap<>();
    private static Map<String, Object> paramDetails = new HashMap<>();

    private static String buildMessage(int id, String method) {
        return (new Message(id, method)).toJson();
    }

    private static String buildMessage(int id, String method,
            Map<String, Object> params) {
        message = new Message(id, method);
        for (String key : params.keySet()) {
            message.addParam(key, params.get(key));
        }
        return message.toJson();
    }

    public static String buildGeoLocationMessage(int id, double latitude, double longitude){
        method = "Emulation.setGeolocationOverride";
        params = new HashMap<>();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("accuracy", 100);
        return buildMessage(id, method, params);
        // return
        // String.format("{\"id\":%s,\"method\":\"Emulation.setGeolocationOverride\",\"params\":{\"latitude\":%s,\"longitude\":%s,\"accuracy\":100}}",id,latitude,longitude);
    }

    public static String buildGetResponseBodyMessage(int id, String requestID){
        method = "Network.getResponseBody";
        params = new HashMap<>();
        params.put("requestId", requestID);
        return buildMessage(id, method, params);
        /*
        return String.format("{\"id\":%s,\"method\":\"Network.getResponseBody\"," +
          "\"params\":{\"requestId\":\"%s\"}}", id, requestID);
        */
    }

    public static String buildNetWorkEnableMessage(int id){      
        method = "Network.enable";
        params = new HashMap<>();
        params.put("maxTotalBufferSize", 10000000);
        params.put("maxResourceBufferSize", 5000000);
        return buildMessage(id, method, params);
        /*
        return String.format(
                "{\"id\":%s,\"method\":\"Network.enable\",\"params\":{\"maxTotalBufferSize\":10000000,\"maxResourceBufferSize\":5000000}}",
                id);
                */
    }

    public static String buildRequestInterceptorPatternMessage(int id, String pattern, String resourceType){
        method = "Network.setRequestInterception";
        params = new HashMap<>();
        paramDetails = new HashMap<>();
        paramDetails.put("urlPattern", pattern);
        paramDetails.put("resourceType", resourceType);
        paramDetails.put("interceptionStage", "HeadersReceived");

        List<Map<String, Object>> patterns = new ArrayList<>();
        patterns.add(paramDetails);
        params.put("patterns", patterns);
        return buildMessage(id, method, params);
        /*
        return String.format(
          "{\"id\":%s," +
          "\"method\":\"Network.setRequestInterception\"," +
          "\"params\":{\"patterns\":[{\"urlPattern\":\"%s\",\"resourceType\":\"%s\",\"interceptionStage\":\"HeadersReceived\"}]}}",
          id, pattern, resourceType);
        */
    }

    public static String buildGetResponseBodyForInterceptionMessage(int id, String interceptionId){
        method = "Network.getResponseBodyForInterception";
        params = new HashMap<>();
        params.put("interceptionId", interceptionId);
        return buildMessage(id, method, params);
        /*
        return String.format(
          "{\"id\":%s,\"method\":\"Network.getResponseBodyForInterception\"," + 
          "\"params\":{\"interceptionId\":\"%s\"}}",
          id, interceptionId);
         */
    }

    public static String buildGetContinueInterceptedRequestMessage(int id, String interceptionId, String response){
        params = new HashMap<>();
        params.put("interceptionId", interceptionId);
        params.put("rawResponse",
                new String(Base64.encodeBase64(response.getBytes())));
        return buildMessage(id, method, params);
        /*
        return String.format(
          "{\"id\":%s,\"method\":\"Network.continueInterceptedRequest\","+
          "\"params\":{\"interceptionId\":\"%s\",\"rawResponse\":\"%s\"}}",
          id, interceptionId, new String(Base64.encodeBase64(response.getBytes())));
         */
    }

    public static String buildGetContinueInterceptedRequestEncodedMessage(int id, String interceptionId, String encodedResponse){
        String message = String.format("{\"id\":%s,\"method\":\"Network.continueInterceptedRequest\",\"params\":{\"interceptionId\":\"%s\",\"rawResponse\":\"%s\"}}",id,interceptionId,encodedResponse);
        return message;
    }

    public static String buildServiceWorkerEnableMessage(int id) {
        return buildMessage(id, "ServiceWorker.enable");
        /*
        return String
                .format("{\"id\":%s,\"method\":\"ServiceWorker.enable\"}", id);
        */
    }

    public static String buildServiceWorkerInspectMessage(int id, String versionId){
        String message = String.format(
                "{\"id\":%s,\"method\":\"ServiceWorker.inspectWorker\",\"params\":{\"versionId\":\"%s\"}}",
                id, "versionId");
        return message;
    }

    public static String buildEnableLogMessage(int id){
        return buildMessage(id, "Log.enable");
        /*
        return String.format("{\"id\":%d,\"method\":\"Log.enable\"}", id);
        */
    }

    public static String printPDFMessage(int id){
        method = "Page.printToPDF";
        params = new HashMap<>();
        params.put("landscape", false);
        params.put("displayHeaderFooter", false);
        params.put("printBackground", true);
        params.put("preferCSSPageSize", true);
        return buildMessage(id, method, params);

        /*
         return String.format(
          "{\"id\":%d,\"method\":\"Page.printToPDF\", \"params\":{\"landscape\":%b,\"displayHeaderFooter\":%b,\"printBackground\":%b,\"preferCSSPageSize\":%b}}",
          id, false, false, true, true);
         */
        // NOTE: {"error":{"code":-32602,"message":"Invalid parameters"
        // ,"data":"landscape: boolean value expected...
    }

    public static String buildEnableRuntimeMessage(int id){
        return buildMessage(id, "Runtime.enable");
        /*
        return String.format("{\"id\":%d,\"method\":\"Runtime.enable\"}", id);
        */
    }

    public static String buildSendPushNotificationMessage(int id, String origin, String registrationId, String data){
        method = "ServiceWorker.deliverPushMessage";
        params = new HashMap<>();
        params.put("origin", origin);
        params.put("registrationId", registrationId);
        params.put("data", data);
        return buildMessage(id, method, params);
        /*
         return String.format(
          "{\"id\":%s,\"method\":\"ServiceWorker.deliverPushMessage\","+
          "\"params\":{\"origin\":\"%s\",\"registrationId\":\"%s\",\"data\":\"%s\"}}",
          id, origin, registrationId, data);
        */
    }

    public static String buildObserveBackgroundServiceMessage(int id){
        method = "BackgroundService.startObserving";
        params = new HashMap<>();
        params.put("service", "pushMessaging");
        return buildMessage(id, method, params);
        /*
         return String.format(
                "{\"id\":%s,\"method\":\"BackgroundService.startObserving\"," + 
                "\"params\":{\"service\":\"%s\"}}",
                id, "pushMessaging");
        */
    }

    public static String buildGetBrowserContextMessage(int id){
        return buildMessage(id, "Target.getBrowserContexts");
        /*
          return String.format("{\"id\":%d,\"method\":\"Target.getBrowserContexts\"}", id);
        */
    }

    public static String buildClearBrowserCacheMessage(int id){
        return buildMessage(id, "Network.clearBrowserCache");
        /*
            return String.format("{\"id\":%d,\"method\":\"Network.clearBrowserCache\"}", id);
        */
    }

    public static String buildClearBrowserCookiesMessage(int id){
        return buildMessage(id, "Network.clearBrowserCookies");
        /*
            return String.format("{\"id\":%d,\"method\":\"Network.clearBrowserCookies\"}", id);
        */
    }

    public static String buildClearDataForOriginMessage(int id, String url){
        method = "Storage.clearDataForOrigin";
        params = new HashMap<>();
        params.put("url", url);
        params.put("storageTypes", "all");
        return buildMessage(id, method, params);
        /*
        return String.format(
                "{\"id\":%s,\"method\":\"Storage.clearDataForOrigin\"," +
                "\"params\":{\"origin\":\"%s\",\"storageTypes\":\"all\"}}", id, url);
            */
    }

    public static String buildTakeElementScreenShotMessage(int id, long x, long y, long height, long width, int scale){
        method = "Page.captureScreenshot";
        params = new HashMap<>();
        paramDetails = new HashMap<>();
        paramDetails.put("x", x);
        paramDetails.put("y", y);
        paramDetails.put("height", height);
        paramDetails.put("width", width);
        paramDetails.put("scale", 100);
        params.put("clip", paramDetails);
        return buildMessage(id, method, params);
        /*
        return String.format(
                "{\"id\":%s,\"method\":\"Page.captureScreenshot\"," + 
                "\"params\":{\"clip\":{\"x\":%s,\"y\":%s,\"width\":%s,\"height\":%s,\"scale\":%s}}}",
                id, x, y, width, height, scale);
        */
    }

    public static String buildTakePageScreenShotMessage(int id) {
        return buildMessage(id, "Page.captureScreenshot");
    }

    private String buildRequestInterceptorEnabledMessage() {
        String method = "Network.setRequestInterception";
        int id = 4;
        message = new Message(id, method);
        params = new HashMap<>();
        params.put("enabled", true);
        return buildMessage(id, method, params);
        /*
        return String.format(
                "{\"id\":4,"+ 
                "\"method\":\"Network.setRequestInterception\"," + 
                "\"params\":{\"enabled\":true}}");
        */
    }

    private String buildBasicHttpAuthenticationMessage(String username,String password){
        byte[] encodedBytes = Base64.encodeBase64(String.format("%s:%s", username, password).getBytes());
        String base64EncodedCredentials = new String(encodedBytes);
        String method = "Network.setExtraHTTPHeaders";
        int id = 2;
        params = new HashMap<>();
        paramDetails = new HashMap<>();
        paramDetails.put("Authorization",
                String.format("Basic %s", base64EncodedCredentials));
        params.put("headers", paramDetails);
        return buildMessage(id, method, params);
        /*
        return String.format( "{\"id\":2,\"method\":\"Network.setExtraHTTPHeaders\"," +
            "\"params\":{\"headers\":{\"Authorization\":\"Basic %s\"}}}",
                base64EncodedCredentials);
        */
    }

    private String buildSendObservingPushMessage(){
        String method = "BackgroundService.clearEvents";
        message = new Message(Utils.getInstance().getDynamicID(), method);
        params = new HashMap<>();
        params.put("service", "backgroundFetch");
        return buildMessage(Utils.getInstance().getDynamicID(), method, params);
        /*
            return String.format( "{\"id\":%d,\"method\":\"BackgroundService.clearEvents\"," +
                "\"params\":{\"service\":\"backgroundFetch\"}}", Utils.getInstance().getDynamicID());
        */
    }

    private String buildAttachToTargetMessage(String targetId){

        String method = "BackgroundService.clearEvents";
        message = new Message(Utils.getInstance().getDynamicID(), method);
        params = new HashMap<>();
        params.put("targetId", targetId);
        return buildMessage(Utils.getInstance().getDynamicID(), method, params);
        /*
            return String.format("{\"id\":%d,\"method\":\"Target.attachToTarget\"," +
                "\"params\":{\"targetId\":\"%s\"}}", 
                Utils.getInstance().getDynamicID(), targetId);
        */
    }
}
