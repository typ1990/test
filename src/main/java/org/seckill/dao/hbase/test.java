package org.seckill.dao.hbase;

import org.seckill.util.PropertiesUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by typ on 2017/9/18.
 */
public class test {
    public static void main(String[] args) {
        try {
            Properties bdConfProperties = PropertiesUtils.getProperties("jdbc.properties");
            String ip = bdConfProperties.getProperty("hadoop.namenode.ip");
            System.out.println(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
