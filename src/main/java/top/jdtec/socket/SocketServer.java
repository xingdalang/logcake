package top.jdtec.socket;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.alibaba.fastjson.JSON;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * nio socket服务端
 */
public class SocketServer {
    //解码buffer
    private Charset cs = Charset.forName("UTF-8");
    //接受数据缓冲区
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
    //发送数据缓冲区
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
    //选择器（叫监听器更准确些吧应该）
    private static Selector selector;

    /**
     * 启动socket服务，开启监听
     *
     * @param port
     * @throws IOException
     */
    public void startSocketServer(int port) {
        try {
        /*    //打开通信信道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            //获取套接字
            ServerSocket serverSocket = serverSocketChannel.socket();
            //绑定端口号
            serverSocket.bind(new InetSocketAddress(port));
*/
            ServerSocket o = new ServerSocket(8988);

            Socket accept = o.accept();
            ILoggingEvent event;
            Logger remoteLogger;
            ObjectInputStream ois = null;
            boolean closed = false;
            try {
            ois = new ObjectInputStream(accept.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                closed = true;
            }
            try {
                while (!closed) {
                    System.out.println("====");
                    event = (ILoggingEvent) ois.readObject();
                    System.out.println(JSON.toJSONString(event));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(accept!=null){
                accept.close();
            }


           /* //打开监听器
            selector = Selector.open();

            //将通信信道注册到监听器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //监听器会一直监听，如果客户端有请求就会进入相应的事件处理
            while (true) {
                selector.select();//select方法会一直阻塞直到有相关事件发生或超时
                Set<SelectionKey> selectionKeys = selector.selectedKeys();//监听到的事件
                for (SelectionKey key : selectionKeys) {
                    handle(key);
                }
                selectionKeys.clear();//清除处理过的事件
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 处理不同的事件
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handle(SelectionKey selectionKey) throws IOException, ClassNotFoundException {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        String requestMsg = "";
        int count = 0;
        if (selectionKey.isAcceptable()) {
            //每有客户端连接，即注册通信信道为可读
            serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            ObjectInputStream ois = null;

            socketChannel = (SocketChannel) selectionKey.channel();
            rBuffer.clear();
            count = socketChannel.read(rBuffer);

            //读取数据
            if (count > 0) {
                rBuffer.flip();
                requestMsg = String.valueOf(cs.decode(rBuffer).array());
            }
            String responseMsg = "已收到客户端的消息:" + requestMsg;
            System.out.println(responseMsg);
            ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(requestMsg.getBytes())));

            ILoggingEvent event = (ILoggingEvent) ois.readObject();
            System.out.println(JSON.toJSONString(event));

            //返回数据
            sBuffer = ByteBuffer.allocate(responseMsg.getBytes("UTF-8").length);
            sBuffer.put(responseMsg.getBytes("UTF-8"));
            sBuffer.flip();
            socketChannel.write(sBuffer);
            socketChannel.close();
        }
    }

}