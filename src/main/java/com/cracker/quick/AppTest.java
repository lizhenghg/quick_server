package com.cracker.quick;


public class AppTest {


    /**
     * 经过实测，使用connection连接池，100次http request，平均耗时3.5秒
     * @throws Exception unexpected exception
     */
    // @Test
    public void httpSimpleTest() throws Exception {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            HttpClient.getInstance().doGet();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("============================================= it takes time " + (endTime -startTime) + " ms") ;
    }

    /**
     * Junit不支持线程池的Test，请自行创建main方法测试
     */
    // @Test
    public void httpBatchTest() {
        HttpClient.getInstance().doGetForBatch(100);
    }

    /**
     * 经过实测，使用connection连接池和线程池，100次http request，平均耗时3秒
     */
    public static void main(String[] args) {
        HttpClient.getInstance().doGetForBatch(100);
    }
}
