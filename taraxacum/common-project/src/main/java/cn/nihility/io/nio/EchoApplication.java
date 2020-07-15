package cn.nihility.io.nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * 实现要求：
 * 1、根据代码片段实现一个简单的SOCKET ECHO程序；
 * 2、接受到客户端连接后，服务端返回一个欢迎消息;
 * 3、接受到"bye"消息后， 服务端返回一个结束消息，并结束当前连接;
 * 4、支持通过telnet连接本服务端，并且可正常运行；
 * 5、注意代码注释书写。
 *
 */
public class EchoApplication {
    public static void main(String[] args) {
        int listenPort = 50000;
        EchoServer server = new EchoServer(listenPort);
        server.startService();

        EchoClient client = new EchoClient(listenPort);
        client.startService();
    }
}

class EchoServer {

    private EchoServerImpl echoServer;

    public EchoServer(int listenPort) {
        echoServer = new EchoServerImpl(listenPort);
    }

    public void startService() {
        new Thread(echoServer).start();
    }
}

class EchoClient {

    private EchoClientImpl echoClient;

    public EchoClient(int listenPort) {
        echoClient = new EchoClientImpl(listenPort);
    }

    public void startService() {
        new Thread(echoClient).start();
    }
}

class EchoServerImpl implements Runnable {

    private Selector selector; // 多路复用器，事件轮询器
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private Charset UTF8 = StandardCharsets.UTF_8;

    public EchoServerImpl(int listenPort) {
        try {
            // 获取一个 ServerSocketChannel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 设置 ServerSocket 为非阻塞
            serverSocketChannel.configureBlocking(false);
            // 绑定启动监听端口
            serverSocketChannel.socket().bind(new InetSocketAddress(listenPort));
            // 获取一个多路复用器
            this.selector = Selector.open();
            // 注册 Selector 到 Channel 中，监听 Accept 事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.out.println("启动 EchoServer 服务失败, " + e.getMessage());
            System.exit(1);
        }
    }

    public void startService() {
        // 循环监听
        for(;;) {
            try {
                // 监听到的事件数量
                int selectCount = selector.select();
                if (0 == selectCount) { continue; }

                // 获取事件集合，轮询事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keys = selectionKeys.iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    try {
                        handle(key);
                    } catch (Exception ex) {
                        /* 出错处理 key */
                        if (null != key) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                        ex.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 处理监听到的请求事件
     */
    private void handle(SelectionKey key) throws IOException {
        /* Key 是有效的 */
        if (key.isValid()) {
            // 处理连接事件
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept(); // 3 次握手
                sc.configureBlocking(false);

                // 返回信息
                if (sc.isConnected()) {
                    byte[] bytes = messageFormatToByteArray("Welcome to My Echo Server.");
                    ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
                    buffer.put(bytes);
                    buffer.flip();
                    sc.write(buffer);
                }

                // 连接建立完成，注册读取事件
                sc.register(selector, SelectionKey.OP_READ);
            }

            // 读事件
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                sc.configureBlocking(false);
                // 写 0， 1024
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int readBytes;

                while ((readBytes = sc.read(byteBuffer)) > 0) {
                    byteBuffer.flip(); // 计算 byte 位置信息
                    bos.write(byteBuffer.array(), 0, readBytes);
                    byteBuffer.clear();
                }

                if (bos.size() > 0) {
                    byte[] responseMsg = formatByteArrayToMessage(bos.toByteArray());
                    bos.close();

                    ByteBuffer buffer = ByteBuffer.allocate(responseMsg.length);
                    buffer.put(responseMsg);
                    buffer.flip();
                    sc.write(buffer);
                }

                if (readBytes < 0) {
                    /* 非法 key */
                    key.cancel();
                    sc.close();
                } else {
                    sc.register(selector, SelectionKey.OP_READ);
                }
            }
        }

    }

    private byte[] formatByteArrayToMessage(byte[] massage) {
        String timeFormat = dateTimeFormatter.format(LocalDateTime.now());
        return (timeFormat + " - " + new String(massage, UTF8) + " (from SERVER)").getBytes(UTF8);
    }

    private byte[] messageFormatToByteArray(String massage) {
        String timeFormat = dateTimeFormatter.format(LocalDateTime.now());
        return (timeFormat + " - " + massage + " (from SERVER)").getBytes(UTF8);
    }

    @Override
    public void run() {
        startService();
    }
}

class EchoClientImpl implements Runnable {

    private Selector selector; // 多路复用器，事件轮询器

    public EchoClientImpl(int listenPort) {
        try {
            // 获取一个客户端 socket
            SocketChannel socketChannel = SocketChannel.open();
            // 设置为非阻塞
            socketChannel.configureBlocking(false);
            // 获取一个多路复用器
            this.selector = Selector.open();
            // 注册  Selector 到 Channel
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            // 发起连接
            socketChannel.connect(new InetSocketAddress(listenPort));
        } catch (IOException e) {
            System.out.println("启动 EchoClient 失败, " + e.getMessage());
            System.exit(1);
        }
    }

    public void startService() {

        for (;;) {
            try {
                int selectCnt = selector.select(1000L);
                if (selectCnt == 0) { continue; }

                // 获取事件集合，轮询事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keys = selectionKeys.iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    try {
                        handle(key);
                    } catch (Exception ex) {
                        /* 出错处理 key */
                        if (null != key) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                        ex.printStackTrace();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handle(SelectionKey key) throws IOException {
        // 是否可连接
        if (key.isValid() && key.isConnectable()) {
            SocketChannel sc = (SocketChannel) key.channel();

            // 完成连接
            if (sc.isConnectionPending()) {
                sc.finishConnect();
                sc.register(selector, SelectionKey.OP_READ);
            } else {
                // 连接失败退出
                System.exit(1);
            }
        }

        // 读事件
        if (key.isValid() && key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            // 写 0， 1024
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int readBytes;

            while ((readBytes = sc.read(byteBuffer)) > 0) {
                byteBuffer.flip(); // 计算 byte 位置信息
                bos.write(byteBuffer.array(), 0, readBytes);
                byteBuffer.clear();
            }

            if (bos.size() > 0) {
                String readMsg = new String(bos.toByteArray(), StandardCharsets.UTF_8);
                bos.close();

                System.out.println(readMsg);
            }

            if (readBytes < 0) {
                /* 非法 key */
                key.cancel();
                sc.close();
            } else {
                // 监听 write 事件
                sc.register(selector, SelectionKey.OP_WRITE);
            }
        }

        // 写事件
        if (key.isValid() && key.isWritable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine();

            sc.configureBlocking(false);
            sc.write(StandardCharsets.UTF_8.encode(msg));
            // 监听 write 事件
            sc.register(selector, SelectionKey.OP_READ);
        }
    }

    @Override
    public void run() {
        startService();
    }
}