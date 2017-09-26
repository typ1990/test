package org.seckill.hbase;

import java.security.MessageDigest;

public class Md5Util {
	private static MessageDigest md5 = null;
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 用于获取一个String的md5值
     * @param string
     * @return
     */
    public static String getMd5_32(String str) {
        byte[] bs = md5.digest(str.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for(byte x:bs) {
            if((x & 0xff)>>4 == 0) {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return sb.toString();
    }
    
    public static String getMd5_16(String str){
    	return	getMd5_32(str).substring(8,24);
    }

    public static void main(String[] args) {
        System.out.println(getMd5_16("hello world"));
        System.out.println(getMd5_16("hehe"));
        System.out.println(getMd5_16("zhoulingjiang"));
    }
}
