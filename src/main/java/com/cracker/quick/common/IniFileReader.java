package com.cracker.quick.common;


import com.cracker.quick.exception.CommonException;
import com.cracker.quick.util.Assert;

import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * 初始化配置文件读取器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class IniFileReader {

    private static final Map<String, String> SETTING = new ConcurrentHashMap<>(2 << 5);

    private static final Integer LOCK = 1;
    
    private static volatile boolean bInit = false;

    private static String baseConfName;

    public static Map<String, String> getContainer() {
        return SETTING;
    }


    public static void initProperties(String confName) throws IOException {

        if (bInit) {
            return;
        }

        if (Assert.isEmpty(confName)) {
            return;
        }

        baseConfName = confName;

        InputStream inStream = loadFileFromOsSystemOrClassPath(baseConfName);

        synchronized (LOCK) {

            Properties properties = new Properties();
            properties.load(inStream);

            @SuppressWarnings("unchecked")
            Enumeration<String> propertyNames = (Enumeration<String>) properties.propertyNames();

            String key, value;

            while (propertyNames.hasMoreElements()) {
                key = propertyNames.nextElement().trim();
                value = properties.getProperty(key).trim();
                SETTING.put(key, value);
            }

            inStream.close();
        }

        bInit = true;

    }


    public static InputStream loadFileFromOsSystemOrClassPath(String path)
            throws FileNotFoundException {

        InputStream source;

        File file;
        file = new File(path);

        if (file.exists() && file.isFile()) {
            source = new FileInputStream(file);
            return source;
        }

        source = IniFileReader.class.getResourceAsStream(path);

        ClassLoader classLoader;

        if (source == null) {

            classLoader = getDefaultClassLoader();

            if (classLoader != null) {
                source = classLoader.getResourceAsStream(path);
            }

            if (source == null) {
                source = ClassLoader.getSystemResourceAsStream(path);
            }
        }

        if (source == null) {
            throw new FileNotFoundException(path + " can't be opened, because it doesn't exist ... ");
        }
        return source;
    }


    /**
     * 获取默认类加载器
     * @return 类加载器
     */
    public static ClassLoader getDefaultClassLoader() {

        ClassLoader classLoader = null;
        try {
            // 启动类加载器
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable e) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (classLoader == null) {
            // 扩展类加载器
            classLoader = IniFileReader.class.getClassLoader();
            // 应用程序类加载器
            if (classLoader == null) {
                try {
                    classLoader = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe it doesn't exist...
                }
            }
        }
        return classLoader;
    }

    private static boolean bDefaultValue = false;
    public static int iDefaultValue = -100;

    public static boolean getBooleanSetting(String key) {
        String value;
        if (Assert.isEmpty(value = SETTING.get(key))) {
            return bDefaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception ex) {
            throw new CommonException("value is not boolean", ex);
        }
    }

    public static int getIntSetting(String key) {
        String value;
        if (Assert.isEmpty(value = SETTING.get(key))) {
            return iDefaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            throw new CommonException("value is not int", ex);
        }
    }
}