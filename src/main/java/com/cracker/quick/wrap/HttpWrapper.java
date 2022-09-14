package com.cracker.quick.wrap;


import com.cracker.quick.util.Assert;

import java.util.Map;

/**
 *
 * HttpWrapper: Http服务包裹类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpWrapper extends AbstractHttpWrapper {



    private String defaultMethod = HttpMethod.GET.name();
    private String defaultPath = "";

    @Override
    public void setRequestMethod(String method) {
        if (method != null && !method.isEmpty()) {
            defaultMethod = method;
        }
    }

    /**
     * GET请求直接把参数写在path上
     * @param path 请求路径
     */
    @Override
    public void setRequestPath(String path) {
        if (path != null && !path.isEmpty()) {
            defaultPath = path;
        }
    }

    @Override
    public boolean wrapHeader(Map<String, String> headerMap) {
        if (Assert.isNotNull(headerMap)) {
            getHeaderMap().putAll(headerMap);
        }
        return true;
    }

    @Override
    public boolean wrapBody(String reqData) {

        setHeader(String.format("%s %s%s", defaultMethod.toUpperCase(), defaultPath, " HTTP/1.1\r\n"));

        StringBuilder tmpBody = new StringBuilder();
        tmpBody.append(getHeader());

        String key, value;

        for (Map.Entry<String, String> entry : getHeaderMap().entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            tmpBody.append(key).append(": ").append(value).append("\r\n");
        }
        // Content-Length为必须
        if (!Assert.isEmpty(reqData)) {
            tmpBody.append("Content-Length: ").append(reqData.getBytes().length).append("\r\n");
            tmpBody.append("\r\n").append(reqData);
        } else {
            tmpBody.append("\r\n");
        }

        setBody(tmpBody.toString());

        return true;
    }


}
