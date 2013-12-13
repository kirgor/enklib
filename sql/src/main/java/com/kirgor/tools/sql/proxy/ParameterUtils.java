package com.kirgor.tools.sql.proxy;

import java.sql.Timestamp;
import java.util.List;

/**
 * Contains methods, which are used as parameter pre-processors.
 */
public abstract class ParameterUtils {
    /**
     * Converts string to lower case.
     */
    public static String lowerCase(String s) {
        return s == null ? null : s.toLowerCase();
    }

    /**
     * Converts string to upper case.
     */
    public static String upperCase(String s) {
        return s == null ? null : s.toUpperCase();
    }

    /**
     * Builds prefix search parameter for LIKE operator from non-formatted string.
     * Basically, this method insert escape character before special symbols and adds '%' at the end.
     */
    public static String likePrefix(String param) {
        return param != null ? param.toLowerCase().replace("_", "\\_").replace("%", "\\%") + "%" : null;
    }

    /**
     * Converts {@link Long} value to appropriate SQL timestamp parameter.
     */
    public static Timestamp timestamp(Long param) {
        return param != null ? new Timestamp(param) : null;
    }

    /**
     * Converts {@link List} of enum values to a bit mask.
     * Enum values from 0 to 31 inclusive are supported, since {@link Integer} has only 32 bits.
     */
    public static Integer bitMask(List<Integer> param) {
        if (param != null && param.size() > 0) {
            int result = 0;
            for (Integer i : param) {
                result |= 1 << i;
            }
            return result;
        } else {
            return null;
        }
    }
}
