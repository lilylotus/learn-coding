package cn.nihility.io.nio;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * MultiplexNioServer
 *
 * @author dandelion
 * @date 2020-04-19 16:25
 */
public class MultiplexNioServer implements Runnable {

    private Selector selector; // 多路复用器，事件轮询器
    private volatile boolean stop = false;

    /**
     * 初始化多路复用器，绑定监听端口
     */
    public MultiplexNioServer(int port)  {
        try {
            // 获取一个 ServerSocketChannel
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            // 获取一个多路复用器
            selector = Selector.open();
            // 设置为非阻塞
            serverChannel.configureBlocking(false);
            // 绑定端口
            serverChannel.socket().bind(new InetSocketAddress(port), 50001);
            // 注册 selector 到 Channel, 监听 Accept 事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

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
                int selectCnt = selector.select(1000L); // 阻塞
                if (selectCnt == 0) {
                    System.out.print(".");
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

    }

    private void handle(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 连接事件
            if (key.isAcceptable()) {
                System.out.println("acceptable");
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept(); // 3 次握手
                sc.configureBlocking(false);

                // 返回信息
                if (sc.isConnected()) {
                    ByteBuffer buffer = ByteBuffer.allocate(128);
                    buffer.put("Connect Server Success.".getBytes(Charset.forName("UTF-8")));
                    buffer.flip();
                    sc.write(buffer);
                    System.out.println("client connect success");
                }

                // 链接建立好注册读取事件
                //sc.register(selector, SelectionKey.OP_READ);
                registerChannel(selector, sc, SelectionKey.OP_READ);
            }

            // 读事件
            if (key.isReadable()) {
                System.out.println("readable");
                SocketChannel sc = (SocketChannel) key.channel();
                // 写 0， 1024
                ByteBuffer byteBuffer = ByteBuffer.allocate(4);
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
                    res(sc, rec);
                }

                if (readBytes < 0) {
                    // 非法 selectionKey ，关闭 Channel
                    key.cancel();
                    sc.close();
                    System.out.println("close channel");
                } else {
                    // 现在服务端就是持续读，没有 write 操作
                    registerChannel(selector, sc, SelectionKey.OP_READ);
                }
            }

            if (key.isValid() && key.isWritable()) {
                System.out.println("writable");
                SocketChannel sc = (SocketChannel) key.channel();
                String msg = "Hei,Client : " + sc.getLocalAddress();

                ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes(Charset.forName("UTF-8")));
                byteBuffer.flip();

                sc.write(byteBuffer);

                System.out.println("send to client [" + msg + "]");

                //registerChannel(selector, sc, SelectionKey.OP_READ);
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


            // ByteBuffer buffer = ByteBuffer.wrap(messageFormatToByteArray("Welcome to My Echo Server."));

            writeBuffer.put(bytes);
            writeBuffer.flip(); // 必须要调用该方法

            channel.write(writeBuffer);
            System.out.println("res end.");
        }
    }
}
