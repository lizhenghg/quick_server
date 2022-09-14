package com.cracker.quick.server;

import com.cracker.quick.BusinessServer;

import java.net.InetSocketAddress;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 服务器组群
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ServerGroup implements Cloneable {

    private final InetSocketAddress[] servers;
    private final String[] requestPaths;
    private final String[] serverNames;

    private int currentIndex;
    private final int queueSize;

    /**
     * AbstractServer组件缓存器，使用些缓存技术，高大上点
     */
    private final Map<Integer, AbstractServer> serverMap = new ConcurrentHashMap<>();

    ServerGroup(InetSocketAddress[] servers, String[] requestPaths, String[] serverNames) {
        this.servers = servers;
        this.requestPaths = requestPaths;
        this.serverNames = serverNames;
        this.queueSize = this.servers.length;
    }

    public AbstractServer getServer() {
        int index = selectIndex();
        if (!this.serverMap.containsKey(index)) {
            // 避免在put之前，已经有线程put进去了。在这里双重保险判断
            synchronized (ServerGroup.class) {
                if (!this.serverMap.containsKey(index)) {
                    AbstractServer server = new BusinessServer(this.servers[index], this.requestPaths[index], this.serverNames[index]);
                    this.serverMap.put(index, server);
                }
            }
        }
        return this.serverMap.get(index);
    }


    public AbstractServer getServer(int index) {

        if (!this.serverMap.containsKey(index)) {
            // 避免在put之前，已经有线程put进去了。在这里双重保险判断
            synchronized (ServerGroup.class) {
                if (!this.serverMap.containsKey(index)) {
                    AbstractServer server = new BusinessServer(this.servers[index], this.requestPaths[index], this.serverNames[index]);
                    this.serverMap.put(index, server);
                }
            }
        }
        return this.serverMap.get(index);
    }




    /**
     * 选择一个范围内的位置
     * 不要求绝对的平均分布，不做去重处理，不做分布式同步处理
     * 算法：轮询
     * synchronized可加可不加，不加追求性能,加追求极致安全
     * @return 队列数组下标
     */
    private int selectIndex() {
        // 先取一个副本
        // 直接操作this.currentIndex有范围溢出风险
        int currentIndex = this.currentIndex;

        if (currentIndex >= this.queueSize) {
            currentIndex = 0;
            this.currentIndex = 0;
        }
        this.currentIndex++;
        return currentIndex;
    }
}
