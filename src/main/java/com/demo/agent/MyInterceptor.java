package com.demo.agent;
 
 
import com.alibaba.fastjson.JSON;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MyInterceptor {
    private static final Logger LOGGER = Logger.getLogger(MyInterceptor.class.getName());
    public static void beforeRequest(Object request, Object response) {
        LOGGER.info("beforeRequest Intercepting HTTP request headers");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // 获取请求方法
        String method = httpServletRequest.getMethod();
//        System.out.println("request URI: " + httpServletRequest.getRequestURI());
//        System.out.println("request URL: " + httpServletRequest.getRequestURL());

        // 只处理 GET 和 POST 请求
        if ("GET".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method)) {
            Map<String, String> params = new HashMap<>();

            // 获取 GET 请求参数
            if ("GET".equalsIgnoreCase(method)) {
                params = getRequestParam(httpServletRequest);
            }
            // 获取 POST 请求参数
            else if ("POST".equalsIgnoreCase(method)) {
                params = getPostRequestParam(httpServletRequest);
            }

            // 构建包含请求类型、请求 URL 和请求参数的 JSON 数据
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("method", method);
            requestData.put("url", httpServletRequest.getRequestURL().toString());
            requestData.put("params", params);

            // 将数据转换为 JSON 格式
            String jsonData = JSON.toJSONString(requestData);
            LOGGER.info("Request Data: " + jsonData);

            // 将 JSON 数据保存到本地文件
            saveParamsToFile(jsonData);
        }

        // 设置响应头
//        httpServletResponse.addHeader("Interceptor", "Interceptor request");
//        httpServletResponse.setContentType("text/plain;charset=UTF-8");
    }

    private static Map<String, String> getRequestParam(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> params.put(key, value[0]));
        return params;
    }

    private static Map<String, String> getPostRequestParam(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            params.put("body", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    private static void saveParamsToFile(String jsonData) {
        try (FileWriter writer = new FileWriter("request_params.json", true)) {
            writer.write(jsonData + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}