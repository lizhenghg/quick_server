package com.cracker.quick.util;

import java.net.*;
import java.util.Enumeration;

/**
 *
 * 网络操作类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class NetworkUtil {

    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final int HTTP_PORT = 80;
    public static final String HTTPS_PORT = "443";
    public static final String COLON = ":";


    public static String getServerIpV4(String interfaceNames) throws SocketException {

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        NetworkInterface networkInterface;
        InetAddress inetAddress = null;
        String serverIp = null;

        while (networkInterfaces.hasMoreElements()) {
            networkInterface = networkInterfaces.nextElement();

            if (networkInterface.getInetAddresses().hasMoreElements()) {
                inetAddress = networkInterface.getInetAddresses().nextElement();
            }
            if (inetAddress == null) {
                continue;
            }
            if (!inetAddress.isLoopbackAddress()) {
                if ("127.0.0.1".equalsIgnoreCase(inetAddress.getHostAddress())) {
                    inetAddress = null;
                    continue;
                }
                if (inetAddress instanceof Inet6Address) {
                    inetAddress = null;
                    continue;
                }
                if (inetAddress instanceof Inet4Address) {
                    if (networkInterface.getName().contains(interfaceNames)) {
                        serverIp = inetAddress.getHostAddress();
                        break;
                    }
                }
            }
            inetAddress = null;
        }
        return serverIp;
    }

    public static String getHostFromStringAddress(String address) throws MalformedURLException {

        assert !Assert.isEmpty(address);

        if (!address.contains(HTTP) && !address.contains(HTTPS)) {
            // 判断是否https
            if (address.contains(COLON)) {
                if (HTTPS_PORT.equalsIgnoreCase(address.substring(address.lastIndexOf(COLON) + 1))) {
                    address = HTTPS.concat("://").concat(address);
                } else {
                    address = HTTP.concat("://").concat(address);
                }
            } else {
                address = HTTP.concat("://").concat(address);
            }
        }

        URL url = new URL(address);

        return url.getHost();

    }


    public static int getPortFromStringAddress(String address) throws MalformedURLException {

        assert !Assert.isEmpty(address);

        int port;

        URL url;

        if (address.contains(HTTP)) {
            url = new URL(address);
            port = url.getPort() == -1 ? 80 : url.getPort();
        } else if (address.contains(HTTPS)) {
            url = new URL(address);
            port = url.getPort() == -1 ? 443 : url.getPort();
        } else {
            if (address.contains(COLON)) {
                try {
                    port = Integer.parseInt(address.substring(address.lastIndexOf(COLON) + 1));
                } catch (Exception e) {
                    throw new IllegalArgumentException("address: " + address + ", after : should be number ... ");
                }
            } else {
                port = HTTP_PORT;
            }
        }

        return port;
    }


    public static String getPathFromStringAddress(String address) throws MalformedURLException {

        assert !Assert.isEmpty(address);

        if (!address.contains(HTTP) && !address.contains(HTTPS)) {
            // 判断是否https
            if (address.contains(COLON)) {
                if (HTTPS_PORT.equalsIgnoreCase(address.substring(address.lastIndexOf(COLON) + 1))) {
                    address = HTTPS.concat("://").concat(address);
                } else {
                    address = HTTP.concat("://").concat(address);
                }
            } else {
                address = HTTP.concat("://").concat(address);
            }
        }

        URL url = new URL(address);

        return url.getPath();

    }


}
