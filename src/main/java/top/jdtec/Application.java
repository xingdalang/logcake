package top.jdtec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.jdtec.socket.SocketServer;

/**
 * SprintBootApplication
 */
@SpringBootApplication
public class Application {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        //起socket服务
        SocketServer server = new SocketServer();
        server.startSocketServer(8988);
    }
}