package cn.nihility.io.nio2;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class MultiplexClient implements Runnable {

    private SocketChannel socketChannel;
    // 多路复用器，事件轮询器
    private Selector selector;
    private volatile boolean run = true;

    public static void main(String[] args) {
        new Thread(new MultiplexClient(40000), "client012").start();
    }

    public MultiplexClient(int port) {
        try {
            /*InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
            socketChannel = SocketChannel.open(inetSocketAddress);*/

            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();

            // 注册 selector 到 Channel, 监听 Connect 事件
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            // 发起连接
            socketChannel.connect(new InetSocketAddress(port));

            System.out.println("Initialization Multiplex Client Success.");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        while (run) {
            try {
                int selectCnt = selector.select(500L);
                if (selectCnt == 0) {
                    continue;
                }
                System.out.println("\r\nselect count [" + selectCnt + "]");
            } catch (IOException ex) {
                System.out.println("Selector->select() error " + ex.getMessage());
                continue;
            }

            // 事件集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            if (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    handleKey(key);
                } catch (Exception ex) {
                    if (null != key) {
                        key.cancel();
                        if (key.channel() != null) {
                            try {
                                key.channel().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socketChannel != null) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleKey(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isValid() && selectionKey.isConnectable()) {
            handleConnection(selectionKey);
        }

        if (selectionKey.isValid() && selectionKey.isReadable()) {
           handleRead(selectionKey);
        }

        if (selectionKey.isValid() && selectionKey.isWritable()) {
           handleWrite(selectionKey);
        }
    }

    private void handleConnection(SelectionKey selectionKey) throws IOException {
        System.out.println("handle connection");
        SocketChannel sc = (SocketChannel) selectionKey.channel();
        sc.configureBlocking(false);

        // 完成连接
        if (sc.isConnectionPending()) {
            System.out.println("Connection is Pending");
            if (sc.finishConnect()) {
                System.out.println("Finished Connection.");
                /*doResponseContent(sc, "Connect To Multiplex Server.");*/
                sc.register(selector, SelectionKey.OP_READ);
            } else {
                System.out.println("Connection To Multiplex Server Error.");
                System.exit(1);
            }
        }
    }

    private void handleRead(SelectionKey selectionKey) throws IOException {
        System.out.println("handle read");
        // 获取对应可读的 Chanel
        final SocketChannel sc = (SocketChannel) selectionKey.channel();
        sc.configureBlocking(false);

        // 创建一个 Buffer 用户加载数据
        final ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        final ByteArrayOutputStream bao = new ByteArrayOutputStream();
        int readByteLen;
        while ((readByteLen = sc.read(readBuffer)) > 0) {
            // 切换到读取模式 position limit capacity
            readBuffer.flip();
            bao.write(readBuffer.array(), 0, readByteLen);
            readBuffer.clear();
        }
        if (bao.size() > 0) {
            String content = new String(bao.toByteArray(), StandardCharsets.UTF_8);
            bao.close();
            System.out.println("Read Server [" + content + "]");
        }

        if (readByteLen < 0) {
            // 小于 0 说明有问题，关闭 SelectionKey 和 channel
            System.out.println("Close The Connection In Question.");
            selectionKey.cancel();
            sc.close();
        } else {
            sc.register(selector, SelectionKey.OP_WRITE);
        }
    }

    private void handleWrite(SelectionKey selectionKey) throws IOException {
        System.out.println("handle write");
        SocketChannel sc = (SocketChannel) selectionKey.channel();

        Scanner scanner = new Scanner(System.in);
        String content = scanner.nextLine();
        System.out.println("input [" + content + "]");
        doResponseContent(sc, content);

        if ("quit".equals(content)) {
            System.out.println("Exit Client Application.");
            stop();
        }

        sc.register(selector, SelectionKey.OP_READ);
    }

    /*private void doConnect() throws IOException {
        // 如果连接成功，就将 SocketChannel 挂上 Selector，并且写入数据；如果失败，就说注册连接成功
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            System.out.println("try connection.");
            socketChannel.register(selector, SelectionKey.OP_READ);
            doResponseContent(socketChannel, "Hello Server.");
        } else {
            System.out.println("have connected.");
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }*/

    private void doResponseContent(SocketChannel socketChannel, String content) throws IOException {
        if (StringUtils.isNoneBlank(content)) {
            String echo = Thread.currentThread().getName() + " : " + content;
            System.out.println("to server [" + echo + "]");
            /*final byte[] writeBytes = res.getBytes(StandardCharsets.UTF_8);
            final ByteBuffer writeBuffer = ByteBuffer.allocate(writeBytes.length);
            writeBuffer.put(writeBytes);
            // 切换到写模式
            writeBuffer.flip();
            socketChannel.write(writeBuffer);*/
            socketChannel.write(ByteBuffer.wrap(echo.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public void stop() {
        run = false;
    }
}
