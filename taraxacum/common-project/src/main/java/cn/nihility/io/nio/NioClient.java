package cn.nihility.io.nio;

/**
 * NioClient
 *
 * @author dandelion
 * @date 2020-04-19 22:15
 */
public class NioClient {

    public static void main(String[] args) {
        MultiplexNioClient client = new MultiplexNioClient(50002);
        Thread t = new Thread(client, "nioClient-0001");
        t.start();

        /*Scanner scanner = new Scanner(System.in);
        String read;

        for (;;) {
            read = scanner.nextLine();
            System.out.println("input [" + read + "]");
            if ("q".equals(read)) {
                client.stop();
                break;
            }
        }*/

        /*try {
            SocketChannel socketChannel = SocketChannel.open();

            Selector selector = Selector.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            socketChannel.connect(new InetSocketAddress("127.0.0.1", 50001));

            for (;;) {

                int select = selector.select(1000L);
                if (0 == select) { continue; }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    *//* operation *//*

                    if (key.isValid() && key.isConnectable()) {
                        System.out.println("connectable");
                        SocketChannel sc = (SocketChannel) key.channel();

                        if (sc.isConnectionPending()) {
                            sc.finishConnect();
                            System.out.println("connect server success.");

                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                        } else {
                            System.exit(1);
                        }

                    }

                    if (key.isValid() && key.isReadable()) {
                        System.out.println("readable");
                        SocketChannel sc = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        int read = sc.read(buffer);

                        if (read > 0) {

                            buffer.flip();
                            System.out.println(new String(buffer.array(), Charset.forName("UTF-8")));
                            buffer.clear();

                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_WRITE);

                        } else {
                            sc.close();
                            key.cancel();
                        }

                    }

                    if (key.isValid() && key.isWritable()) {
                        System.out.println("writable");
                        SocketChannel sc = (SocketChannel) key.channel();

                        Scanner scanner = new Scanner(System.in);
                        String read = scanner.nextLine();
                        System.out.println("input [" + read + "]");
                        sc.write(Charset.forName("UTF-8").encode(read));

                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                    }

                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

}
