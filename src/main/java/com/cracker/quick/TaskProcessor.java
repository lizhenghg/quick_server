package com.cracker.quick;


import com.cracker.quick.connection.Connection;
import com.cracker.quick.server.AbstractServer;
import com.cracker.quick.server.ServerManager;
import com.cracker.quick.wrap.AbstractHttpWrapper;
import com.cracker.quick.wrap.ByteWrapper;
import com.cracker.quick.wrap.HttpMethod;
import com.cracker.quick.wrap.HttpWrapper;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 *
 * 任务执行器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class TaskProcessor implements Runnable {



    @Override
    public void run() {

        AbstractHttpWrapper httpWrapper = new HttpWrapper();

        AbstractServer server = ServerManager.getInstance().getServer();

        Random random = new Random();

        Connection connection = null;

        try {

            httpWrapper.setRequestMethod(HttpMethod.GET.name());
            httpWrapper.setRequestPath(server.getRequestPath() + "?wd=" + random.nextInt(100));
            httpWrapper.wrapBody(null);

            connection = server.getConnection();

            if (connection != null) {
                connection.getOutputStream().write(httpWrapper.getBody().getBytes(StandardCharsets.UTF_8));
                // 获取返回头部信息，httpCode == 200才执行
                if (ByteWrapper.headerParse(connection.getInputStream())) {
                    System.out.println("receive retCode: httpCode == 200");
                }
                System.out.println("receive is: " + ByteWrapper.bodyParse(connection.getInputStream()));
            }
        } catch (Exception ex) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                connection = null;
            }
        } finally {
            if (connection != null) {
                try {
                    connection.release();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}