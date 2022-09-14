package com.cracker.quick.connection;


import com.cracker.quick.ClientGlobal;
import com.cracker.quick.exception.CommonException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 连接计算(操作器)
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ConnectionCalculator {

    private InetSocketAddress socketAddress;
    private String requestPath;

    /**
     * 总Connection计数器
     */
    private final AtomicInteger totalCount = new AtomicInteger();

    /**
     * 空闲Connection计数器
     */
    private final AtomicInteger freeCount = new AtomicInteger();

    /**
     * 公平锁
     */
    private final Lock lock = new ReentrantLock(true);

    private final Condition condition = lock.newCondition();

    /**
     * 空闲可使用的Connection
     */
    private final LinkedList<Connection> freeConnections = new LinkedList<>();



    public ConnectionCalculator(InetSocketAddress socketAddress, String requestPath) {
        this.socketAddress = socketAddress;
        this.requestPath = requestPath;
    }

    private ConnectionCalculator() {}

    /**
     * 获取Connection
     * @return Connection
     */
    public Connection getConnection() throws IOException {
        lock.lock();
        try {
            Connection connection;
            while (true) {
                if (freeCount.get() > 0) {
                    freeCount.decrementAndGet();
                    connection = freeConnections.poll();
                    // connection == null always false
                    if (connection == null) {
                        continue;
                    }
                    // connection是否可用状态，是否已超时
                    if (!connection.isAvailable() ||
                            (System.currentTimeMillis() - connection.getLastAccessTime()) > ClientGlobal.g_socket_maxIdleTime) {
                        closeConnection(connection);
                        continue;
                    }
                    // connection是否需要活跃检测
                    if (connection.isNeedActiveTest()) {
                        if (!connection.activeTest()) {
                            closeConnection(connection);
                        } else {
                            connection.setNeedActiveTest(false);
                        }
                    }
                } else if (ClientGlobal.g_socket_pool_max_count_per_entity == 0
                        || totalCount.get() < ClientGlobal.g_socket_pool_max_count_per_entity) {
                    AbstractFactory connectionFactory = new ConnectionFactory();
                    connection = connectionFactory.create(this.socketAddress, this.requestPath);
                    totalCount.incrementAndGet();
                } else {
                    try {
                        if (condition.await(ClientGlobal.g_socket_pool_max_wakeup_time, TimeUnit.MILLISECONDS)) {
                            // wait single success, if not, throw InterruptedException
                            continue;
                        }
                        throw new CommonException("connect to server " + socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort() + " fail, wait_time > " + ClientGlobal.g_socket_pool_max_wakeup_time + "ms");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        throw new CommonException("connect to server " + socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort() + " fail, error msg: " + ex.getMessage());
                    }
                }
                return connection;
            }
        } finally {
            lock.unlock();
        }

    }


    /**
     * 释放连接
     * @param connection Connection
     */
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        lock.lock();
        try {
            connection.setLastAccessTime(System.currentTimeMillis());
            freeConnections.add(connection);
            freeCount.incrementAndGet();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭连接
     * @param connection Connection
     * @throws IOException 直接抛异常
     */
    public void closeConnection(Connection connection) throws IOException {
        if (connection == null) {
            return;
        }
        totalCount.decrementAndGet();
        connection.closeDirectly();
    }

    /**
     * 给当前ip:port:requestPath所对应的全部Connection
     * 设置活跃检测
     */
    public void setActiveTestFlag() {
        if (freeCount.get() > 0) {
            lock.lock();
            try {
                for (Connection freeConnection : freeConnections) {
                    freeConnection.setNeedActiveTest(true);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return "ConnectionCalculator{" +
                "ip:port='" + socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort() +
                ", requestPath=" + requestPath +
                ", totalCount=" + totalCount +
                ", freeCount=" + freeCount +
                ", freeConnections =" + freeConnections +
                '}';
    }
}
