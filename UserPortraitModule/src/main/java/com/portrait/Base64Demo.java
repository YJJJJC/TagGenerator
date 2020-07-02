package com.portrait;

import java.util.Base64;

/**
 * @author:yjc
 * @Date: 2019/9/3 13:09
 * @Description:
 *
 */
public class Base64Demo {

    public static void main(String[] args) {
        byte[] bytes = Base64.getDecoder().decode("NXxY3tn5XsuFcyzEw8qP8g==".getBytes());
        System.out.println(new String(bytes));
    }
}
