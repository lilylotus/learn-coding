package cn.nihility.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * SocketChannelServer
 *
 * @author dandelion
 * @date 2020-03-19 12:08
 */
public class SocketChannelServer {

    final static int BUFFER_SIZE = 1024;
    final static int TIMEOUT = 3000;

    public static void main(String[] args) {
//        server();
        serverNIO();
    }

    private static void serverNIO() {
        selector();
    }

    private static void selector() {
        Selector selector = null;
        ServerSocketChannel ssc = null;

        try {
            selector = Selector.open();

            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(SocketChannelClient.PORT));
            ssc.configureBlocking(false);

            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                if (selector.select(TIMEOUT) == 0) {
                    System.out.println("=============");
                    continue;
                }

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key, selector);
                    }
                    if (key.isWritable()) {
                        handleWrite(key, selector);
                    }
                    if (key.isConnectable()) {
                        System.out.println("isConnectable is true");
                    }
                    keyIterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != selector) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ssc != null) {
                try {
                    ssc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void handleWrite(SelectionKey key, Selector selector) throws IOException {

        SocketChannel sc = (SocketChannel) key.channel();

        String msg = "server send data : " + Math.random();
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));

        while (byteBuffer.hasRemaining()) {
            sc.write(byteBuffer);
        }

        sc.register(selector, SelectionKey.OP_READ);
        System.out.println(msg);

    }

    private static void handleRead(SelectionKey key, Selector selector) throws IOException {

        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE * 4);
        String remoteAddress = sc.getRemoteAddress().toString();
        System.out.println("remote address = " + remoteAddress);

        int readSize = sc.read(byteBuffer);
        if (readSize < 0) { // 如果不关闭会一直产生 isReadable 这个消息
            key.cancel();
            sc.close();
        } else if (readSize > 0) {
            StringBuilder sb = new StringBuilder();
            while (readSize > 0) { // 确保读取完整
                byteBuffer.flip();
                sb.append(new String(byteBuffer.array(), 0, readSize, Charset.forName("UTF-8")));
                byteBuffer.clear(); // 每次清空 对应上面 flip()
                readSize = sc.read(byteBuffer);
            }
            System.out.println("server receive: " + sb.toString());

            sc.register(selector, SelectionKey.OP_WRITE);
        } else {
            // 0 字节，忽略
        }

    }

    private static void handleAccept(SelectionKey key) throws IOException {
        // 只负责监听，阻塞，管理，不发送、接收数据
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        // 就绪后的操作，刚到达的socket句柄
        SocketChannel socketChannel = ssChannel.accept();
        if (socketChannel != null) {
            socketChannel.configureBlocking(false);
            // 告知选择器关心的通道，准备好读数据
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        }

    }


    private static void server() {

        ServerSocket serverSocket = null;
        InputStream in = null;

        try {
            serverSocket = new ServerSocket(SocketChannelClient.PORT);
            int recvMsgSize;
            byte[] buffer = new byte[1024];

            while (true) {

                Socket socket = serverSocket.accept();
                SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
                System.out.println("Handling client address " + remoteSocketAddress);
                in = socket.getInputStream();
                while ((recvMsgSize = in.read(buffer)) != -1) {
                    byte[] tmp = new byte[recvMsgSize];
                    System.arraycopy(buffer, 0, tmp, 0, recvMsgSize);
                    System.out.println(new String(tmp, 0, recvMsgSize, Charset.forName("UTF-8")));
                }

                System.out.println("-------------------------------");

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != serverSocket) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
