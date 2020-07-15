package cn.nihility.io.nio;

/**
 * EchoClient
 *
 * @author dandelion
 * @date 2020-05-18 15:37
 */
public class EchoClientCall {

    public static void main(String[] args) {
        int listenPort = 50000;

        EchoClient client = new EchoClient(listenPort);
        client.startService();
    }

}
