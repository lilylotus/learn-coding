package cn.nihility.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BioServerSingle
 *
 * @author dandelion
 * @date 2020-04-19 12:19
 */
public class BioServerSingle {

    public static void main(String[] args) {

        int port = 50001;
        ServerSocket serverSocket = null; // 服务端对象
        Socket socket = null; // 客户端链接
        InputStream in; // 输入流
        OutputStream out; // 输出流

        try {
            serverSocket = new ServerSocket(port);

            BioServerHeadlerExecutorPool pool = new BioServerHeadlerExecutorPool(50, 100);

            System.out.println("Socket Server Start.");
            for (;;) {
                System.out.println("Socket Accept.");

                socket = serverSocket.accept(); // 阻塞，建立三次握手，等待客户端

                System.out.println("connect client [" + socket.getRemoteSocketAddress() + "]");

                //new Thread(new SocketHandler(socket)).start();
                pool.execute(new SocketHandler(socket));

                /*in = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int len;

                // socket 读取也会阻塞，等待客户端传输完成
                while ((len = in.read(buffer)) != -1) {
                    String rec = new String(buffer, 0, len, Charset.forName("UTF-8"));
                    System.out.println("Client Input Is : " + rec);
                    // 返回信息
                    out = socket.getOutputStream();
                    out.write(("success rec [" + rec + "]").getBytes(Charset.forName("UTF-8")));
                    out.flush();
                    // out.close(); // 还未处理完成，不能关闭 流
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            /*if (null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
            if (null != serverSocket) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
