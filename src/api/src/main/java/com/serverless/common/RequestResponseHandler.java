package com.serverless.common;

import java.util.HashMap;
import java.util.Map;

public class RequestResponseHandler {

    public static Map<String, String> createHeaders() {
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("X-Powered-By", "AWS Lambda & Serverless Govt & Version 2");
        headersMap.put("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        headersMap.put("Access-Control-Allow-Origin", "*");
        headersMap.put("Access-Control-Allow-Headers", "Content-Type");
        return headersMap;
    }
}
