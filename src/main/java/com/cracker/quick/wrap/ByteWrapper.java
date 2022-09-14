package com.cracker.quick.wrap;


import com.cracker.quick.exception.CommonException;
import com.cracker.quick.util.Assert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;


/**
 *
 * ByteWrapper: 字节包裹类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2022-06-24
 * <br/>jdk版本：1.8
 * <br/>=================================
 *
 * 如下为常见response响应报文（Content-Type为application/json）
 * start ------>
 * HTTP/1.1 200 OK
 * x-proxy-by: TIF-APIGate
 * Date: Thu, 03 Mar 2022 07:46:45 GMT
 * Content-Type: application/json; charset=utf-8
 * Content-Length: 59
 * Connection: keep-alive
 * Rpcret: 0
 * Access-Control-Allow-Origin: *
 * Access-Control-Allow-Headers: Origin,Authorization,Access-Control-Allow-Origin,Access-Control-Allow-Headers,Content-Type,SID,yst-pre-release
 * Access-Control-Allow-Credentials: true
 * x-tif-target-nonce: 0100007f:017f4ebed602:09860b
 *
 * {"data":{},"errcode": 0,"errmsg":"", "hint":"tl5nld9xnsFd"}
 * end ------<
 *
 */
public class ByteWrapper {

    private static final int HTTP_RESPONSE_BLANK_NUM = 2;

    private static final int HTTP_OK_CODE = 200;

    /**
     * 解析response响应报文
     * @param source InputStream
     * @return true is ok, false is internal server exception
     * @throws IOException IOException
     */
    public static boolean headerParse(InputStream source) throws IOException {

        byte[] headers = new byte[12];

        int bytes;


        if ((bytes = source.read(headers)) == -1) {
            throw new CommonException("read nothing from system inputStream ... ");
        }
        if (bytes != headers.length) {
            throw new IOException("receive package size " + bytes + " != " + headers.length);
        }
        // HTTP/1.1 200
        String headerStr = new String(headers, StandardCharsets.UTF_8).trim();

        String[] arrayHeader = headerStr.split(" ");

        if (arrayHeader.length != HTTP_RESPONSE_BLANK_NUM) {
            throw new IllegalArgumentException("http response message error, should be (protocol responseCode)");
        }
        if (Integer.parseInt(arrayHeader[1]) != HTTP_OK_CODE) {
            System.out.println("receive ret: httpCode == " + Integer.parseInt(arrayHeader[1]));
            return false;
        }
        return true;
    }

    /**
     * 解析response报文，这里只解析
     * Content-Type: application/json; charset=utf-8
     * @param bodyStream body流
     * @return String
     * @throws IOException IOException
     */
    public static String bodyParse(InputStream bodyStream) throws IOException {

        if (bodyStream == null) {
            throw new IOException("body stream is null");
        }

        // 这里根据实际情况决定开辟内存大小，理论上比返回字节数组大一点好
        byte[] src = new byte[1024];
        byte[] dst;

        int bytes;
        int totalCapacity = 0;

        LinkedList<byte[]> byteArray = new LinkedList<>();

        // 这里不使用bodyStream.available()，原因：可能存在数值为0但真实存在数据情况
        // read方法是操作系统处理的，有可能你调用available()的时候，人家还在等待数据的到来
        while ((bytes = bodyStream.read(src)) > -1) {

            totalCapacity += bytes;
            dst = new byte[bytes];
            // 复制字节数组
            System.arraycopy(src, 0, dst, 0, bytes);
            byteArray.add(dst);

            // 条件成立，证明已经读取完毕
            if (src.length > bytes) {
                break;
            }
        }

        byte[] ret;
        if (totalCapacity > 0) {
            ret = arraycopy(byteArray, totalCapacity);
            return parseByte(ret);
        }

        return null;
    }

    public static byte[] arraycopy(LinkedList<byte[]> byteArray, int totalCapacity) {

        assert byteArray != null && totalCapacity > 0;

        byte[] dst = new byte[totalCapacity];

        int index = 0;

        int arraySize = byteArray.size();

        byte[] tmp;

        for (int i = 0; i < arraySize; i++) {
            // 先进先出
            tmp = byteArray.poll();
            if (tmp == null) {
                continue;
            }
            // 复制字节数组
            System.arraycopy(tmp, 0, dst, index, tmp.length);
            index += tmp.length;
        }
        return dst;
    }


    public static String parseByte(byte[] bytes) throws IOException {

        if (!Assert.isNotNull(bytes)) {
            return null;
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);

        BufferedReader bufferedReader = new BufferedReader(isr);

        String line;

        int flag = 0;

        try {
            while (true) {
                if (Assert.isEmpty(line = bufferedReader.readLine())
                        && flag != 1) {
                    flag = 1;
                    continue;
                }
                if (flag == 1) {
                    return line == null ? null : line.trim();
                }
            }
        } finally {
            bufferedReader.close();
            isr.close();
            bis.close();
        }
    }
}
