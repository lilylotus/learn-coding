package cn.nihility.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SocketDemo
 *
 * @author dandelion
 * @date 2020-03-23 15:52
 */
public class SocketDemo {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket(); // tcp
        socket.connect(new InetSocketAddress("localhost", 8080));


        ServerSocket serverSocket = new ServerSocket(8080);
        serverSocket.bind(new InetSocketAddress("localhost", 8080));
    }

}
