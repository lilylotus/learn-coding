package cn.nihility.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * NioClientHandler
 * 接收服务端响应数据
 * @author dandelion
 * @date 2020-03-19 15:06
 */

public class NioClientHandler implements Runnable {

    private Selector selector;

    private boolean stop = false;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    public void stop() {
        this.stop = true;
    }


    @Override
    public void run() {

        try {
            for (;!stop;) {
                // 就绪 Channel 的数量
                int selectCnt = selector.select();
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

                    // 可读事件
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client Handler exit.");
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

        // 打印信息
        System.out.println(sb.toString());

    }
}
