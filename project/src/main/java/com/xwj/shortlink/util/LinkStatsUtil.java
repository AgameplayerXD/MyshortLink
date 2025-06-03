package com.xwj.shortlink.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 监控短链接状态所用到的工具类
 */
public class LinkStatsUtil {


    /**
     * 获取客户端真实IP地址，优先从各类代理头中获取，无法识别时回退到request.getRemoteAddr()
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) return "unknown";

        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For 可能是一个逗号分隔的 IP 列表，第一个是客户端 IP
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();

    }


    /**
     * 获取用户的操作系统信息
     *
     * @param request
     * @return
     */
    public static String getUserOS(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }

        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "macOS";
        } else if (userAgent.contains("x11")) {
            return "Unix";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        } else {
            return "Other";
        }
    }

    /**
     * 获取用户的浏览器信息
     *
     * @param request
     * @return
     */
    public static String getUserBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";

        if (userAgent.contains("Edge")) {
            return "Microsoft Edge";
        } else if (userAgent.contains("OPR") || userAgent.contains("Opera")) {
            return "Opera";
        } else if (userAgent.contains("Chrome")) {
            return "Google Chrome";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari";
        } else if (userAgent.contains("Firefox")) {
            return "Mozilla Firefox";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        } else {
            return "Unknown";
        }
    }

    /**
     * 获取用户访问设备
     *
     * @param request 请求
     * @return 访问设备
     */
    public static String getDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("mobile")) {
            return "Mobile";
        }
        return "PC";
    }

    /**
     * 获取用户访问网络
     *
     * @param request 请求
     * @return 访问设备
     */
    public static String getNetwork(HttpServletRequest request) {
        String actualIp = getClientIp(request);
        // 这里简单判断IP地址范围，您可能需要更复杂的逻辑
        // 例如，通过调用IP地址库或调用第三方服务来判断网络类型
        return actualIp.startsWith("192.168.") || actualIp.startsWith("10.") ? "WIFI" : "Mobile";
    }
}
