package cn.nihility.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * NioUtils
 *
 * @author dandelion
 * @date 2020-03-19 15:13
 */
public class NioUtils {

    /**
     * 可读事件处理器
     */
    public static void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
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
    public static void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
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
