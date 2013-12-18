package com.kirgor.enklib.ejb.exception;

public class ProxyInjectException extends Exception {
    public ProxyInjectException(Throwable cause, Class proxyClass) {
        super("Failed to inject the proxy of class " + proxyClass.getName(), cause);
    }
}
