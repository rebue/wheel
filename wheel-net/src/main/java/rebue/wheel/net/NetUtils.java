package rebue.wheel.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rebue.wheel.core.util.RegexUtils;

public class NetUtils {
    private final static Logger _log = LoggerFactory.getLogger(NetUtils.class);

    /**
     * 指定第一网卡的名称
     */
    private static String       _firstNetworkInterfaceName;

    /**
     * 指定第一网卡的名称
     * 
     * @param name
     *             网卡名称
     */
    public static void setFirstNetworkInterface(final String name) {
        _firstNetworkInterfaceName = name;
    }

    /**
     * 忽略的网卡
     */
    private static List<Pattern> _ignoreNetworkInterfaces = new LinkedList<>();
    static {
        _ignoreNetworkInterfaces.add(Pattern.compile("docker\\d{1,2}"));
    }

    /**
     * 添加要忽略的网卡(多网卡下排除一些不合理的网卡)
     * 
     * @param regex
     *              要忽略网卡的正则表达式
     */
    public static void addIgnoreNetworkInterface(final String regex) {
        _ignoreNetworkInterfaces.add(Pattern.compile(regex));
    }

    /**
     * FIXME 解决并发问题
     *
     */
    private static class NetUtilsSingletonHolder {
        private static List<String> _macs = new LinkedList<>();
        private static List<String> _ips  = new LinkedList<>();
        private static String       _firstIp;
        private static String       _firstMac;
        static {
            _log.info("NetUtils类初始化：获取本机的IP地址和MAC地址");
            try {
                final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                if (networkInterfaces != null) {
                    while (networkInterfaces.hasMoreElements()) {
                        final NetworkInterface         networkInterface     = networkInterfaces.nextElement();
                        final String                   networkInterfaceName = networkInterface.getName();
                        // 获取网卡的所有IP地址
                        final Enumeration<InetAddress> inetAddresses        = networkInterface.getInetAddresses();
                        String                         ip                   = null, firstIp = null;
                        if (inetAddresses != null) {
                            while (inetAddresses.hasMoreElements()) {
                                final InetAddress address = inetAddresses.nextElement();
                                ip = address.getHostAddress();
                                _ips.add(ip);
                                if (_firstIp == null && isValidAddress(address)) {
                                    if (_firstNetworkInterfaceName != null) {
                                        if (networkInterfaceName.equals(_firstNetworkInterfaceName)) {
                                            _firstIp = firstIp = ip;
                                        }
                                    }
                                    else {
                                        if (isValidNetworkInterface(networkInterfaceName)) {
                                            _firstIp = firstIp = ip;
                                        }
                                    }
                                }
                            }
                        }

                        // 获取网卡的MAC地址
                        final byte[] bytes = networkInterface.getHardwareAddress();
                        String       mac;
                        if (bytes != null && bytes.length > 0) {
                            final StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < bytes.length; i++) {
                                if (i != 0) {
                                    sb.append("-");
                                }
                                final int    tmp = bytes[i] & 0xff; // 字节转换为整数
                                final String str = Integer.toHexString(tmp);
                                if (str.length() == 1) {
                                    sb.append("0" + str);
                                }
                                else {
                                    sb.append(str);
                                }
                            }
                            mac = sb.toString().toUpperCase();
                            _macs.add(mac);
                            if (_firstMac == null && firstIp != null) {
                                _firstMac = mac;
                            }
                        }
                        else {
                            mac = "(none)";
                        }
                        if (ip == null) {
                            ip = "(none)";
                        }
                        _log.info("网卡: {} IP: {} MAC: {}", StringUtils.rightPad(networkInterfaceName, 20), StringUtils.rightPad(ip, 16), StringUtils.rightPad(mac, 18));
                    }
                    if (_ips.isEmpty() || _macs.isEmpty()) {
                        final String msg = "没有找到IP地址或MAC地址";
                        _log.error(msg);
                        throw new RuntimeException(msg);
                    }
                }
            } catch (final SocketException e) {
                final String msg = "网络IO异常";
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

    public static boolean isValidNetworkInterface(final String networkInterfaceName) {
        for (final Pattern pattern : _ignoreNetworkInterfaces) {
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
    private static boolean isValidAddress(final InetAddress inetAddress) {
        if (inetAddress == null || inetAddress.isLoopbackAddress() || !inetAddress.isSiteLocalAddress()) {
            return false;
        }
        final String name = inetAddress.getHostAddress();
        return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && RegexUtils.matchIpv4(name));
    }

    public static String getMacAddressOfAgent(final String ip) {
        String str        = "";
        String macAddress = "";
        try {
            final Process           p     = Runtime.getRuntime().exec("nbtstat -A " + ip);
            final InputStreamReader ir    = new InputStreamReader(p.getInputStream());
            final LineNumberReader  input = new LineNumberReader(ir);
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    if (str.indexOf("MAC 地址") > 1) {
                        // 客户端使用的是中文版操作系统
                        macAddress = str.substring(str.indexOf("MAC 地址") + 9, str.length());
                        break;
                    }
                    else if (str.indexOf("MAC Address") > 1) {// 客户端使用的是英文版操作系统
                        macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length());
                        break;
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace(System.out);
        }
        return macAddress;
    }

}
