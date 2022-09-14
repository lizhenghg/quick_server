package com.cracker.quick;



import com.cracker.quick.common.IniFileReader;
import com.cracker.quick.server.ServerManager;

import java.io.IOException;
import java.util.Map;

/**
 *
 * 全局数据配置类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ClientGlobal {

    /**
     * 下面为.properties配置文件里的变量名
     */
    public static final String PROP_KEY_SERVERS = "quick.server.servers";
    public static final String PROP_KEY_SOCKET_REUSE_ADDRESS = "quick.server.socket_reuse_address";
    public static final String PROP_KEY_SOCKET_SO_TIMEOUT = "quick.server.socket_soTimeout";
    public static final String PROP_KEY_SOCKET_KEEP_ALIVE = "quick.server.socket_keepAlive";
    public static final String PROP_KEY_SOCKET_MAX_IDLE_TIME = "quick.server.socket_max_idle_time";
    public static final String PROP_KEY_SOCKET_POOL_ENABLED = "quick.server.socket_pool_enabled";
    public static final String PROP_KEY_SOCKET_CONNECT_TIMEOUT = "quick.server.socket_connect_timeout";
    public static final String PROP_KEY_SOCKET_POOL_MAX_COUNT_PER_ENTITY = "quick.server.socket_pool_max_count_per_entity";
    public static final String PROP_KEY_SOCKET_POOL_MAX_WAKEUP_TIME = "quick.server.socket_pool_max_wakeup_time";


    /**
     * 如下为默认值
     */
    public final static String DEFAULT_SERVERS = "127.0.0.1:3701";
    public final static boolean DEFAULT_SOCKET_REUSE_ADDRESS = false;
    /**
     * 默认socket调用read()时，等待的最长时间，单位：second
     */
    public final static int DEFAULT_SOCKET_SO_TIMEOUT = 5;
    public final static boolean DEFAULT_SOCKET_KEEP_ALIVE = false;
    public final static int DEFAULT_SOCKET_MAX_IDLE_TIME = 6;
    public final static boolean DEFAULT_SOCKET_POOL_ENABLED = false;
    public final static int DEFAULT_SOCKET_CONNECT_TIMEOUT = 5;
    public final static int DEFAULT_SOCKET_POOL_MAX_COUNT_PER_ENTITY = 10;
    public final static int DEFAULT_SOCKET_POOL_MAX_WAKEUP_TIME = 1;



    /**
     * 如下为正式使用值
     */
    public static String g_servers = DEFAULT_SERVERS;
    public static boolean g_socket_reuseAddress = DEFAULT_SOCKET_REUSE_ADDRESS;
    /**
     * 单位：millisecond
     */
    public static int g_socket_soTimeOut = DEFAULT_SOCKET_SO_TIMEOUT * 1000;
    public static boolean g_socket_keepAlive = DEFAULT_SOCKET_KEEP_ALIVE;
    /**
     * 单位：millisecond
     */
    public static int g_socket_maxIdleTime = DEFAULT_SOCKET_MAX_IDLE_TIME * 1000;
    public static boolean g_socket_pool_enabled = DEFAULT_SOCKET_POOL_ENABLED;
    /**
     * 单位：millisecond
     */
    public static int g_socket_connect_timeout = DEFAULT_SOCKET_CONNECT_TIMEOUT * 1000;
    public static int g_socket_pool_max_count_per_entity = DEFAULT_SOCKET_POOL_MAX_COUNT_PER_ENTITY;
    /**
     * 单位：millisecond
     */
    public static int g_socket_pool_max_wakeup_time = DEFAULT_SOCKET_POOL_MAX_WAKEUP_TIME * 1000;





    /**
     * 在这里初始化全局配置
     * @param confName 配置文件名称
     */
    public static void init(String confName) throws IOException {

        IniFileReader.initProperties(confName);
        Map<String, String> containMap = IniFileReader.getContainer();
        String serverStrings;
        int socketSoTimeOut;
        int socketMaxIdleTime;
        int socketConnectTimeOut;
        int socketPoolMaxCountPerEntity;
        int socketPoolMaxWakeupTime;



        if ((serverStrings = containMap.get(PROP_KEY_SERVERS)) != null
                && !serverStrings.isEmpty()) {
            g_servers = serverStrings;
        }

        g_socket_reuseAddress = IniFileReader.getBooleanSetting(PROP_KEY_SOCKET_REUSE_ADDRESS);

        if ((socketSoTimeOut = IniFileReader.getIntSetting(PROP_KEY_SOCKET_SO_TIMEOUT)) != IniFileReader.iDefaultValue) {
            g_socket_soTimeOut = socketSoTimeOut * 1000;
        }

        g_socket_keepAlive = IniFileReader.getBooleanSetting(PROP_KEY_SOCKET_KEEP_ALIVE);

        if ((socketMaxIdleTime = IniFileReader.getIntSetting(PROP_KEY_SOCKET_MAX_IDLE_TIME)) != IniFileReader.iDefaultValue) {
            g_socket_maxIdleTime = socketMaxIdleTime * 1000;
        }

        g_socket_pool_enabled = IniFileReader.getBooleanSetting(PROP_KEY_SOCKET_POOL_ENABLED);

        if ((socketConnectTimeOut = IniFileReader.getIntSetting(PROP_KEY_SOCKET_CONNECT_TIMEOUT)) != IniFileReader.iDefaultValue) {
            g_socket_connect_timeout = socketConnectTimeOut * 1000;
        }

        if ((socketPoolMaxCountPerEntity = IniFileReader.getIntSetting(PROP_KEY_SOCKET_POOL_MAX_COUNT_PER_ENTITY)) != IniFileReader.iDefaultValue) {
            g_socket_pool_max_count_per_entity = socketPoolMaxCountPerEntity;
        }

        if ((socketPoolMaxWakeupTime = IniFileReader.getIntSetting(PROP_KEY_SOCKET_POOL_MAX_WAKEUP_TIME)) != IniFileReader.iDefaultValue) {
            g_socket_pool_max_wakeup_time = socketPoolMaxWakeupTime * 1000;
        }
        // 初始化Server
        ServerManager.init(g_servers);
    }
}
