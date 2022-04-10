package alfatto_task1_junior.server;

import com.alfatto.task1_junior.redis.RedisCacheImpl;
import com.sun.net.httpserver.HttpServer;
import alfatto_task1_junior.controller.HttpController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static alfatto_task1_junior.config.ServerConfig.*;


public class Server {
    private HttpServer server;

    public void start(){
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREADS_COUNT);
        try {
            server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
            server.createContext("/redis", new HttpController(new RedisCacheImpl()));
            server.setExecutor(threadPoolExecutor);
            server.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
