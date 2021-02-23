package com.prestigeding.remoting;

import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) {
//        System.out.println("HELLO, WORLD".getBytes().length);

        Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
        String[] index = COMMA_SPLIT_PATTERN.split("0");

        System.out.println("a");
    }
}
