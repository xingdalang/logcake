package top.jdtec.logBack;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 用来实现核心的业务代码
 * @version 1.0.0
 * @since 2017/5/25上午12:50
 */
// sharable 标识一个ChannelHandle可以被多个Channel安全的共享
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);
    /**
     * 每个传入的消息都要调用
     * @param context
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception{
        ByteBuf buf = (ByteBuf) msg;

       // 打印消息
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);


//        BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(req));

//        InputStream sbs = new ByteArrayInputStream(req);
        System.out.println(new String(req));



        ObjectInputStream ois = null;
        boolean closed = false;
        try {
//            ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(req)));
        } catch (Exception e) {
            e.printStackTrace();
            closed = true;
        }

        ILoggingEvent event;
        Logger remoteLogger;
        System.out.println("----");
        ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(req)));
        try {
            while (!closed) {
                System.out.println("====");
                // read an event from the wire
                event = (ILoggingEvent) ois.readObject();
                System.out.println(JSON.toJSONString(event));
            }
        } catch (java.io.EOFException e) {
            logger.info("Caught java.io.EOFException closing connection.");
        } catch (java.net.SocketException e) {
            logger.info("Caught java.net.SocketException closing connection.");
        } catch (IOException e) {
            logger.info("Caught java.io.IOException: " + e);
            logger.info("Closing connection.");
        } catch (Exception e) {
            logger.error("Unexpected exception. Closing connection.", e);
        }

      /*  if(bufferedInputStream!=null&&bufferedInputStream.available()<0){
            bufferedInputStream.close();
        }
*/
        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {
                logger.warn("Could not close connection.", e);
            } finally {
                ois = null;
            }
        }
//                context.write(1);
        }

    /**
     * 通知ChannelInboundHandler最后对channelRead()的调用是当前批量读取中的最后一条消息
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 将未决消息冲刷到远程节点, 并且关闭该Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//        ctx.flush();
    }

    /**
     * 在读取操作期间，有异常抛出时会调用
     * @param context
     * @param couse
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable couse) {
        couse.printStackTrace();
        context.close();
    }
}
