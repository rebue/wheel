package rebue.wheel;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtils {
    private final static Logger  _log                     = LoggerFactory.getLogger(NetUtils.class);

    private static List<Pattern> _ignoreNetworkInterfaces = new LinkedList<>();
    static {
        _ignoreNetworkInterfaces.add(Pattern.compile("docker\\d{1,2}"));
    }

    /**
     * 添加要忽略的网卡(多网卡下排除一些不合理的网卡)
     * 
     * @param regex
     *            要忽略网卡的正则表达式
     */
    public static void addIgnoreNetworkInterface(String regex) {
        _ignoreNetworkInterfaces.add(Pattern.compile(regex));
    }

    private static class NetUtilsSingletonHolder {
        private static List<String> _macs = new LinkedList<>();
        private static List<String> _ips  = new LinkedList<>();
        private static String       _firstIp;
        private static String       _firstMac;
        static {
            _log.info("NetUtils类初始化：获取本机的IP地址和MAC地址");
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                if (networkInterfaces != null) {
                    while (networkInterfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = networkInterfaces.nextElement();
                        String networkInterfaceName = networkInterface.getName();
                        // 获取网卡的所有IP地址
                        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                        String ip = null, firstIp = null;
                        if (inetAddresses != null) {
                            while (inetAddresses.hasMoreElements()) {
                                InetAddress address = inetAddresses.nextElement();
                                ip = address.getHostAddress();
                                _ips.add(ip);
                                if (_firstIp == null) {
                                    if (isValidAddress(address)) {
                                        if (isValidNetworkInterface(networkInterfaceName)) {
                                            _firstIp = firstIp = ip;
                                        }
                                    }
                                }
                            }
                        }

                        // 获取网卡的MAC地址
                        byte[] bytes = networkInterface.getHardwareAddress();
                        String mac;
                        if (bytes != null && bytes.length > 0) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < bytes.length; i++) {
                                if (i != 0) {
                                    sb.append("-");
                                }
                                int tmp = bytes[i] & 0xff; // 字节转换为整数
                                String str = Integer.toHexString(tmp);
                                if (str.length() == 1) {
                                    sb.append("0" + str);
                                } else {
                                    sb.append(str);
                                }
                            }
                            mac = sb.toString().toUpperCase();
                            _macs.add(mac);
                            if (_firstMac == null && firstIp != null) {
                                _firstMac = mac;
                            }
                        } else {
                            mac = "(none)";
                        }
                        if (ip == null)
                            ip = "(none)";
                        _log.info("网卡: {} IP: {} MAC: {}", StrUtils.padRight(networkInterfaceName, 20, ' '), StrUtils.padRight(ip, 16, ' '), StrUtils.padRight(mac, 18, ' '));
                    }
                    if (_ips.isEmpty() || _macs.isEmpty()) {
                        String msg = "没有找到IP地址或MAC地址";
                        _log.error(msg);
                        throw new RuntimeException(msg);
                    }
                }
            } catch (SocketException e) {
                String msg = "网络IO异常";
                _log.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
    }

    /**
     * 获取本机正式的第一个MAC地址
     */
    public static String getFirstMacAddrOfLocalHost() {
        _log.info("获取本机正式的第一个MAC地址");
        return NetUtilsSingletonHolder._firstMac;
    }

    public static boolean isValidNetworkInterface(String networkInterfaceName) {
        for (Pattern pattern : _ignoreNetworkInterfaces) {
            if (pattern.matcher(networkInterfaceName).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取本机正式的第一个IP地址
     */
    public static String getFirstIpOfLocalHost() {
        _log.info("获取本机正式的第一个IP地址");
        return NetUtilsSingletonHolder._firstIp;
    }

    private static final String LOCALHOST = "127.0.0.1";
    private static final String ANYHOST   = "0.0.0.0";

    /**
     * 判断是否是正式的IP地址
     */
    private static boolean isValidAddress(InetAddress inetAddress) {
        if (inetAddress == null || inetAddress.isLoopbackAddress() || !inetAddress.isSiteLocalAddress())
            return false;
        String name = inetAddress.getHostAddress();
        return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && RegexUtils.matchIpv4(name));
    }

}
