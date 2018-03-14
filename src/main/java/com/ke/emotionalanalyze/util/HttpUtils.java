package com.ke.emotionalanalyze.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpUtils {

    private static CloseableHttpClient httpClient = HttpClients.createDefault();
    private static HttpGet httpGet;


    //设置请求头
    public static void setHead(HttpGet httpGet){
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8");
        httpGet.setHeader("Connection","keep-alive");
        httpGet.setHeader("Accept-Encoding","gzip, deflate, sdch");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        httpGet.setHeader("Cache-Control","no-cache");
    }

    /**
     * 封装GET请求，返回HttpEntity
     */
    public static HttpEntity httpGet(String url){
        //封装GET请求
        httpGet = new HttpGet(url);
        //设置请求头
        HttpUtils.setHead(httpGet);
        //发送GET请求
        HttpEntity httpEntity = null;
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            //如果不是返回200则打印状态码
            if(response.getStatusLine().getStatusCode()!=200)
                System.out.println(response.getStatusLine().getStatusCode());
            //获取html实体
            httpEntity = response.getEntity();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("GET请求错误");
        }
        return httpEntity;
    }

    //获取HTML内容
    public static String getHtml(HttpEntity httpEntity){
        StringBuilder sb = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpEntity.getContent()));
            String temp;
            while((temp = reader.readLine())!=null){
                sb.append(temp);
            }
            httpEntity.getContent().close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
