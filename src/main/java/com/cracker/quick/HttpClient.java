package com.cracker.quick;

import com.cracker.quick.common.IniFileReader;
import com.cracker.quick.connection.Connection;
import com.cracker.quick.pool.ThreadPoolUtil;
import com.cracker.quick.server.AbstractServer;
import com.cracker.quick.server.ServerManager;
import com.cracker.quick.wrap.AbstractHttpWrapper;
import com.cracker.quick.wrap.ByteWrapper;
import com.cracker.quick.wrap.HttpMethod;
import com.cracker.quick.wrap.HttpWrapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

/**
 *
 * 对外暴露服务类: HttpClient
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    private static volatile HttpClient instance;

    private final static Integer SYNC_OBJECT = 1;

    private static final String CONF_NAME = "server.properties";

    private static final String SERVER_LOG4J = "log4j.xml";

    private HttpClient() {
        init();
    }

    public static HttpClient getInstance() {
        if (instance == null) {
            synchronized (SYNC_OBJECT) {
                if (instance == null) {
                    instance = new HttpClient();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }


    private void init() {
        DOMConfigurator domConfigurator = new DOMConfigurator();
        try {
            domConfigurator.doConfigure(IniFileReader.loadFileFromOsSystemOrClassPath(SERVER_LOG4J), LogManager.getLoggerRepository());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("init log succeed ... ");
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("init conf: {}", CONF_NAME);
        }

        try {
            ClientGlobal.init(CONF_NAME);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 获取服务器
     * @return AbstractServer
     */
    private AbstractServer getServer() {
        return ServerManager.getInstance().getServer();
    }

    /**
     * 对外使用的方法，这里简单处理
     */
    public void doGet() throws Exception {

        Connection connection = null;
        AbstractHttpWrapper httpWrapper = new HttpWrapper();

        AbstractServer server = getServer();

        Random random = new Random();

        httpWrapper.setRequestMethod(HttpMethod.GET.name());
        httpWrapper.setRequestPath(server.getRequestPath() + "?wd=" + random.nextInt(100));
        httpWrapper.wrapBody(null);


        try {
            connection = server.getConnection();

            if (connection != null) {
                connection.getOutputStream().write(httpWrapper.getBody().getBytes(StandardCharsets.UTF_8));
                // 获取返回头部信息，httpCode == 200才执行
                if (ByteWrapper.headerParse(connection.getInputStream())) {
                    System.out.println("receive retCode: httpCode == 200");
                }
                System.out.println("ret is: " + ByteWrapper.bodyParse(connection.getInputStream()));
            }
        } catch (Exception ex) {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } finally {
            if (connection != null) {
                connection.release();
            }
        }

    }

    /**
     * 对外使用的方法，这里简单处理
     */
    public void doGetForBatch(int times) {
        if (times <= 0) {
            throw new IllegalArgumentException("times must be number more than zero");
        }
        for (int i = 0; i < times; ++i) {
            ThreadPoolUtil.getInstance().execute(new TaskProcessor());
        }
    }

    public static void main(String[] args) {
        HttpClient.getInstance().doGetForBatch(100);
    }
}