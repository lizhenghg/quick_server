package com.cracker.quick.server;

import com.cracker.quick.exception.CommonException;
import com.cracker.quick.lock.NonDistributedLockClient;
import com.cracker.quick.util.Assert;
import com.cracker.quick.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 *
 * 服务器管理类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class ServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManager.class);

    private static ServerManager serverManager;

    private static volatile boolean bInit = false;

    private static NonDistributedLockClient lockClient = NonDistributedLockClient.getInstance();

    private ServerGroup serverGroup;

    private ServerManager() {}

    public static void init(final String serverStrings) throws MalformedURLException {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("init abstractServer ... ");
        }

        if (bInit) {
            return;
        }

        if (Assert.isEmpty(serverStrings)) {
            return;
        }

        try {

            if (lockClient.lock(serverStrings)) {

                serverManager = new ServerManager(serverStrings);

            } else {
                throw new CommonException("ServerManager lock key: " + serverStrings + " failed ... ");
            }

        } finally {
            lockClient.unlock(serverStrings);
        }

        bInit = true;
    }

    public static ServerManager getInstance() {
        return Objects.requireNonNull(serverManager);
    }

    private ServerManager(String serverStrings) throws MalformedURLException {
        initServerGroup(serverStrings);
    }


    private void initServerGroup(String serverStrings) throws MalformedURLException {

        String[] servers = serverStrings.split(",");

        InetSocketAddress[] emailServerAddress = new InetSocketAddress[servers.length];
        String[] requestPath = new String[servers.length];
        String[] serverName = new String[servers.length];

        int index = 0;
        InetSocketAddress socketAddress;

        for (String server : servers) {
            socketAddress = new InetSocketAddress(NetworkUtil.getHostFromStringAddress(server),
                    NetworkUtil.getPortFromStringAddress(server));
            emailServerAddress[index] = socketAddress;
            requestPath[index] = NetworkUtil.getPathFromStringAddress(server);
            serverName[index] = String.format("local_business_server_%s", ++index);
        }

        serverGroup = new ServerGroup(emailServerAddress, requestPath, serverName);
    }



    public AbstractServer getServer() {
        return this.serverGroup.getServer();
    }

    public AbstractServer getServer(int index) {
        return this.serverGroup.getServer(index);
    }

}
