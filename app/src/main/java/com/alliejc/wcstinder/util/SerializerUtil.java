package com.alliejc.wcstinder.util;

import com.alliejc.wcstinder.trackmyswing.Dancer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gkatta on 2/28/17.
 */

public class SerializerUtil {
    /**
     * This method serializes the object passed in
     *
     * @param obj
     * @return
     */
    public static byte[] serialize(List<Dancer> obj) {
        byte[] arrByte;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            arrByte = byteOut.toByteArray();
        } catch (Exception e) {
            arrByte = null;
        }
        return arrByte;
    }

    /**
     * Deserializes the byte array
     *
     * @param arrByte
     * @return
     */
    public static List<Dancer> deserialize(byte[] arrByte) {
        List<Dancer> obj;
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(arrByte);
            ObjectInputStream in = new ObjectInputStream(byteIn);
            obj = (List<Dancer>) in.readObject();
        } catch (Exception e) {
            obj = null;
            e.printStackTrace();
        }
        return obj;
    }


    public static Object deserializeString(byte[] arrByte) {
        Object obj;
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(arrByte);
            ObjectInputStream in = new ObjectInputStream(byteIn);
            obj = in.readObject();
        } catch (Exception e) {
            obj = null;
        }
        return obj;
    }

    public static byte[] serialize(Object obj) {
        byte[] arrByte;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            arrByte = byteOut.toByteArray();
        } catch (Exception e) {
            arrByte = null;
        }
        return arrByte;
    }
}

