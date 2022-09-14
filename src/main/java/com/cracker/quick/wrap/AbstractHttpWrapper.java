package com.cracker.quick.wrap;


import com.cracker.quick.util.Pair;


import java.util.HashMap;
import java.util.Map;


/**
 *
 * AbstractHttpWrapper: 超级简单的抽象Http包裹类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-03-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractHttpWrapper {

    protected static Map<String, String> headerMap = new HashMap<>();
    protected String header;
    protected String body;

    private static final Pair<String, String> CACHE_CONTROL_PAIR = new Pair<>("Cache-Control", "no-cache");
    private static final Pair<String, String> PRAGMA_PAIR = new Pair<>("Pragma", "no-cache");
    private static final Pair<String, String> USER_AGENT_PAIR = new Pair<>("User-Agent", "JavaSocket/" + System.getProperty("java.version"));
    private static final Pair<String, String> ACCEPT_PAIR = new Pair<>("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
    private static final Pair<String, String> CONNECTION_PAIR = new Pair<>("Connection", "keep-alive");


    public AbstractHttpWrapper() {
        initDefaultValue();
    }

    private void initDefaultValue() {

        initDefaultHeader();
    }


    private void initDefaultHeader() {

        headerMap.put(CACHE_CONTROL_PAIR.getKey(), CACHE_CONTROL_PAIR.getValue());
        headerMap.put(PRAGMA_PAIR.getKey(), PRAGMA_PAIR.getValue());
        headerMap.put(USER_AGENT_PAIR.getKey(), USER_AGENT_PAIR.getValue());
        headerMap.put(ACCEPT_PAIR.getKey(), ACCEPT_PAIR.getValue());
        headerMap.put(CONNECTION_PAIR.getKey(), CONNECTION_PAIR.getValue());
    }
    
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {

        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        AbstractHttpWrapper.headerMap = headerMap;
    }

    /**
     * header包裹
     * @param headerMap 待处理的header
     * @return boolean
     * @throws Exception Exception
     */
    public abstract boolean wrapHeader(Map<String, String> headerMap) throws Exception;

    /**
     * body包裹
     * @param reqData 发送数据
     * @return boolean
     * @throws Exception Exception
     */
    public abstract boolean wrapBody(String reqData) throws Exception;

    /**
     * 设置请求方法
     * @param method 请求方法
     * @throws Exception Exception
     */
    public abstract void setRequestMethod(String method) throws Exception;

    /**
     * 设置请求路径
     * @param path 请求路径
     * @throws Exception Exception
     */
    public abstract void setRequestPath(String path) throws Exception;

}
