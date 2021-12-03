package cn.nihility.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class NetworkUtil {

    private static final Logger log = LoggerFactory.getLogger(NetworkUtil.class);

    public static final String LOOPBACK_ADDRESS = "127.0.0.1";
    public static final String LOCAL_HOST_NAME = "localhost";

    private NetworkUtil() {
    }

    /**
     * 获取指定 网卡 接口的 IP 地址，若是没有指定网卡接口返回所有接口地址集合
     *
     * @param interfaceName 接口名称
     * @return 网卡接口地址集合
     */
    public static List<String> getIPAddress(String interfaceName) throws SocketException {
        final List<String> ipList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (log.isDebugEnabled()) {
                log.debug("Net Interface Name [{}]", ni.getDisplayName());
            }
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                // skip loopback address , skip ipv6 address
                if (address.isLoopbackAddress() || address instanceof Inet6Address) {
                    continue;
                }
                if (null == interfaceName || ni.getDisplayName().equalsIgnoreCase(interfaceName)) {
                    ipList.add(address.getHostAddress());
                }
            }
        }
        return ipList;
    }

    /**
     * 获取得到的第一个本地的 IP 地址，当获取地址抛出错误，返回回环地址。
     */
    public static String getFirstHostAddress() {
        try {
            return getIPAddress(null).get(0);
        } catch (SocketException e) {
            log.error("Get Local IP Address Error", e);
        }
        return LOOPBACK_ADDRESS;
    }

    /**
     * 获取本地的 host 名称
     */
    public static String getLocalHostName() {
        String hostName;
        if ((hostName = System.getenv("COMPUTERNAME")) == null) {
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                log.error("Get LocalHost Name Error", e);
                hostName = LOCAL_HOST_NAME;
            }
        }
        return hostName;
    }

}
