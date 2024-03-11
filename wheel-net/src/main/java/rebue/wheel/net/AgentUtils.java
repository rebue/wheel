package rebue.wheel.net;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import rebue.wheel.core.OsUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AgentUtils {
    /**
     * 获取浏览器客户端IP
     *
     * @param passProxy 指出之前经过什么反向代理(只能设置为noproxy/nginx/apache/weblogic其中之一)
     */
    public static String getIpAddr(final HttpServletRequest req, final String passProxy) {
        log.info("获取浏览器客户端IP");
        String ip;
        if ("noproxy".equalsIgnoreCase(passProxy)) {
            ip = AgentUtils.getIpAddrNoPassProxy(req);
        } else {
            if ("nginx".equalsIgnoreCase(passProxy)) {
                ip = AgentUtils.getIpAddrPassNginx(req);
            } else if ("apache".equalsIgnoreCase(passProxy)) {
                ip = AgentUtils.getIpAddrPassApache(req);
            } else if ("weblogic".equalsIgnoreCase(passProxy)) {
                ip = AgentUtils.getIpAddrPassWebLogic(req);
            } else {
                throw new IllegalArgumentException("passProxy配置错误，只能设置为noproxy/nginx/apache/weblogic其中之一");
            }
            if (ip == null) {
                log.info("如果没取到IP，说明没经过代理，直接按没有代理的方式取IP");
                ip = AgentUtils.getIpAddrNoPassProxy(req);
            }
        }
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            log.info("发现以上得到的是lo的IP，那就再去获取本机第一个有效的IP");
            ip = NetUtils.getFirstIpOfLocalHost();
        }
        return ip;
    }

    /**
     * 获取浏览器客户端IP(在没有经过反向代理的情况下)
     */
    public static String getIpAddrNoPassProxy(final HttpServletRequest request) {
        log.info("获取浏览器客户端IP(在没有经过反向代理的情况下)");
        final String ip = request.getRemoteAddr();
        log.info("从request.getRemoteAddr()中获取到IP: {}", ip);
        return ip;
    }

    /**
     * 获取浏览器客户端IP(在经过Nginx反向代理的情况下)
     * 在一般情况下使用request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * <p>
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，如果还不存在则返回null
     */
    public static String getIpAddrPassNginx(final HttpServletRequest req) {
        log.info("从Nginx反向代理后的请求头中获取X-Real-IP");
        String ip = req.getHeader("X-Real-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            log.info("从X-Real-IP中获取到ip: {}", ip);
            return ip;
        }

        log.info("从Nginx反向代理后的请求头中获取x-forwarded-for");
        ip = req.getHeader("x-forwarded-for");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            final int index = ip.indexOf(",");
            if (index > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
            log.info("从x-forwarded-for中获取到ip: {}", ip);
            return ip;
        }

        return null;
    }

    /**
     * 获取浏览器客户端IP(在经过Apache反向代理的情况下)
     * 在一般情况下使用request.getRemoteAddr()即可，但是经过Apache等反向代理软件后，这个方法会失效。
     * <p>
     * 本方法先从Header中获取Proxy-Client-IP，如果还不存在则返回null
     */
    public static String getIpAddrPassApache(final HttpServletRequest req) {
        log.info("从Apache反向代理后的请求头中获取Proxy-Client-IP");
        final String ip = req.getHeader("Proxy-Client-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            log.info("从Proxy-Client-IP中获取到ip: {}", ip);
            return ip;
        }

        return null;
    }

    /**
     * 获取浏览器客户端IP(在经过WebLogic反向代理的情况下)
     * 在一般情况下使用request.getRemoteAddr()即可，但是经过WebLogic等反向代理软件后，这个方法会失效。
     * <p>
     * 本方法先从Header中获取WL-Proxy-Client-IP，如果还不存在则返回null
     */
    public static String getIpAddrPassWebLogic(final HttpServletRequest req) {
        log.info("从WebLogic反向代理后的请求头中获取WL-Proxy-Client-IP");
        final String ip = req.getHeader("WL-Proxy-Client-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            log.info("从WL-Proxy-Client-IP中获取到ip: {}", ip);
            return ip;
        }

        return null;
    }

    /**
     * 获取到用户浏览器的信息
     */
    public static String getUserAgent(final HttpServletRequest req) {
        log.info("获取到用户浏览器的信息");
        final String userAgent = req.getHeader("user-agent");
        if (StringUtils.isBlank(userAgent)) {
            log.info("没有获取到用户浏览器的信息");
            return "";
        } else {
            log.info("用户浏览器的信息: {}", userAgent);
            return userAgent;
        }
    }

    /**
     * 获取来访者的浏览器版本
     *
     * @param req 请求
     * @return 浏览器版本
     */
    public static String getRequestBrowserInfo(final HttpServletRequest req) {
        String       browserVersion = null;
        final String header         = req.getHeader("user-agent");
        if (header == null || header.equals("")) {
            return "";
        }
        if (header.indexOf("MSIE") > 0) {
            browserVersion = "IE";
        } else if (header.indexOf("Firefox") > 0) {
            browserVersion = "Firefox";
        } else if (header.indexOf("Chrome") > 0) {
            browserVersion = "Chrome";
        } else if (header.indexOf("Safari") > 0) {
            browserVersion = "Safari";
        } else if (header.indexOf("Camino") > 0) {
            browserVersion = "Camino";
        } else if (header.indexOf("Konqueror") > 0) {
            browserVersion = "Konqueror";
        }
        return browserVersion;
    }

    /**
     * 获取系统版本信息
     *
     * @param req 请求
     * @return 系统版本
     */
    public static String getRequestSystemInfo(final HttpServletRequest req) {
        String       systenInfo = null;
        final String header     = req.getHeader("user-agent");
        if (header == null || header.equals("")) {
            return "";
        }
        // 得到用户的操作系统
        if (header.indexOf("NT 6.0") > 0) {
            systenInfo = "Windows Vista/Server 2008";
        } else if (header.indexOf("NT 5.2") > 0) {
            systenInfo = "Windows Server 2003";
        } else if (header.indexOf("NT 5.1") > 0) {
            systenInfo = "Windows XP";
        } else if (header.indexOf("NT 6.1") > 0) {
            systenInfo = "Windows 7";
        } else if (header.indexOf("NT 6.2") > 0) {
            systenInfo = "Windows Slate";
        } else if (header.indexOf("NT 6.3") > 0) {
            systenInfo = "Windows 9";
        } else if (header.indexOf("NT 5") > 0) {
            systenInfo = "Windows 2000";
        } else if (header.indexOf("NT 4") > 0) {
            systenInfo = "Windows NT4";
        } else if (header.indexOf("Me") > 0) {
            systenInfo = "Windows Me";
        } else if (header.indexOf("98") > 0) {
            systenInfo = "Windows 98";
        } else if (header.indexOf("95") > 0) {
            systenInfo = "Windows 95";
        } else if (header.indexOf("Mac") > 0) {
            systenInfo = "Mac";
        } else if (header.indexOf("Unix") > 0) {
            systenInfo = "UNIX";
        } else if (header.indexOf("Linux") > 0) {
            systenInfo = "Linux";
        } else if (header.indexOf("SunOS") > 0) {
            systenInfo = "SunOS";
        }
        return systenInfo;
    }

    /**
     * 获取来访者的主机名称
     *
     * @param ip IP地址
     * @return 主机名称
     */
    public static String getHostName(final String ip) {
        InetAddress inet;
        try {
            inet = InetAddress.getByName(ip);
            return inet.getHostName();
        } catch (final UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 执行命令
     *
     * @param cmd 获取的命令
     * @return 命令打印信息
     */
    private static String callCmd(final String[] cmd) {
        StringBuilder result = new StringBuilder();
        String        line;
        try {
            final Process           proc = Runtime.getRuntime().exec(cmd);
            final InputStreamReader is   = new InputStreamReader(proc.getInputStream());
            final BufferedReader    br   = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 执行命令
     *
     * @param cmd     第一个命令
     * @param another 第二个命令
     * @return 第二个命令的执行结果
     */

    private static String callCmd(final String[] cmd, final String[] another) {
        StringBuilder result = new StringBuilder();
        String        line;
        try {
            final Runtime rt   = Runtime.getRuntime();
            Process       proc = rt.exec(cmd);
            proc.waitFor(); // 已经执行完第一个命令，准备执行第二个命令
            proc = rt.exec(another);
            final InputStreamReader is = new InputStreamReader(proc.getInputStream());
            final BufferedReader    br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * @param ip           目标ip,一般在局域网内
     * @param sourceString 命令处理的结果字符串
     * @param macSeparator mac分隔符号
     * @return mac地址，用上面的分隔符号表示
     */

    private static String filterMacAddress(final String ip, final String sourceString, final String macSeparator) {
        String        result  = "";
        final String  regExp  = "((([0-9,A-F,a-f]{1,2}" + macSeparator + "){1,5})[0-9,A-F,a-f]{1,2})";
        final Pattern pattern = Pattern.compile(regExp);
        final Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            result = matcher.group(1);
            if (sourceString.indexOf(ip) <= sourceString.lastIndexOf(matcher.group(1))) {
                break; // 如果有多个IP,只匹配本IP对应的Mac.
            }
        }
        return result;
    }

    /**
     * @param ip 目标ip
     * @return Mac Address
     */

    private static String getMacInWindows(final String ip) {
        String result;
        final String[] cmd = {"cmd", "/c", "ping " + ip
        };
        final String[] another = {"cmd", "/c", "arp -a"
        };
        final String cmdResult = callCmd(cmd, another);
        log.info("ping的结果: {}", cmdResult);
        if (StringUtils.isBlank(cmdResult)) {
            return null;
        }
        result = filterMacAddress(ip, cmdResult, "-");
        return result;
    }

    /**
     * @param ip 目标ip
     * @return Mac Address
     */
    private static String getMacInLinux(final String ip) {
        String result;
        final String[] cmd = {"/bin/sh", "-c", "ping " + ip + " -c 2 && arp -a"
        };
        final String cmdResult = callCmd(cmd);
        log.info("ping的结果: {}", cmdResult);
        if (StringUtils.isBlank(cmdResult)) {
            return null;
        }
        result = filterMacAddress(ip, cmdResult, ":");
        return result;
    }

    /**
     * 获取MAC地址
     *
     * @return 返回MAC地址
     */
    public static String getMacAddress(final String ip) {
        log.info("通过IP({})获取MAC地址", ip);
        String macAddress;
        if (OsUtils.isWin()) {
            macAddress = getMacInWindows(ip);
        } else {
            macAddress = getMacInLinux(ip);
        }
        if (StringUtils.isBlank(macAddress)) {
            macAddress = "未获取到MAC地址";
        }
        return macAddress;
    }
}
