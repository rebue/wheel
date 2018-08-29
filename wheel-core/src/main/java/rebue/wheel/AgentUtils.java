package rebue.wheel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentUtils {
    private final static Logger _log = LoggerFactory.getLogger(AgentUtils.class);

    /**
     * 获取浏览器客户端IP
     * 
     * @param passProxy
     *            指出之前经过什么反向代理(只能设置为noproxy/nginx/apache/weblogic其中之一)
     */
    public static String getIpAddr(HttpServletRequest req, String passProxy) {
        _log.info("获取浏览器客户端IP");
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
                _log.info("如果没取到IP，说明没经过代理，直接按没有代理的方式取IP");
                ip = AgentUtils.getIpAddrNoPassProxy(req);
            }
        }
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            _log.info("发现以上得到的是lo的IP，那就再去获取本机第一个有效的IP");
            ip = NetUtils.getFirstIpOfLocalHost();
        }
        return ip;
    }

    /**
     * 获取浏览器客户端IP(在没有经过反向代理的情况下)
     */
    public static String getIpAddrNoPassProxy(HttpServletRequest request) {
        _log.info("获取浏览器客户端IP(在没有经过反向代理的情况下)");
        String ip = request.getRemoteAddr();
        _log.info("从request.getRemoteAddr()中获取到IP: {}", ip);
        return ip;
    }

    /**
     * 获取浏览器客户端IP(在经过Nginx反向代理的情况下)
     * 在一般情况下使用request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * 
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，如果还不存在则返回null
     * 
     */
    public static String getIpAddrPassNginx(HttpServletRequest req) {
        _log.info("从Nginx反向代理后的请求头中获取X-Real-IP");
        String ip = req.getHeader("X-Real-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            _log.info("从X-Real-IP中获取到ip: {}", ip);
            return ip;
        }

        _log.info("从Nginx反向代理后的请求头中获取x-forwarded-for");
        ip = req.getHeader("x-forwarded-for");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            int index = ip.indexOf(",");
            if (index > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
            _log.info("从x-forwarded-for中获取到ip: {}", ip);
            return ip;
        }

        return null;
    }

    /**
     * 获取浏览器客户端IP(在经过Apache反向代理的情况下)
     * 在一般情况下使用request.getRemoteAddr()即可，但是经过Apache等反向代理软件后，这个方法会失效。
     * 
     * 本方法先从Header中获取Proxy-Client-IP，如果还不存在则返回null
     * 
     * 
     */
    public static String getIpAddrPassApache(HttpServletRequest req) {
        _log.info("从Apache反向代理后的请求头中获取Proxy-Client-IP");
        String ip = req.getHeader("Proxy-Client-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            _log.info("从Proxy-Client-IP中获取到ip: {}", ip);
            return ip;
        }

        return null;
    }

    /**
     * 获取浏览器客户端IP(在经过WebLogic反向代理的情况下)
     * 在一般情况下使用request.getRemoteAddr()即可，但是经过WebLogic等反向代理软件后，这个方法会失效。
     * 
     * 本方法先从Header中获取WL-Proxy-Client-IP，如果还不存在则返回null
     * 
     */
    public static String getIpAddrPassWebLogic(HttpServletRequest req) {
        _log.info("从WebLogic反向代理后的请求头中获取WL-Proxy-Client-IP");
        String ip = req.getHeader("WL-Proxy-Client-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            _log.info("从WL-Proxy-Client-IP中获取到ip: {}", ip);
            return ip;
        }

        return null;
    }

    /**
     * 获取到用户浏览器的信息
     */
    public static String getUserAgent(HttpServletRequest req) {
        _log.info("获取到用户浏览器的信息");
        String userAgent = req.getHeader("user-agent");
        if (StringUtils.isBlank(userAgent)) {
            _log.info("没有获取到用户浏览器的信息");
            return "";
        } else {
            _log.info("用户浏览器的信息: {}", userAgent);
            return userAgent;
        }
    }

    /**
     * 获取来访者的浏览器版本
     * 
     * @param req
     * @return
     */
    public static String getRequestBrowserInfo(HttpServletRequest req) {
        String browserVersion = null;
        String header = req.getHeader("user-agent");
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
     * @param req
     * @return
     */
    public static String getRequestSystemInfo(HttpServletRequest req) {
        String systenInfo = null;
        String header = req.getHeader("user-agent");
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
        } else if (header.indexOf("NT 6.0") > 0) {
            systenInfo = "Windows Vista";
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
     * @param ip
     * @return
     */
    public static String getHostName(String ip) {
        InetAddress inet;
        try {
            inet = InetAddress.getByName(ip);
            return inet.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 命令获取mac地址
     * 
     * @param cmd
     * @return
     */
    private static String callCmd(String[] cmd) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 
     * 
     * 
     * @param cmd
     *            第一个命令
     * 
     * @param another
     *            第二个命令
     * 
     * @return 第二个命令的执行结果
     * 
     */

    private static String callCmd(String[] cmd, String[] another) {
        String result = "";
        String line = "";
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            proc.waitFor(); // 已经执行完第一个命令，准备执行第二个命令
            proc = rt.exec(another);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 
     * 
     * 
     * @param ip
     *            目标ip,一般在局域网内
     * 
     * @param sourceString
     *            命令处理的结果字符串
     * 
     * @param macSeparator
     *            mac分隔符号
     * 
     * @return mac地址，用上面的分隔符号表示
     * 
     */

    private static String filterMacAddress(final String ip, final String sourceString, final String macSeparator) {
        String result = "";
        String regExp = "((([0-9,A-F,a-f]{1,2}" + macSeparator + "){1,5})[0-9,A-F,a-f]{1,2})";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            result = matcher.group(1);
            if (sourceString.indexOf(ip) <= sourceString.lastIndexOf(matcher.group(1))) {
                break; // 如果有多个IP,只匹配本IP对应的Mac.
            }
        }
        return result;
    }

    /**
     * @param ip
     *            目标ip
     * @return Mac Address
     * 
     */

    private static String getMacInWindows(final String ip) {
        String result = "";
        String[] cmd = { "cmd", "/c", "ping " + ip };
        String[] another = { "cmd", "/c", "arp -a" };
        String cmdResult = callCmd(cmd, another);
        _log.info("ping的结果: {}", cmdResult);
        if (StringUtils.isBlank(cmdResult)) {
            return null;
        }
        result = filterMacAddress(ip, cmdResult, "-");
        return result;
    }

    /**
     * 
     * @param ip
     *            目标ip
     * @return Mac Address
     * 
     */
    private static String getMacInLinux(final String ip) {
        String result = "";
        String[] cmd = { "/bin/sh", "-c", "ping " + ip + " -c 2 && arp -a" };
        String cmdResult = callCmd(cmd);
        _log.info("ping的结果: {}", cmdResult);
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
    public static String getMacAddress(String ip) {
        _log.info("通过IP({})获取MAC地址", ip);
        String macAddress = "";
        if (OsUtils.isWin())
            macAddress = getMacInWindows(ip);
        else
            macAddress = getMacInLinux(ip);
        if (StringUtils.isBlank(macAddress))
            macAddress = "未获取到MAC地址";
        return macAddress;
    }
}
