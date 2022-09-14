package com.cracker.quick.server;

import com.cracker.quick.connection.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *
 * 抽象服务器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractServer {

    protected InetSocketAddress socketAddress;
    protected String requestPath;
    protected String serverName;

    private AbstractServer() {}

    protected AbstractServer(InetSocketAddress socketAddress, String requestPath, String serverName) {
        this.socketAddress = socketAddress;
        this.requestPath = requestPath;
        this.serverName = serverName;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }


    public Connection getConnection() throws IOException {
        throw new UnsupportedOperationException("unSupported Operation, let subClass do it ... ");
    }

}
