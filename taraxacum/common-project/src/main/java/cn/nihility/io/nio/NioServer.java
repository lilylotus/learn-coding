package cn.nihility.io.nio;

import java.util.Scanner;

/**
 * NioServer
 *
 * @author dandelion
 * @date 2020-04-19 16:53
 */
public class NioServer {

    public static void main(String[] args) {

        MultiplexNioServer server = new MultiplexNioServer(50002);
        Thread t = new Thread(server, "nioServer-001");
        t.start();

        Scanner scanner = new Scanner(System.in);
        String read;

        for (;;) {
            read = scanner.nextLine();
            System.out.println("input [" + read + "]");
            if ("q".equals(read)) {
                server.stop();
                break;
            }
        }

    }

}
