package cn.nihility.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * NIOClient
 *
 * @author dandelion
 * @date 2020-03-19 14:59
 */
public class NIOClient {

    public static void main(String[] args) throws IOException {
        NIOClient nioClient = new NIOClient();
        nioClient.start();
    }

    /**
     * 启动
     */
    public void start() throws IOException {
        // 连接服务器
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));

        // 接受服务器响应
        // 新开线程，专门负责接收服务端响应数据
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        NioClientHandler handler = new NioClientHandler(selector);
        new Thread(handler).start();

        // 向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();

            if (msg != null && msg.length() > 0) {
                if ("stop".equals(msg)) {
                    handler.stop();
                    socketChannel.close();
                    selector.close();
                    break;
                } else {
                    socketChannel.write(StandardCharsets.UTF_8.encode(msg));
                }
            }
        }

    }

}
