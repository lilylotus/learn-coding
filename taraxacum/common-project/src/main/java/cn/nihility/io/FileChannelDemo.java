package cn.nihility.io;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * FileChannelDemo
 *
 * @author dandelion
 * @date 2020-03-18 23:54
 */
public class FileChannelDemo {

    public static void main(String[] args) {

        String filePath = FileChannelDemo.class.getResource("/io.txt").getPath();
        System.out.println("path = " + filePath);

        io(filePath);
        System.out.println("\n=============================\n");
        nio(filePath);


    }

    public static void nio(String filePath) {
        RandomAccessFile randomAccessFile = null;
        RandomAccessFile randomAccessFile1 = null;

        try {
            randomAccessFile = new RandomAccessFile(filePath, "rw");
            randomAccessFile1 = new RandomAccessFile("io.bak", "rw");

            FileChannel channel = randomAccessFile.getChannel();
            FileChannel channelWrite = randomAccessFile1.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            StringBuilder sb = new StringBuilder(2048);
            int len;

            while ((len = channel.read(byteBuffer)) != -1) {
                byteBuffer.flip();

                sb.append(new String(byteBuffer.array(), 0, len, Charset.forName("UTF-8")));

                channelWrite.write(byteBuffer);

                //byteBuffer.compact();
                byteBuffer.clear();
            }

            System.out.println(sb.toString());


            channel.close();
            channelWrite.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(randomAccessFile);
            close(randomAccessFile1);
        }

    }

    private static void close(RandomAccessFile randomAccessFile) {
        if (null != randomAccessFile) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void io(String filePath) {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
            StringBuilder sb = new StringBuilder(2048);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len, Charset.forName("UTF-8")));
            }

            System.out.println(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
