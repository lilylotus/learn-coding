package cn.nihility.io.nio2;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class MultiplexClient implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(MultiplexClient.class);

    private SocketChannel socketChannel;
    private Selector selector;
    private String host;
    private int port;
    private volatile boolean run = true;

    public static void main(String[] args) {
        int port = 40000;
        String host = "localhost";

        new Thread(new MultiplexClient(host, port)).start();
    }

    public MultiplexClient(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();

            log.info("Initialization Multiplex Client Success.");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            //尝试连接
            doConnect();
        }catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        int selectCnt;
        while (run) {
            selectCnt = 0;
            try {
                selectCnt = selector.select(1000L);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (selectCnt > 0) {
                final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                if (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();
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
            log.info("Connection To Multiplex Server.");

            SocketChannel sc = (SocketChannel) selectionKey.channel();

            if (sc.isConnectionPending()) {
                log.info("Connection is Pending");
                if (sc.finishConnect()) {
                    log.info("Finished Connection.");
                    sc.register(selector, SelectionKey.OP_READ);

                    doWrite(sc, "Connect To Multiplex Server.");
                } else {
                    log.error("Connection To Multiplex Server Error.");
                    System.exit(1);
                }
            }
        }

        if (selectionKey.isValid() && selectionKey.isReadable()) {
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
                log.info("Receive Server Content [{}]", content);

                /*sc.register(selector, SelectionKey.OP_WRITE);*/
                sc.register(selector, SelectionKey.OP_READ);
            } else {
                // 小于 0 说明有问题，关闭 SelectionKey 和 channel
                log.info("Close The Connection In Question.");
                selectionKey.channel();
                sc.close();
            }
        }

       /* if (selectionKey.isValid() && selectionKey.isWritable()) {
            SocketChannel sc = (SocketChannel) selectionKey.channel();
            Scanner scanner = new Scanner(System.in);

            String content = scanner.nextLine();
            final ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(content);
            sc.write(byteBuffer);

            sc.register(selector, SelectionKey.OP_READ);

            if ("quit".equals(content)) {
                log.info("Exit Client Application.");
                stop();
            }
        }*/

    }

    private void doConnect() throws IOException {
        // 如果连接成功，就将 SocketChannel 挂上 Selector，并且写入数据；如果失败，就说注册连接成功
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            log.info("do connection.");
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel, "Hello Server.");
        } else {
            log.info("has connected.");
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel, String content) throws IOException {
        if (StringUtils.isNoneBlank(content)) {
            String res = Thread.currentThread().getName() + ":" + content;
            log.info("to server [{}]", res);
            final byte[] writeBytes = res.getBytes(StandardCharsets.UTF_8);
            final ByteBuffer writeBuffer = ByteBuffer.allocate(writeBytes.length);
            writeBuffer.put(writeBytes);
            // 切换到写模式
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
        }
    }

    public void stop() {
        run = false;
    }
}
