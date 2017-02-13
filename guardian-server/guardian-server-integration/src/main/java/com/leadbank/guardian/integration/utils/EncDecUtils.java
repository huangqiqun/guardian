package com.leadbank.guardian.integration.utils;

/**
 * 
 *  利得金融
 * Copyright (c) 2013-2015 leadbank,Inc.All Rights Reserved.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 签名 加密类
 * 
 * @author JASON
 * @version $Id: EncDecUtils.java, v 0.1 2015年5月26日 下午3:18:40 JASON Exp $
 */
public class EncDecUtils {
    // 加密字符到MD5
    public static String toMD5(String str) {
        MessageDigest alg = null;
        try {
            alg = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        alg.update(str.getBytes());
        byte[] digesta = alg.digest();
        return byte2Hex(digesta);
    }

    static String byte2Hex(byte[] paramArrayOfByte) {
        StringBuffer localStringBuffer = new StringBuffer();
        String str = "";
        for (int i = 0; i < paramArrayOfByte.length; ++i) {
            str = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
            if (str.length() == 1)
                localStringBuffer.append("0");
            localStringBuffer.append(str);
        }
        return localStringBuffer.toString().toUpperCase();
    }

    /**
     * md5加密
     * @param str
     * @param encoding
     * @return
     */
    public static String getMD5(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val < 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toUpperCase();
    }


    public static void main(String[] args) {
//        System.out.println(transpasswordDesenc("123456", "19891990"));
//        System.out.println(transpasswordDesdec("N2NhMzZhNGQzOTJiZjY3MzJhNmMzNDljZWVjMTI3ZDQNvcItk+LCgQ=", "19891990"));
    }
}
