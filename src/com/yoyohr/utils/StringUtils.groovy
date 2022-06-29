package com.yoyohr.utils

/**
 * <p>StringUtils</p>
 *
 * @author nekoimi 2022/06/29
 */
class StringUtils {

    public static boolean isEmpty(Object strObj) {
        if (strObj == null) {
            return true
        }
        return strObj.toString().trim().length() <= 0
    }

    public static boolean isNotEmpty(Object strObj) {
        return !isEmpty(strObj);
    }
}
