package com.fancier.missingyou.web.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
/**
 * 网络工具类
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
public class NetUtils {

    /**
     * 获取客户端 IP 地址
     *
     */
    public static String getIpAddress(HttpServletRequest request) {
        // 首先尝试从 HTTP 请求头中获取 "x-forwarded-for" 字段，这通常表示来自代理服务器的真实客户端 IP 地址。
        String ip = request.getHeader("x-forwarded-for");
        // 如果未从第一个头部获取到有效 IP，则检查其他常见的代理头（"Proxy-Client-IP" 和 "WL-Proxy-Client-IP"）。
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        // 本机地址处理
        // 如果依然未找到有效 IP，使用 request.getRemoteAddr() 获取连接地址。
        // 如果获取到的 IP 是 "127.0.0.1"（即本地地址），则通过 InetAddress.getLocalHost() 尝试获取本机的实际 IP 地址。
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                // 根据网卡取本机配置的 IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (inet != null) {
                    ip = inet.getHostAddress();
                }
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        if (ip == null) {
            return "127.0.0.1";
        }
        return ip;
    }

}