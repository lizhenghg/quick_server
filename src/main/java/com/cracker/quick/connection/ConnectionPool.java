package com.cracker.quick.connection;


import com.cracker.quick.exception.CommonException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * 连接池，主要是连接ConnectionCalculator
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class ConnectionPool {

    /**
     * key: ip:port:requestPath, value: ConnectionCalculator
     * 为什么需要ConnectionCalculator？因为不同的ip:port:requestPath需要处理一些关于Connection的关闭、释放
     */
    private final static Map<String, ConnectionCalculator> CP = new ConcurrentHashMap<>();

    /**
     * 只允许：同一个类、同一个包中的类访问
     */
    ConnectionPool() {}

    public Connection getConnection(InetSocketAddress socketAddress, String requestPath) throws IOException {
        if (socketAddress == null || requestPath == null) {
            return null;
        }
        String key = getKey(socketAddress, requestPath);
        ConnectionCalculator connectionCalculator = CP.get(key);
        if (connectionCalculator == null) {
            // 避免在执行put之前，已经有线程put进去了。在这里双重保险判断
            // 在单例中经常使用
            synchronized (ConnectionPool.class) {
                connectionCalculator = CP.get(key);
                if (connectionCalculator == null) {
                    connectionCalculator = new ConnectionCalculator(socketAddress, requestPath);
                    CP.put(key, connectionCalculator);
                }
            }
        }
        return connectionCalculator.getConnection();
    }


    public void releaseConnection(Connection connection) throws IOException {
        if (connection == null) {
            return;
        }
        String key = getKey(connection.getInetSocketAddress(), connection.getRequestPath());
        ConnectionCalculator connectionCalculator = CP.get(key);
        if (connectionCalculator == null) {
            connection.closeDirectly();
            throw new CommonException("releaseConnection error, key: " + key + " couldn't find specified connectionCalculator");
        }
        connectionCalculator.releaseConnection(connection);
    }


    public void closeConnection(Connection connection) throws IOException {
        if (connection == null) {
            return;
        }
        String key = getKey(connection.getInetSocketAddress(), connection.getRequestPath());
        ConnectionCalculator connectionCalculator = CP.get(key);
        if (connectionCalculator == null) {
            connection.closeDirectly();
            throw new CommonException("closeConnection error, key: " + key + " couldn't find specified connectionCalculator");
        }
        connectionCalculator.closeConnection(connection);
    }


    public String getKey(InetSocketAddress socketAddress, String requestPath) {
        if (socketAddress == null || requestPath == null) {
            return null;
        }
        return String.format("%s:%s:%s", socketAddress.getAddress().getHostAddress(),
                socketAddress.getPort(), requestPath);
    }


    @Override
    public String toString() {
        if (!CP.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, ConnectionCalculator> calculatorEntry : CP.entrySet()) {
                builder.append("key:[")
                        .append(calculatorEntry.getKey())
                        .append(" ]-------- entry:")
                        .append(calculatorEntry.getValue())
                        .append("\n");
            }
            return builder.toString();
        }
        return null;
    }
}
