package cn.nihility.cloud.uitil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static void main(String[] args) throws SocketException {
        System.out.println(getHostAddress(null));
        System.out.println(getHostAddress("Realtek PCIe GbE Family Controller"));
    }

    public static String getIp() {
        return getIp(null);
    }

    public static String getIp(String interfaceName) {
        String ip = "";
        interfaceName = StringUtils.isBlank(interfaceName) ? null : interfaceName.trim();
        try {
            List<String> ipList = getHostAddress(interfaceName);
            ip = ipList.isEmpty() ? "" : ipList.get(0);
        } catch (Exception ex) {
            logger.warn("Utils get IP warn", ex);
        }
        return ip;
    }


    /**
     * 获取已激活网卡的 IP 地址
     * @param interfaceName 可以依据网卡的名称获取该 IP 地址
     */
    public static List<String> getHostAddress(String interfaceName) throws SocketException {
        final List<String> ipList = new ArrayList<>(5);
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
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
