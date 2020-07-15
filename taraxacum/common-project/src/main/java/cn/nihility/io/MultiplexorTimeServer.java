package cn.nihility.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * MultiplexorTimeServer
 *
 * @author dandelion
 * @date 2020-03-19 14:27
 */
public class MultiplexorTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;


    public MultiplexorTimeServer(int port) {
        try {
            // 1. 创建 Selector
            selector = Selector.open(); // 创建多路复用器

            // 2. 通过 ServerSocketChannel 来创建 Channel 通道
            serverSocketChannel = ServerSocketChannel.open();
            // 3. 配置 Channel 为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            // 4. 绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            // 5. channel 注册到 Selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("The time Server is start in port " + port);

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

        while (!stop) {
            try {
                // 就绪 Channel 的数量
                int selectCnt = selector.select(1000);// 每隔一秒唤醒
                System.out.println("ready cnt = " + selectCnt);

                if (selectCnt == 0) { continue; }

                // 获取到所有可用的 Channel 集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

                while (keyIterator.hasNext()) {
                    // selectionKey 实例
                    SelectionKey selectionKey = keyIterator.next();

                    // 移除 selectionKey
                    keyIterator.remove();

                    if (!selectionKey.isValid()) { continue; }

                    // 如果是接入事件
                    if (selectionKey.isAcceptable()) {
                        acceptHandler(serverSocketChannel, selector);
                    }

                    // 可读事件
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }

                    // 可写事件
                    if (selectionKey.isWritable()) {

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        // 从 selectionKey 中获取已经就绪的 Channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // 创建 buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        StringBuilder sb = new StringBuilder();
        // 循环读取客户端信息
        while (socketChannel.read(byteBuffer) > 0) {
            // 切换 Buffer 为可读模式
            byteBuffer.flip();

            // 读取 Buffer 中的内容
            sb.append(StandardCharsets.UTF_8.decode(byteBuffer));

            // 重置 Buffer
            byteBuffer.clear();
        }

        // 将 Channel 再次注册到 Selector 上，监听可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);

        // 发送给其他客户端
        System.out.println(":: " + sb.toString());

    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        // 如果是接入事件，创建 socketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 将 socketChannel 设置为非阻塞模式
        socketChannel.configureBlocking(false);
        // 将 Channel 注册到 Selector 上，监听可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 回复客户端提示信息
        socketChannel.write(Charset.forName("UTF-8").encode("服务端收到请求，开始处理..."));
    }
}
