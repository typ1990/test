/*
 * Copyright (C) 2016 Shouqi, Inc. All Rights Reserved.
 */

package org.seckill.util;



import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * properties文件操作工具类
 *
 */
public class PropertiesUtils {
    /** Properties缓存 */
    private static Map<String, Properties> filePropMapping = new HashMap<String, Properties>();

    /**
     * 据文件名获取Properties，若不缓存不存在则解析文件
     * 
     * @param fileName property文件名
     * @return 属性文件对应的Properties对象
     * @throws IOException
     */
    public static Properties getProperties(String fileName) throws IOException {
        if (filePropMapping.containsKey(fileName)) {
            return filePropMapping.get(fileName);
        } else {
            Properties tmpProp = new Properties();
            tmpProp.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName));

            filePropMapping.put(fileName, tmpProp);

            return tmpProp;
        }
    }

    /**
     * 获取指定文件中的Property值
     * 
     * @param fileName 属性文件名
     * @param propertyName 属性名
     * @return 属性文件中对应的Property对象
     * @throws IOException
     */
    public static String getProperty(String fileName, String propertyName) throws IOException {
        Properties properties = getProperties(fileName);
        if (null == properties) {
            return null;
        }

        return properties.getProperty(propertyName);
    }

    /**
     * 获取int类型属性值: 若无此属性字段,则返回默认值;若转换错误则直接抛异常
     * @param properties 属性集合
     * @param filedName 字段名
     * @param dftValue 默认值
     * @return int属性值: 若无此属性返回默认值
     */
    public static int getIntProperty(Properties properties, String filedName, int dftValue) {
        String strValue = properties.getProperty(filedName);
        if (StringUtils.isBlank(strValue)) {
            return dftValue;
        }

        return Integer.valueOf(strValue);
    }
}