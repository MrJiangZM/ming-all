package com.ming.blog.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BigDecimalUtils {

    public static final DecimalFormat DF_FOR_2EFFECT_DIGIT = new DecimalFormat("###.##");

    /**
     * <li>123->123</li>
     * <li>123.132->123.13</li>
     * <li>123.0->123</li>
     * <li>123.00->123</li>
     * <li>123.10->123.1</li>
     *
     * @param value bigDecimal 保留两位有效位小数
     *
     * @return valueStr
     */
    public static String get2EffectDigitStr(BigDecimal value) {
        if (Objects.isNull(value)) {
            value = BigDecimal.ZERO;
        }
        return DF_FOR_2EFFECT_DIGIT.format(value);
    }

}