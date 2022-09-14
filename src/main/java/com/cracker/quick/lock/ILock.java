package com.cracker.quick.lock;

/**
 * 同步锁操作接口
 * 锁住指定的String
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2021-05-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface ILock {

    /**
     * 最大默认堵塞时间，单位是毫秒。这里默认10秒
     */
    long DEFAULT_MAX_BLOCKING_TIME = 625L << 4;

    /**
     * 默认堵塞间隔，单位是毫秒。这里默认1秒
     */
    long DEFAULT_BLOCKING_INTERVAL = 125L << 3;

    /**
     * 堵塞锁，最大堵塞时间为默认值
     * @param key 待锁住的key
     * @return true means lock succeed, or means lock fail
     */
    boolean lock(String key);

    /**
     * 堵塞锁，最大堵塞时间为expireTime
     * @param key 待锁住的key
     * @param expireTime 超时时间
     * @return true means lock succeed, or means lock fail
     */
    boolean lock(String key, long expireTime);

    /**
     * 非堵塞锁，获取不到就立即返回
     * @param key key 待锁住的key
     * @return true means lock succeed, or means lock fail
     */
    boolean lockWithoutBlock(String key);

    /**
     * 解锁
     * @param key 待解锁的key
     */
    void unlock(String key);
}
