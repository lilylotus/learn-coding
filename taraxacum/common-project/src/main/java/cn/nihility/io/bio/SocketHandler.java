package cn.nihility.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * SocketHandler
 *
 * @author dandelion
 * @date 2020-04-19 14:46
 */
public class SocketHandler implements Runnable {

    private Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        InputStream in;
        OutputStream out;

        try {
            System.out.println("in [" + socket.getRemoteSocketAddress() + "]");

            byte[] buffer = new byte[1024];
            int len;
            String rec;

            /* 该写法不行， read 阻塞问题
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (;;) {
                in = socket.getInputStream();
                while ((len = in.read(buffer)) != -1) {
                    System.out.println("len " + len);
                    bos.write(buffer, 0, len);
                }
                System.out.println("exit len " + len);

                rec = new String(bos.toByteArray(), Charset.forName("UTF-8"));
                bos.reset();
                System.out.println("Rec [" + rec + "]");

                if ("quit".equals(rec)) {
                    break;
                }

                out = socket.getOutputStream();
                out.write(("success rec [" + rec + "]").getBytes(Charset.forName("UTF-8")));
            }*/

            in = socket.getInputStream();
            out = socket.getOutputStream();
            // 读取阻塞
            while ((len = in.read(buffer)) != -1) {
                rec = new String(buffer, 0, len, Charset.forName("UTF-8"));
                System.out.println("Rec [" + rec + "]");

                out.write(("success rec [" + rec + "]").getBytes(Charset.forName("UTF-8")));
                out.flush();
            }

            System.out.println("exit [" + socket.getRemoteSocketAddress() + "]");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
