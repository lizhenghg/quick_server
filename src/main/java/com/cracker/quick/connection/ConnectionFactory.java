package com.cracker.quick.connection;



import com.cracker.quick.ClientGlobal;
import com.cracker.quick.exception.CommonException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 *
 * Connection工厂类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ConnectionFactory extends AbstractFactory {

    /**
     * 使用工厂模式创建Connection，这里需要传入InetSocketAddress和requestPath，寓意为需要外部xxxServer服务器类去调用，
     * 跟整个connection包解耦
     * @param socketAddress InetSocketAddress
     * @param requestPath requestPath
     * @throws CommonException CommonException
     * @return Connection
     */
    @Override
    public Connection createConnection(InetSocketAddress socketAddress, String requestPath) throws CommonException {

        try {
            Socket socket = new Socket();
            socket.setReuseAddress(ClientGlobal.g_socket_reuseAddress);
            socket.setKeepAlive(ClientGlobal.g_socket_keepAlive);
            socket.setSoTimeout(ClientGlobal.g_socket_soTimeOut);
            socket.connect(socketAddress, ClientGlobal.g_socket_connect_timeout);
            return new Connection(socket, socketAddress, requestPath);
        } catch (IOException e) {
            throw new CommonException(String.format("connect to server, ip:port = %s:%s, fails, error msg: %s",
                    socketAddress.getAddress().getHostAddress(), socketAddress.getPort(), e.getMessage()), e);
        }
    }

}