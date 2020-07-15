package cn.nihility.io.bio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * BioClientSingle
 *
 * @author dandelion
 * @date 2020-04-19 12:26
 */
public class BioClientSingle {

    public static void main(String[] args) {

        int port = 50001;
        String charset = "UTF-8";
        Socket socket = null;

        try {
            socket = new Socket("127.0.0.1", port);
            Scanner scanner = new Scanner(System.in);

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            int len;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            for (;;) {
                String line = scanner.nextLine();

                if ("quit".equals(line)) { break; }

                out.write(line.getBytes(Charset.forName(charset)));
                out.flush();
                // out.close(); 不能关闭，就等于关闭了 socket


                len = in.read(buffer); // 这里会被阻塞
                if (len > 0) {
                    bos.write(buffer, 0, len);
                    String rec = new String(bos.toByteArray(), Charset.forName(charset));
                    System.out.println("rec [" + rec + "]");
                    bos.reset();
                }

                // in.close(); 不能 close，相当于关闭了 socket

                /*while ((len = in.read(buffer)) > 0) { // 这里会被阻塞
                    String rec = new String(buffer, 0, len, Charset.forName(charset));
                    System.out.println("rec [" + rec + "]");
                }*/
            }

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
