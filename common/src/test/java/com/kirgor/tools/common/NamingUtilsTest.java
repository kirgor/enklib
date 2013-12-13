package com.kirgor.tools.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NamingUtilsTest {
    @Test
    public void lowerCamelToUpperCamel() {
        assertEquals("HelloWorld", NamingUtils.lowerCamelToUpperCamel("helloWorld"));
    }

    @Test
    public void upperCamelToLowerCamel() {
        assertEquals("helloWorld", NamingUtils.upperCamelToLowerCamel("HelloWorld"));
    }

    @Test
    public void camelToSnake() {
        assertEquals("hello_world", NamingUtils.camelToSnake("helloWorld", false));
        assertEquals("HELLO_WORLD", NamingUtils.camelToSnake("helloWorld", true));
    }

    @Test
    public void lowerSnakeToUpperSnake() {
        assertEquals("HELLO_WORLD", NamingUtils.lowerSnakeToUpperSnake("hello_world"));
    }

    @Test
    public void upperSnakeToLowerSnake() {
        assertEquals("hello_world", NamingUtils.upperSnakeToLowerSnake("HELLO_WORLD"));
    }

    @Test
    public void snakeToCamel() {
        assertEquals("helloWorld", NamingUtils.snakeToCamel("hello_world", false));
        assertEquals("HelloWorld", NamingUtils.snakeToCamel("hello_world", true));
    }
}
