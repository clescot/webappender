package com.clescot.webappender;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;

public class HttpBridgeLimitDecorator implements HttpBridge {

    private HttpBridge httpBridge;
    private int limit = 0;
    private int current = 0;

    public HttpBridgeLimitDecorator(HttpBridge httpBridge) {
        this.httpBridge = httpBridge;
        Optional<List<String>> optionalLimit = Optional.fromNullable(httpBridge.getHeadersAsMap().get("x-wa-limit"));

        if(optionalLimit.isPresent()&&optionalLimit.get().get(0)!=null){
            limit = Integer.parseInt(optionalLimit.get().get(0));
        }

    }

    @Override
    public void start() {
        httpBridge.start();
    }

    @Override
    public void finish() {
        httpBridge.finish();
    }

    @Override
    public boolean serializeLogs(String key, String value) {
        if(limit==0 ||limit>current){
            current +=value.length();
            httpBridge.serializeLogs(key,value);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Map<String, List<String>> getHeadersAsMap() {
        return httpBridge.getHeadersAsMap();
    }
}
