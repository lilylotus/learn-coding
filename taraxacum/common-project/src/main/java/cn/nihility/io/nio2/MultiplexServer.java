package cn.nihility.io.nio2;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

public class MultiplexServer implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(MultiplexServer.class);
    private Selector selector;

    private volatile boolean run = true;

    public static void main(String[] args) {
        int port = 40000;
        new Thread(new MultiplexServer(port)).start();
    }

    public MultiplexServer(int port) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 设置非阻塞
            serverSocketChannel.configureBlocking(false);

            final ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));

            /* 多路复用器 */
            selector = Selector.open();
            // 注册 serverSocketChannel 到 多路复用器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.info("Initialization Nio Server Success.");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void listen() {
        int selectCnt;
        while (run) {
            selectCnt = 0;
            try {
                selectCnt = selector.select(1000L);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (selectCnt > 0) {
                log.info("Selector count [{}]", selectCnt);
                final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                final Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();
                    // 移除事件，避免重复处理
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
        }
    }

    private void handleKey(SelectionKey selectionKey) throws IOException {
        // 可连接
        if (selectionKey.isValid() && selectionKey.isAcceptable()) {
            log.info("Acceptable SelectionKey");
            final ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
            // 三次握手
            final SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);

            // 返回信息
            if (sc.isConnected()) {
                sc.write(StandardCharsets.UTF_8.encode("Welcome To Nio Server."));
                log.info("Echo Connected Client Info.");
            }

            sc.register(selector, SelectionKey.OP_READ);
        }

        if (selectionKey.isValid() && selectionKey.isReadable()) {
            log.info("Readable SelectionKey");
            // 获取对应可读的 Chanel
            final SocketChannel sc = (SocketChannel) selectionKey.channel();
            // 创建一个 Buffer 用户加载数据
            final ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();

            while (sc.read(readBuffer) > 0) {
                // 切换到读取模式 position limit capacity
                readBuffer.flip();
                byte[] buffer = new byte[readBuffer.remaining()];
                readBuffer.get(buffer);
                bao.write(buffer);
                readBuffer.clear();
            }

            if (bao.size() > 0) {
                String content = new String(bao.toByteArray(), StandardCharsets.UTF_8);
                log.info("Receive Client Content ({})", content);

                responseReceiveMessage(sc, content);
                /*sc.register(selector, SelectionKey.OP_READ);*/
            } else {
                // 小于 0 说明有问题，关闭 SelectionKey 和 channel
                log.info("Close The Connection In Question.");
                selectionKey.channel();
                sc.close();
            }
        }
    }

    private void responseReceiveMessage(SocketChannel socketChannel, String content) throws IOException {
        if (StringUtils.isNoneBlank(content)) {
            String res = Thread.currentThread().getName() + ":" + content;
            log.info("to client [{}]", res);
            final byte[] writeBytes = res.getBytes(StandardCharsets.UTF_8);
            final ByteBuffer writeBuffer = ByteBuffer.allocate(writeBytes.length);
            writeBuffer.put(writeBytes);
            // 切换到写模式
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
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
