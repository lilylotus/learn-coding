package cn.nihility.io.nio2;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class MultiplexServer implements Runnable {

    // 多路复用器，事件轮询器
    private Selector selector;
    private static ServerSocketChannel serverSocketChannel;

    private volatile boolean run = true;

    public static void main(String[] args) {
        new Thread(new MultiplexServer(40000), "server").start();
    }

    public MultiplexServer(int port) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            // 设置非阻塞
            serverSocketChannel.configureBlocking(false);
            // 绑定地址和端口
            final ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));
            /* 多路复用器 */
            selector = Selector.open();
            // 注册 serverSocketChannel 到 多路复用器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Initialization Nio Server Success.");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void listen() {
        while (run) {
            try {
                // 阻塞
                int selectCnt = selector.select();
                if (selectCnt == 0) {
                    System.out.print(".");
                    continue;
                }
                System.out.println("\r\nselect count [" + selectCnt + "]");
            } catch (IOException ex) {
                System.out.println("Selector->select() error" + ex.getMessage());
                continue;
            }

            // 事件集合
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                // 移除事件，避免重复处理
                iterator.remove();

                try {
                    handleKey(key);
                } catch (Exception ex) {
                    System.out.println("handle key " + ex.getMessage());
                    if (null != key) {
                        key.cancel();
                        if (key.channel() != null) {
                            try {
                                key.channel().close();
                            } catch (IOException e) {
                                System.out.println("key channel close ex " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleKey(SelectionKey selectionKey) throws IOException {
        // 可连接
        if (selectionKey.isValid() && selectionKey.isAcceptable()) {
            handleConnection(selectionKey);
        }

        if (selectionKey.isValid() && selectionKey.isReadable()) {
            handleRead(selectionKey);
        }
    }

    private void handleConnection(SelectionKey selectionKey) throws IOException {
        System.out.println("handle connection");
        // ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
        // 三次握手
        SocketChannel sc = serverSocketChannel.accept();
        sc.configureBlocking(false);

        // 返回信息
        if (sc.isConnected()) {
            responseMessage(sc, "Connect Server Success.");
            System.out.println("Client connect success.");
        }

        sc.register(selector, SelectionKey.OP_READ);
    }

    private void handleRead(SelectionKey selectionKey) throws IOException {
        System.out.println("handle read");
        // 获取对应可读的 Chanel
        SocketChannel sc = (SocketChannel) selectionKey.channel();
        sc.configureBlocking(false);
        // 创建一个 Buffer 用户加载数据
        ByteBuffer readBuffer = ByteBuffer.allocate(256);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        while (sc.read(readBuffer) > 0) {
            // 切换到读取模式 position limit capacity
            readBuffer.flip();
            bao.write(readBuffer.array(), 0, readBuffer.remaining());
            readBuffer.clear();
        }

        if (bao.size() > 0) {
            String content = new String(bao.toByteArray(), StandardCharsets.UTF_8);
            bao.close();
            System.out.println("Read Client [" + content + "]");

            responseMessage(sc, content);

            if ("quit".equals(content)) {
                System.out.println("Client require close socket.");
                sc.close();
            } else {
                // 现在服务端就是持续读，没有 write 操作
                sc.register(selector, SelectionKey.OP_READ);
            }
        } else {
            // 小于 0 说明有问题，关闭 SelectionKey 和 channel
            System.out.println("Close The Connection In Question.");
            selectionKey.cancel();
            sc.close();
        }
    }

    private void responseMessage(SocketChannel socketChannel, String content) throws IOException {
        if (StringUtils.isNoneBlank(content)) {
            String echo = "Success rec [" + content + "]";
            System.out.println("To client [" + echo + "]");
            byte[] writeBytes = echo.getBytes(StandardCharsets.UTF_8);
            ByteBuffer writeBuffer = ByteBuffer.allocate(writeBytes.length);
            writeBuffer.put(writeBytes);
            // 切换到写模式
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
            System.out.println("response end.");
            /*socketChannel.write(ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8)));*/
        }
    }

    private void stop() {
        run = false;
    }

    @Override
    public void run() {
        listen();
    }

}
