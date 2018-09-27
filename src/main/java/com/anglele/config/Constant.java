package com.anglele.config;

import com.anglele.utils.EncryptUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

/**
 * Created by jeffeng on 2018-9-27.
 */
public class Constant {
    /**
     * 存放wss证书的路径
     */
    public static String JKS_PATH = "";
    /**
     * 存放wss证书的密码
     */
    public static String JKS_PSD = "";
    /**
     * redis服务器地址
     */
    public static String REDIS_HOST = "";
    /**
     * redis服务器数据库
     */
    public static int REDIS_DB = 1;
    /**
     * redis连接超时
     */
    public static int REDIS_TIMEOUT = 10000;
    /**
     * redis密码
     */
    public static String REDIS_PASSWORD = "";

    public static int REDIS_MAX_ACTIVE = 1024;

    public static int REDIS_MAX_IDLE = 200;

    public static int REDIS_MAX_WAIT = 10000;

    /**
     * 是否是初始化
     */
    public static String DB_ENTRY = "0";

    public static String WEB_CODE = "www.anglele.com/web";

    public static int WEB_SOCKET_PORT = 8000;

    public static String WS_NAME = "ws";

    public static int WEB_SOCKET_EXECUTE_TIMES = 10;

    public static String WEB_SOCKET_EXECUTE_CRON = "0/5 * * * * ? ";

    public static String WEB_SOCKET_KEY="WEB_SCOKET";

    public static void loadProperties() throws Exception {
        //String dir = path + File.separator + webName;
        File file = new File(System.getProperty("user.dir") + File.separator, "websocket.properties");
        OrderedProperties properties = new OrderedProperties();
        Object o = Constant.class.newInstance();
        Field[] oFile;
        int oFile1;
        if (file.exists()) {
            BufferedInputStream var11 = new BufferedInputStream(new FileInputStream(file));
            properties.load(var11);
            oFile = Constant.class.getDeclaredFields();
            Field[] var15 = oFile;
            oFile1 = oFile.length;

            for (int var12 = 0; var12 < oFile1; ++var12) {
                Field var16 = var15[var12];
                if (!var16.isAccessible()) {
                    var16.setAccessible(true);
                }

                if (StringUtils.isNotEmpty(properties.getProperty(var16.getName()))) {
                    if (Integer.TYPE == var16.getType()) {
                        var16.setInt(o, Integer.parseInt(properties.getProperty(var16.getName())));
                    } else if (Boolean.TYPE == var16.getType()) {
                        var16.setBoolean(o, Boolean.parseBoolean(properties.getProperty(var16.getName())));
                    } else if (Long.TYPE == var16.getType()) {
                        var16.setLong(o, Long.parseLong(properties.getProperty(var16.getName())));
                    } else {
                        var16.set(o, properties.getProperty(var16.getName()));
                    }
                }
            }

            var11.close();
            if (StringUtils.isNotEmpty(WEB_CODE)) {
                String var13 = EncryptUtils.getAESKey(WEB_CODE);
                if ("1".equals(DB_ENTRY)) {
                    REDIS_PASSWORD = EncryptUtils.decryptByAES(var13, REDIS_PASSWORD);
                    JKS_PSD = EncryptUtils.decryptByAES(var13, JKS_PSD);
                } else {
                    properties.setProperty("DB_ENTRY", "1");
                    properties.setProperty("REDIS_PASSWORD", EncryptUtils.encryptToAES(var13, REDIS_PASSWORD));
                    properties.setProperty("JKS_PSD", EncryptUtils.encryptToAES(var13, JKS_PSD));
                    FileOutputStream var161 = new FileOutputStream(file, false);
                    properties.store(var161, "websocket.properties");
                    var161.close();
                }
            }
        } else {
            file.createNewFile();
            Field[] var111 = o.getClass().getDeclaredFields();
            oFile = var111;
            int var121 = var111.length;

            for (oFile1 = 0; oFile1 < var121; ++oFile1) {
                Field var151 = oFile[oFile1];
                if (!var151.isAccessible()) {
                    var151.setAccessible(true);
                }

                properties.put(var151.getName(), var151.get(var151.getName()) == null ? "" : var151.get(var151.getName()));
            }

            FileOutputStream var17 = new FileOutputStream(file, false);
            properties.store(var17, "websocket.properties");
            var17.close();
        }

    }

}
