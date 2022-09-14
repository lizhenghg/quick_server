package com.cracker.quick.connection;


import com.cracker.quick.exception.CommonException;

import java.net.InetSocketAddress;

/**
 *
 * 抽象工厂类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractFactory {

    public final Connection create(InetSocketAddress socketAddress, String requestPath) {
        return createConnection(socketAddress, requestPath);
    }

    /**
     * 创建Connection
     * @param socketAddress InetSocketAddress
     * @param requestPath requestPath
     * @throws CommonException CommonException
     * @return Connection
     */
    public abstract Connection createConnection(InetSocketAddress socketAddress, String requestPath) throws CommonException;

}
