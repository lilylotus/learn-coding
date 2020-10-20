package cn.nihility.unify.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtil {

    private static final Logger log = LoggerFactory.getLogger(NetworkUtil.class);

    public static void main(String[] args) throws SocketException {
        getIPAddress(null).forEach(System.out::println);
    }

    /**
     * 获取指定 网卡 接口的 IP 地址，若是没有指定网卡接口返回所有接口地址集合
     * @param interfaceName 接口名称
     * @return 网卡接口地址集合
     */
    private static List<String> getIPAddress(String interfaceName) throws SocketException {
        final List<String> ipList = new ArrayList<>(6);
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (log.isDebugEnabled()) {
                log.debug("Net Interface Name [{}]", ni.getDisplayName());
            }
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                // skip loopback address
                if (address.isLoopbackAddress()) {
                    continue;
                }
                // skip ipv6 address
                if (address instanceof Inet6Address) {
                    continue;
                }
                String hostAddress = address.getHostAddress();
                if (null == interfaceName) {
                    ipList.add(hostAddress);
                } else if (interfaceName.equals(ni.getDisplayName())) {
                    ipList.add(hostAddress);
                }
            }
        }
        return ipList;
    }

}
