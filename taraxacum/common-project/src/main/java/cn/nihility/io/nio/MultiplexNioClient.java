package cn.nihility.io.nio;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * MultiplexNioClient
 *
 * @author dandelion
 * @date 2020-04-19 16:25
 */
public class MultiplexNioClient implements Runnable {

    private final static Charset UTF8 = Charset.forName("UTF-8");
    private Selector selector; // 多路复用器，事件轮询器
    private volatile boolean stop = false;

    private SocketChannel socketChannel;

    /**
     * 初始化多路复用器，绑定监听端口
     */
    public MultiplexNioClient(int port)  {
        try {
            // 获取一个客户端通道
            socketChannel = SocketChannel.open();
            // 设置为非阻塞
            socketChannel.configureBlocking(false);
            // 获取一个多路复用器
            selector = Selector.open();
            // 注册 selector 到 Channel, 监听 Connect 事件
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            // 发起连接
            socketChannel.connect(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {

        for (; !stop; ) {
            try {
                int selectCnt = selector.select(); // 阻塞
                if (selectCnt == 0) {
                    continue;
                }
                System.out.println("\r\nselect count [" + selectCnt + "]");

                // 事件集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key;

                while (it.hasNext()) {
                    key = it.next();
                    it.remove();

                    try {
                        handle(key);
                    } catch (Exception ex) {
                        System.out.println("ex " + ex.getMessage());
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

        if (null != selector) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (null != socketChannel) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(SelectionKey key) throws IOException {
        // 是否可连接
        if (key.isValid() && key.isConnectable()) {
            System.out.println("connectable");
            SocketChannel sc = (SocketChannel) key.channel();
            // 完成连接
            if (sc.isConnectionPending()) {
                // 完成连接
                sc.finishConnect();
                System.out.println("connect server success.");

                /*// 发送消息给 server
                String res = "Hello, Server ...";
                ByteBuffer byteBuffer = ByteBuffer.wrap(res.getBytes(UTF8));
                byteBuffer.flip();
                sc.write(byteBuffer);
                System.out.println("send msg to server success.");*/

                registerChannel(selector, sc, SelectionKey.OP_READ);
            } else {
                // 连接失败退出
                System.exit(1);
            }
        }

        // 读事件
        if (key.isValid() && key.isReadable()) {
            System.out.println("readable");
            SocketChannel sc = (SocketChannel) key.channel();
            // 写 0， 1024
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int readBytes;

            while ((readBytes = sc.read(byteBuffer)) > 0) {
                byteBuffer.flip(); // 计算 byte 位置信息
                // 读取的多少 byte, remain = readBytes
                //int remain = byteBuffer.remaining();
                bos.write(byteBuffer.array(), 0, readBytes);
                byteBuffer.clear();
            }

            /*readBytes = sc.read(byteBuffer);
            while (readBytes > 0) {
                // 读写模式反转
                byteBuffer.flip();

                byte[] buffer = new byte[byteBuffer.remaining()];
                byteBuffer.get(buffer);
                bos.write(buffer);

                //String rec = new String(buffer, Charset.forName("UTF-8"));
                //System.out.println("read is : [" + rec + "]");

                byteBuffer.clear();
                readBytes = sc.read(byteBuffer);
            }*/

            if (bos.size() > 0) {
                String rec = new String(bos.toByteArray(), Charset.forName("UTF-8"));
                System.out.println("read is : [" + rec + "]");
                bos.close();

                // res(sc, rec);
            }

            if (readBytes < 0) {
                // 非法 selectionKey ，关闭 Channel
                System.out.println("close channel");
                sc.close();
                key.cancel();
            } else {
                registerChannel(selector, sc, SelectionKey.OP_WRITE);
            }
        }

        if (key.isWritable()) {
            System.out.println("writable");
            SocketChannel sc = (SocketChannel) key.channel();
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine();
            System.out.println("send to server [" + msg + "]");

            sc.write(UTF8.encode(msg));

            registerChannel(selector, socketChannel, SelectionKey.OP_READ);

            if ("quit".equals(msg)) {
                this.stop();
            }
        }
    }

    private void registerChannel(Selector selector, SocketChannel channel, int op) throws IOException {
        if (null == channel) { return; }
        channel.configureBlocking(false);
        channel.register(selector, op);
    }

    private void res(SocketChannel channel, String response) throws IOException {
        if (StringUtils.isNotEmpty(response)) {
            byte[] bytes = ("success rec [" + response + "]").getBytes(Charset.forName("UTF-8"));
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);

            writeBuffer.put(bytes);
            writeBuffer.flip(); // 必须要调用该方法

            channel.write(writeBuffer);
            System.out.println("res end.");
        }
    }
}
