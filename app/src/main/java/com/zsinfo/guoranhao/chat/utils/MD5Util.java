package com.zsinfo.guoranhao.chat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lixl on 2018/7/17.
 *
 首先将目的字符串加密一次，获得32位字符串
 然后将32位字符串拆为2段，分别加密1次
 最后将加密后的2段拼接，加密100次
 *
 */
public class MD5Util {

    /**
     * 加盐
     * @param content
     * @return
     */
    public static String getMD5(String content) {
        String s = makeMD5(content);
        String s1 = null;
        if (s != null) {
            s1 = s.substring(0, 16);
        }
        String s2 = null;
        if (s != null) {
            s2 = s.substring(16, 32);
        }
        s1 = makeMD5(s1);
        s2 = makeMD5(s2);
        s = s1 + s2;
        for (int i = 0; i < 100; i++) {
            s = makeMD5(s);
        }
        return s;
    }

    /**
     * 32位md5加密
     * @param content
     * @return
     */
    public static String makeMD5(String content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(content.getBytes());
            return getHashString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getHashString(MessageDigest messageDigest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : messageDigest.digest()) {
            builder.append(Integer.toHexString(  ( b >> 4 ) & 0xf )  );
            builder.append(Integer.toHexString(  b & 0xf )           );
        }
        return builder.toString();
    }

}
