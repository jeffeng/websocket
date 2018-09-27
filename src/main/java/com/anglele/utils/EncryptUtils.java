package com.anglele.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具
 * Created by jeffeng on 2017/8/12.
 */
public class EncryptUtils {
    public static String encryptToMD5(String info) {
        byte[] digesta = null;

        try {
            MessageDigest rs = MessageDigest.getInstance("MD5");
            rs.update(info.getBytes());
            digesta = rs.digest();
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

        String rs1 = byte2hex(digesta);
        return rs1;
    }

    public static String encryptToSHA(String info) {
        byte[] digesta = null;

        try {
            MessageDigest rs = MessageDigest.getInstance("SHA-1");
            rs.update(info.getBytes());
            digesta = rs.digest();
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

        String rs1 = byte2hex(digesta);
        return rs1;
    }

    public static String getKey(String algorithm, String src) {
        return algorithm.equals("AES") ? src.substring(0, 16) : (algorithm.equals("DES") ? src.substring(0, 8) : null);
    }

    public static String getAESKey(String src) {
        return getKey("AES", src);
    }

    public static String getDESKey(String src) {
        return getKey("DES", src);
    }

    public static SecretKey createSecretKey(String algorithm) {
        SecretKey deskey = null;

        try {
            KeyGenerator keygen = KeyGenerator.getInstance(algorithm);
            deskey = keygen.generateKey();
        } catch (NoSuchAlgorithmException var4) {
            var4.printStackTrace();
        }

        return deskey;
    }

    public static SecretKey createSecretAESKey() {
        return createSecretKey("AES");
    }

    public static SecretKey createSecretDESKey() {
        return createSecretKey("DES");
    }

    public static String encrypt(String Algorithm, SecretKey key, String info) {
        byte[] cipherByte = null;

        try {
            Cipher e = Cipher.getInstance(Algorithm);
            e.init(1, key);
            cipherByte = e.doFinal(info.getBytes());
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return byte2hex(cipherByte);
    }

    public static String decrypt(String Algorithm, SecretKey key, String sInfo) {
        byte[] cipherByte = null;

        try {
            Cipher e = Cipher.getInstance(Algorithm);
            e.init(2, key);
            cipherByte = e.doFinal(hex2byte(sInfo));
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return new String(cipherByte);
    }

    public static String decrypt(String Algorithm, String sSrc, String sKey) throws Exception {
        try {
            if (sKey == null) {
                throw new Exception("Key为空null");
            } else if (Algorithm.equals("AES") && sKey.length() != 16) {
                throw new Exception("Key长度不是16位");
            } else if (Algorithm.equals("DES") && sKey.length() != 8) {
                throw new Exception("Key长度不是8位");
            } else {
                byte[] ex = sKey.getBytes("ASCII");
                SecretKeySpec skeySpec = new SecretKeySpec(ex, Algorithm);
                Cipher cipher = Cipher.getInstance(Algorithm);
                cipher.init(2, skeySpec);
                byte[] encrypted1 = hex2byte(sSrc);

                try {
                    byte[] e = cipher.doFinal(encrypted1);
                    String originalString = new String(e);
                    return originalString;
                } catch (Exception var9) {
                    throw var9;
                }
            }
        } catch (Exception var10) {
            throw var10;
        }
    }

    public static String encrypt(String Algorithm, String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            throw new Exception("Key为空null");
        } else if (Algorithm.equals("AES") && sKey.length() != 16) {
            throw new Exception("Key长度不是16位");
        } else if (Algorithm.equals("DES") && sKey.length() != 8) {
            throw new Exception("Key长度不是8位");
        } else {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, Algorithm);
            Cipher cipher = Cipher.getInstance(Algorithm);
            cipher.init(1, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return byte2hex(encrypted);
        }
    }

    public static String encryptToDES(SecretKey key, String info) {
        return encrypt("DES", key, info);
    }

    public static String encryptToDES(String key, String info) throws Exception {
        return encrypt("DES", info, key);
    }

    public static String decryptByDES(SecretKey key, String sInfo) {
        return decrypt("DES", key, sInfo);
    }

    public static String decryptByDES(String key, String sInfo) throws Exception {
        return decrypt("DES", sInfo, key);
    }

    public static String encryptToAES(SecretKey key, String info) {
        return encrypt("AES", key, info);
    }

    public static String encryptToAES(String key, String info) throws Exception {
        return encrypt("AES", info, key);
    }

    public static String decryptByAES(SecretKey key, String sInfo) {
        return decrypt("AES", key, sInfo);
    }

    public static String decryptByAES(String key, String sInfo) throws Exception {
        return decrypt("AES", sInfo, key);
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        } else {
            int l = strhex.length();
            if (l % 2 == 1) {
                return null;
            } else {
                byte[] b = new byte[l / 2];

                for (int i = 0; i != l / 2; ++i) {
                    b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
                }

                return b;
            }
        }
    }

    public static String byte2hex(byte[] b) {
        String hs = "";

        for (int n = 0; n < b.length; ++n) {
            String temp = Integer.toHexString(b[n] & 255);
            if (temp.length() == 1) {
                hs = hs + "0" + temp;
            } else {
                hs = hs + temp;
            }
        }

        return hs.toUpperCase();
    }

    public static void main(String[] args) {
        new EncryptUtils();
        String source = "www.putiman.com";
        System.out.println("Hello经过MD5:" + encryptToMD5(source));
        System.out.println("Hello经过SHA:" + encryptToSHA(source));
        System.out.println("========随机生成Key进行加解密==============");
        SecretKey key = createSecretDESKey();
        String str1 = encryptToDES(key, source);
        System.out.println("DES加密后为:" + str1);
        String str2 = decryptByDES(key, str1);
        System.out.println("DES解密后为：" + str2);
        SecretKey key1 = createSecretAESKey();
        String stra = encryptToAES(key1, source);
        System.out.println("AES加密后为:" + stra);
        String strb = decryptByAES(key1, stra);
        System.out.println("AES解密后为：" + strb);
        System.out.println("========指定Key进行加解密==============");

        try {
            String e = getAESKey(encryptToSHA(source));
            String DESKey = getDESKey(encryptToSHA(source));
            System.out.println(e);
            System.out.println(DESKey);
            String str11 = encryptToDES(DESKey, source);
            System.out.println("DES加密后为:" + str11);
            String str12 = decryptByDES(DESKey, str11);
            System.out.println("DES解密后为：" + str12);
            String strc = encryptToAES(e, source);
            System.out.println("AES加密后为:" + strc);
            String strd = decryptByAES(e, strc);
            System.out.println("AES解密后为：" + strd);
        } catch (Exception var15) {
            var15.printStackTrace();
        }

    }
}
