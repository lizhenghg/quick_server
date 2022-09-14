package com.cracker.quick;

import com.cracker.quick.connection.AbstractFactory;
import com.cracker.quick.connection.Connection;
import com.cracker.quick.connection.ConnectionFactory;
import com.cracker.quick.connection.ConnectionManager;
import com.cracker.quick.server.AbstractServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *
 * 具体业务服务器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class BusinessServer extends AbstractServer {

    public BusinessServer(InetSocketAddress socketAddress, String requestPath, String serverName) {
        super(socketAddress, requestPath, serverName);
    }

    @Override
    public Connection getConnection() throws IOException {
        Connection connection;
        if (ClientGlobal.g_socket_pool_enabled) {
            connection = ConnectionManager.getInstance().getConnection(socketAddress, requestPath);
        } else {
            AbstractFactory factory = new ConnectionFactory();
            connection = factory.create(socketAddress, requestPath);
        }
        return connection;
    }
}