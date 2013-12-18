package com.kirgor.enklib.ejb.exception;

/**
 * Exception, which raises when injection of stored procedure proxy field (annotated by
 * {@link com.kirgor.enklib.ejb.annotation.InjectStoredProcedureProxy}) has been failed.
 */
public class InjectStoredProcedureProxyException extends Exception {
    public InjectStoredProcedureProxyException(Throwable cause, Class proxyClass) {
        super("Failed to inject the proxy of class " + proxyClass.getName(), cause);
    }
}
