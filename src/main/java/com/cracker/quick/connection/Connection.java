package com.cracker.quick.connection;


import com.cracker.quick.ClientGlobal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * 手写连接类，原则：一个Connection对应一个Socket
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class Connection {

    private final Socket socket;
    private final InetSocketAddress inetSocketAddress;
    private String requestPath;
    private boolean needActiveTest = false;

    private Long lastAccessTime = System.currentTimeMillis();

    public Connection(Socket socket, InetSocketAddress inetSocketAddress, String requestPath) {
        this.socket = socket;
        this.inetSocketAddress = inetSocketAddress;
        this.requestPath = requestPath;
    }

    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    public Long getLastAccessTime() {
        return this.lastAccessTime;
    }
    public void setLastAccessTime(Long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public String getRequestPath() {
        return requestPath;
    }
    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public boolean isNeedActiveTest() {
        return needActiveTest;
    }
    public void setNeedActiveTest(boolean needActiveTest) {
        this.needActiveTest = needActiveTest;
    }

    /**
     * 释放Connection
     * @throws IOException IOException
     */
    public void release() throws IOException {
        if (ClientGlobal.g_socket_pool_enabled) {
            ConnectionManager.getInstance().releaseConnection(this);
        } else {
            closeDirectly();
        }
    }


    /**
     * 关闭Connection
     * @throws IOException IOException
     */
    public void close() throws IOException {
        if (ClientGlobal.g_socket_pool_enabled) {
            ConnectionManager.getInstance().closeConnection(this);
        } else {
            closeDirectly();
        }
    }

    /**
     * 直接关闭socket
     * @throws IOException IOException
     */
    public void closeDirectly() throws IOException {
        this.socket.close();
    }



    /**
     * 判断是否连接状态
     * @return true is connected, false is not yet.
     */
    public boolean isConnected() {
        return this.socket.isConnected();
    }

    /**
     * 判断该connection是否可用状态
     * @return true is available, false is not available
     */
    public boolean isAvailable() {
        if (isConnected()) {
            if (this.socket.getPort() == 0) {
                return false;
            }
            if (this.socket.getInetAddress() == null) {
                return false;
            }
            if (this.socket.getRemoteSocketAddress() == null) {
                return false;
            }
            if (this.socket.isInputShutdown()) {
                return false;
            }
            return !this.socket.isOutputShutdown();
        }
        return false;
    }


    /**
     * 活跃检测
     * 这里简单处理。
     * 处理原理很简单，直接向待检测的连接发送数据，查看返回响应即可
     * @return true is active, false is not
     */
    public boolean activeTest() {
        return true;
    }
}