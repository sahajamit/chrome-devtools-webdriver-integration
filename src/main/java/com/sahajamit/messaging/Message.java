package com.sahajamit.messaging;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Message {
    private int id;
    private String method;
    private Map<String,Object> params;

    public Message(int id, String method) {
        this.id = id;
        this.method = method;
    }

    public void addParam(String key, Object value){
        if(Objects.isNull(params))
            params = new HashMap<>();
        params.put(key,value);
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
