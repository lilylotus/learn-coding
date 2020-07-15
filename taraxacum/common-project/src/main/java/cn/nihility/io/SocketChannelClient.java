package cn.nihility.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * SocketChannelClient
 *
 * @author dandelion
 * @date 2020-03-19 12:00
 */
public class SocketChannelClient {

    final static String ADDRESS = "192.168.1.15";
    final static int PORT = 8080;

    public static void main(String[] args) {
        clientNIO();
    }

    private static void clientNIO() {
        SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            if (!socketChannel.connect(new InetSocketAddress(ADDRESS, PORT))) {
                // 不断地轮询连接状态，直到完成连接
                while (!socketChannel.finishConnect()) {
                    //在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性
                    //这里为了演示该方法的使用，只是一直打印"."
                    System.out.println(".");
                }
            }

            // 区分打印
            System.out.println();

            // 分别实例化用来读写的缓冲区
            ByteBuffer writeBuffer = ByteBuffer.wrap("Send Send Send Client".getBytes(StandardCharsets.UTF_8));
            ByteBuffer readBuffer = ByteBuffer.allocate("send".getBytes(StandardCharsets.UTF_8).length - 1);

            while (writeBuffer.hasRemaining()) {
                // 如果用来向通道中写数据的缓冲区中还有剩余的字节，则继续将数据写入信道
                socketChannel.write(writeBuffer);
            }

            Thread.sleep(10000);

            StringBuffer stringBuffer = new StringBuffer();
            // 如果read（）接收到-1，表明服务端关闭，抛出异常
            while ((socketChannel.read(readBuffer)) > 0) {
                readBuffer.flip();
                stringBuffer.append(new String(readBuffer.array(), 0, readBuffer.limit(), StandardCharsets.UTF_8));
                readBuffer.clear();
            }

            System.out.println("Client Receive = " + stringBuffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (socketChannel != null) {
                // 关闭信道
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void client() {

        SocketChannel socketChannel = null;
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false); // 配置为非阻塞方式
            socketChannel.connect(new InetSocketAddress(ADDRESS, PORT));

            if (socketChannel.finishConnect()) {
                int cnt = 0;
                while (true) {
                    TimeUnit.SECONDS.sleep(1);

                    String message = "I'm " + (++cnt) + "-th information from client.";

                    buffer.clear();
                    buffer.put(message.getBytes(Charset.forName("UTF-8")));
                    buffer.flip();

                    while (buffer.hasRemaining()) {
                        System.out.println(buffer);
                        socketChannel.write(buffer);
                    }

                    if (cnt == 5) { break; }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (null != socketChannel) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
