package com.kirgor.tools.common;

/**
 * Contains methods related to different naming conventions.
 */
public class NamingUtils {
    /**
     * Converts lower camel case string like to upper camel case (Pascal),
     * for example 'helloWorld' -> 'HelloWorld'.
     */
    public static String lowerCamelToUpperCamel(String lowerCamel) {
        return lowerCamel.substring(0, 1).toUpperCase() + lowerCamel.substring(1);
    }

    /**
     * Converts upper camel case (Pascal) string to lower camel case,
     * for example 'HelloWorld' -> 'helloWorld'.
     */
    public static String upperCamelToLowerCamel(String upperCamel) {
        return upperCamel.substring(0, 1).toLowerCase() + upperCamel.substring(1);
    }

    /**
     * Converts camel case string (lower or upper/Pascal) to snake case,
     * for example 'helloWorld' or 'HelloWorld' -> 'hello_world'.
     *
     * @param camel Input string.
     * @param upper True if result snake cased string should be upper cased like 'HELLO_WORLD'.
     */
    public static String camelToSnake(String camel, boolean upper) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : camel.toCharArray()) {
            char nc = upper ? Character.toUpperCase(c) : Character.toLowerCase(c);
            if (Character.isUpperCase(c)) {
                stringBuilder.append('_').append(nc);
            } else {
                stringBuilder.append(nc);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Converts camel case string (lower or upper/Pascal) to lower snake case,
     * for example 'helloWorld' or 'HelloWorld' -> 'hello_world' or 'HELLO_WORLD'.
     */
    public static String camelToLowerSnake(String camel) {
        return camelToSnake(camel, false);
    }

    /**
     * Converts camel case string (lower or upper/Pascal) to upper snake case,
     * for example 'helloWorld' or 'HelloWorld' -> 'HELLO_WORLD'.
     */
    public static String camelToUpperSnake(String camel) {
        return camelToSnake(camel, true);
    }

    /**
     * Converts lower snake case string like to upper snake case,
     * for example 'hello_world' -> 'HELLO_WORLD'.
     */
    public static String lowerSnakeToUpperSnake(String lowerSnake) {
        return lowerSnake.toUpperCase();
    }

    /**
     * Converts upper snake case string like to lower snake case,
     * for example 'HELLO_WORLD' -> 'hello_world'.
     */
    public static String upperSnakeToLowerSnake(String upperSnake) {
        return upperSnake.toLowerCase();
    }

    /**
     * Converts snake case string (lower or upper) to camel case,
     * for example 'hello_world' or 'HELLO_WORLD' -> 'helloWorld' or 'HelloWorld'.
     *
     * @param snake Input string.
     * @param upper True if result snake cased string should be upper cased like 'HelloWorld'.
     */
    public static String snakeToCamel(String snake, boolean upper) {
        StringBuilder sb = new StringBuilder();
        boolean firstWord = true;
        for (String word : snake.split("_")) {
            if (!word.isEmpty()) {
                if (firstWord && !upper) {
                    sb.append(word.toLowerCase());
                } else {
                    sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
                }
                firstWord = false;
            }
        }
        return sb.toString();
    }

    /**
     * Converts snake case string (lower or upper) to lower camel case,
     * for example 'hello_world' or 'HELLO_WORLD' -> 'helloWorld'.
     */
    public static String snakeToLowerCamel(String snake) {
        return snakeToCamel(snake, false);
    }

    /**
     * Converts snake case string (lower or upper) to upper camel case,
     * for example 'hello_world' or 'HELLO_WORLD' -> 'HelloWorld'.
     */
    public static String snakeToUpperCamel(String snake) {
        return snakeToCamel(snake, true);
    }
}
