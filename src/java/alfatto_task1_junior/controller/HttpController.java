package alfatto_task1_junior.controller;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import alfatto_task1_junior.redis.RedisCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class HttpController implements HttpHandler {


    private final RedisCache redisCache;

    public HttpController(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public void handle(HttpExchange httpExchange) {

        String methodURI = httpExchange
                .getRequestURI()
                .toString()
                .split("/")[2];

        if(methodURI.startsWith("get") && "GET".equals(httpExchange.getRequestMethod())) {
            handleGetRequest(httpExchange);
        } else if(methodURI.startsWith("set") && "POST".equals(httpExchange.getRequestMethod())) {
            handleSetRequest(httpExchange);
        }
        else if(methodURI.startsWith("del") && "DELETE".equals(httpExchange.getRequestMethod())) {
             handleDelRequest(httpExchange);
        }
        else if(methodURI.startsWith("keys") && "GET".equals(httpExchange.getRequestMethod())) {
             handleKeysRequest(httpExchange);
        }
    }

    private void handleGetRequest(HttpExchange httpExchange) {
        String key = httpExchange
                .getRequestURI()
                .toString()
                .split("\\?")[1]
                .split("=")[1];

        String value = redisCache.get(key);
        handleResponse(httpExchange, value.getBytes(StandardCharsets.UTF_8));
    }

    private void handleSetRequest(HttpExchange httpExchange) {
        String setStatus;
        try(InputStream inputStream = httpExchange.getRequestBody()){
            byte[] bytes = inputStream.readAllBytes();
            String[] command = new String(bytes).split(" ");
            redisCache.set(command[0], command[1]);
            setStatus = "OK";
        }
        catch (IOException exception){
            setStatus = "ERR";
            exception.printStackTrace();
        }
        handleResponse(httpExchange, setStatus.getBytes(StandardCharsets.UTF_8));
    }

    private void handleDelRequest(HttpExchange httpExchange) {
        String responseData;
        try(InputStream inputStream = httpExchange.getRequestBody()){
            byte[] bytes = inputStream.readAllBytes();
            String[] keys = new String(bytes).split(" ");
            int countDeleted = redisCache.del(keys);
            responseData = Integer.toString(countDeleted);
        }
        catch (IOException exception){
            responseData = "ERR";
            exception.printStackTrace();
        }
        handleResponse(httpExchange, responseData.getBytes(StandardCharsets.UTF_8));
    }

    private void handleKeysRequest(HttpExchange httpExchange) {
        String URI = httpExchange.getRequestURI().toString();
        String pattern = URI
                .substring(URI.indexOf("?"))
                .split("=")[1];

        String keys = String.join(" ", redisCache.keys(pattern));
        handleResponse(httpExchange, keys.getBytes(StandardCharsets.UTF_8));
    }

    private void handleResponse(HttpExchange httpExchange, byte[] responseData) {
        try(OutputStream outputStream = httpExchange.getResponseBody()){

            httpExchange.sendResponseHeaders(200, responseData.length);

            outputStream.write(responseData);
            outputStream.flush();
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }
}
