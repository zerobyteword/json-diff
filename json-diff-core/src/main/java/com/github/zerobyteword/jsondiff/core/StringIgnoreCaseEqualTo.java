package com.github.zerobyteword.jsondiff.core;


public class StringIgnoreCaseEqualTo implements EqualTo<String> {

    public static final EqualTo DEFAULT = new StringIgnoreCaseEqualTo();


    @Override
    public boolean equalTo(String o1, String o2) {
        return o1.equalsIgnoreCase(o2);
    }
}
