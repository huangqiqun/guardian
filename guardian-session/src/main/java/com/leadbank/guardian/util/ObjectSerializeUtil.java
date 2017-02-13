package com.leadbank.guardian.util;

import java.io.*;

public class ObjectSerializeUtil {

    public static Object deserialize(String str) throws IOException, ClassNotFoundException {
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object result = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();

        return result;
    }

    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String str = byteArrayOutputStream.toString("ISO-8859-1");
        str = java.net.URLEncoder.encode(str, "UTF-8");

        objectOutputStream.close();
        byteArrayOutputStream.close();
        return str;
    }

}
