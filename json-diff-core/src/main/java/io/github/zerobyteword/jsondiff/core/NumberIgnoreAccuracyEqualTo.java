package io.github.zerobyteword.jsondiff.core;

import java.math.BigDecimal;

public class NumberIgnoreAccuracyEqualTo implements EqualTo<Number> {
    public static final EqualTo DEFAULT = new NumberIgnoreAccuracyEqualTo();

    @Override
    public boolean equalTo(Number o1, Number o2) {
        return new BigDecimal(o1.toString()).compareTo(new BigDecimal(o2.toString())) == 0;
    }
}
