package com.cracker.quick.pool;


import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 线程池工具类: ThreadPoolUtil
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class ThreadPoolUtil {

    private static volatile ThreadPoolUtil mInstance;
    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    private int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maxPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 1;

    private TimeUnit unit = TimeUnit.HOURS;

    private ThreadPoolExecutor executor;

    private BlockingQueue<Runnable> linkQueue = new LinkedBlockingQueue<>(1024);
 
    private ThreadPoolUtil() {
        // 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行
        corePoolSize = Runtime.getRuntime().availableProcessors() << 1 + 1;
        maxPoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(
                // 当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                corePoolSize,
                // 然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                maxPoolSize,
                // 表示的是maximumPoolSize当中等待任务的存活时间
                keepAliveTime,
                unit,
                // 缓冲队列，用于存放等待任务，Linked的先进先出
                linkQueue,
                new DefaultThreadFactory(Thread.NORM_PRIORITY, "thread-pool-"),
                // 拒绝策略，直接抛弃任务，并抛出异常
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
 
    public static ThreadPoolUtil getInstance() {
        if (mInstance == null) {
            synchronized (ThreadPoolUtil.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPoolUtil();
                }
            }
        }
        return Objects.requireNonNull(mInstance);
    }
 
    /**
     * 执行任务
     * @param runnable Runnable
     */
    public void execute(Runnable runnable) {
        if (runnable != null) {
            executor.execute(runnable);
        }
    }
 
    /**
     * 移除任务
     *
     * @param runnable Runnable
     */
    public boolean remove(Runnable runnable) {
        if (runnable != null) {
            return executor.remove(runnable);
        }
        return false;
    }
 
    private static class DefaultThreadFactory implements ThreadFactory {
        /**
         * 线程池的计数
         */
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        /**
         * 线程的计数
         */
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;
        private final int threadPriority;
 
        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            this.namePrefix = threadNamePrefix + POOL_NUMBER.getAndIncrement() + "-thread-";
        }
 
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            // 返回True该线程就是守护线程
            // 守护线程应该永远不去访问固有资源，如：数据库、文件等。因为它会在任何时候甚至在一个操作的中间发生中断。
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }
            thread.setPriority(threadPriority);
            return thread;
        }
    }
    
    public static void main(String[] args) {
    	try {
    		for (int i = 0; i < 20; i++) {
    			ThreadPoolUtil threadPool = ThreadPoolUtil.getInstance();
    			threadPool.execute(() -> {
                    System.out.println(Thread.currentThread()+"：开始。。");
                    try {
                        Thread.sleep(1000 << 1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread()+"：结束。。");
                });
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}