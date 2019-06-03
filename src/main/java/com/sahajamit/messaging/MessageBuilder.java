package com.sahajamit.messaging;

import com.sahajamit.utils.Utils;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

public class MessageBuilder {
    public static String buildGeoLocationMessage(int id, String latitude, String longitude){
        String message = String.format("{\"id\":%s,\"method\":\"Emulation.setGeolocationOverride\",\"params\":{\"latitude\":%s,\"longitude\":%s,\"accuracy\":100}}",id,latitude,longitude);
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

//    private void getRequestId(String message){
//        if(!reqId.equalsIgnoreCase(""))
//            return;
//        try{
//            JSONObject jsonObject = new JSONObject(message);
//            String method = jsonObject.getString("method");
//            if(method.equalsIgnoreCase("Network.requestWillBeSent")){
//                reqId = jsonObject.getJSONObject("params").getString("requestId");
//                System.out.println("Extracted Request ID is: " + reqId);
//            }
//        }catch (Exception e){
//            throw new RuntimeException("Error in reading the message: ", e);
//        }
//    }
//
//    private void getInterceptionId(String message){
//        if(!interceptionId.equalsIgnoreCase(""))
//            return;
//        try{
//            JSONObject jsonObject = new JSONObject(message);
//            String method = jsonObject.getString("method");
//            if(method.equalsIgnoreCase("Network.requestIntercepted")){
//                interceptionId = jsonObject.getJSONObject("params").getString("interceptionId");
//                System.out.println("Interception ID is: " + interceptionId);
//                interceptionReqId = Utils.getInstance().getDynamicID();
//                this.sendWSMessage(wsURL,this.buildGetResponseBodyForInterceptionMessage(interceptionReqId,interceptionId));
//            }
//        }catch (Exception e){
//            throw new RuntimeException("Error in reading the message: ", e);
//        }
//    }
//
//    private void overRideResponse(String response){
//        try{
//            response = "This is dummy response";
//            this.sendWSMessage(wsURL,this.buildGetContinueInterceptedRequestMessage(interceptionReqId,interceptionId,response));
//        }catch (Exception e){
//            throw new RuntimeException("Error in sending the message: ", e);
//        }
//
//    }

    private String buildServiceWorkerEnableMessage(){
        String message = "{\"id\":1111,\"method\":\"ServiceWorker.enable\"}";
        System.out.println(message);
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





    private String buildSendPushNotificationMessage(String origin, String registrationId, String data){
        String message = String.format("{\"id\":123,\"method\":\"ServiceWorker.deliverPushMessage\",\"params\":{\"origin\":\"%s\",\"registrationId\":\"%s\",\"data\":\"%s\"}}",origin,registrationId,data);
        System.out.println(message);
        return message;
    }

    private String buildSendObservingPushMessage(){
        int id = Utils.getInstance().getDynamicID();
        String message = String.format("{\"id\":%d,\"method\":\"BackgroundService.clearEvents\",\"params\":{\"service\":\"backgroundFetch\"}}",id);
        System.out.println(message);
        return message;
    }

    private String buildEnableLogMessage(){
        int id = Utils.getInstance().getDynamicID();
        String message = String.format("{\"id\":%d,\"method\":\"Log.enable\"}",id);
        System.out.println(message);
        return message;
    }

    private String buildEnableRuntimeMessage(){
        int id = Utils.getInstance().getDynamicID();
        String message = String.format("{\"id\":%d,\"method\":\"Runtime.enable\"}",id);
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
