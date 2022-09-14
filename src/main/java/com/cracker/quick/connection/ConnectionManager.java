package com.cracker.quick.connection;


import com.cracker.quick.exception.CommonException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 *
 * 连接管理类，守护连接池
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ConnectionManager {

    private static volatile ConnectionManager instance;

    private final ConnectionPool connectionPool;

    public static ConnectionManager getInstance() {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    instance = new ConnectionManager();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    private ConnectionManager() {
        this.connectionPool = new ConnectionPool();
    }

    public Connection getConnection(InetSocketAddress socketAddress, String requestPath) throws IOException {
        if (socketAddress == null || requestPath == null) {
            throw new CommonException("couldn't get connection from pool, InetSocketAddress or requestPath is null");
        }
        return this.connectionPool.getConnection(socketAddress, requestPath);
    }


    public void releaseConnection(Connection connection) throws IOException {
        if (connection == null) {
            return;
        }
        this.connectionPool.releaseConnection(connection);
    }

    public void closeConnection(Connection connection) throws IOException {
        if (connection == null) {
            return;
        }
        this.connectionPool.closeConnection(connection);
    }
}
