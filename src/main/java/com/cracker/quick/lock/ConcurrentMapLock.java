package com.cracker.quick.lock;

import com.cracker.quick.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步锁操作接口实现类，非分布式锁
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ConcurrentMapLock implements ILock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentMapLock.class);

    private final Map<String, Object> lockMap;

    private static final Integer DEFAULT_LOCK_VALUE = 1;

    public ConcurrentMapLock() {
        this.lockMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean lock(String key) {
        return lock(key, DEFAULT_MAX_BLOCKING_TIME);
    }

    @Override
    public boolean lock(String key, long expireTime) {

        if (expireTime <= 0) {
            throw new IllegalArgumentException("lock_blocking_time is negative!");
        }

        Assert.notEmpty(key, "lock key is negative!");

        int retryingCount = ((int) (expireTime / DEFAULT_BLOCKING_INTERVAL)) + 1;

        while (true) {
            if (this.lockMap.putIfAbsent(key, DEFAULT_LOCK_VALUE) == null) {
                return true;
            }
            if (--retryingCount <= 0) {
                return false;
            }

            try {
                Thread.sleep(DEFAULT_BLOCKING_INTERVAL);
            } catch (InterruptedException e) {
                LOGGER.error("get lock: {} failed, err: {}", key, e.getMessage(), e);
                return false;
            }
        }

    }

    @Override
    public boolean lockWithoutBlock(String key) {
        Assert.notEmpty(key, "lockWithoutBlock error: key is empty!");
        return this.lockMap.putIfAbsent(key, DEFAULT_LOCK_VALUE) == null;
    }

    @Override
    public void unlock(String key) {
        Assert.notEmpty(key, "unlock error: key is empty!");
        if (!this.lockMap.remove(key, DEFAULT_LOCK_VALUE)) {
            throw new IllegalStateException("unlock error, remove key: " + key + " failed ... ");
        }
    }
}
