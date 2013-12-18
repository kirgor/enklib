package com.kirgor.enklib.ejb;

import com.kirgor.enklib.sql.dialect.Dialect;
import com.kirgor.enklib.sql.proxy.StoredProcedureProxyFactory;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Root(name = "config")
@SuppressWarnings("UnusedDeclaration")
class Config {
    @Element
    private String dataSourceJNDI;
    @Element
    private String dialectClassName;
    @Element
    private String authenticationDAOClassName;
    @Element(required = false)
    private String passwordHashAlgorithm = "SHA-256";
    @Element(required = false)
    private int passwordHashIterations = 1024;
    @Element(required = false)
    private int passwordSaltLength = 32;
    @Element(required = false)
    private String securityTokenStorageClassName = "com.kirgor.enklib.ejb.HashMapSecurityTokenStorage";
    @Element(required = false)
    private int securityTokenLength = 32;
    @Element(required = false)
    private String securityCookieName = "securityToken";
    @Element(required = false)
    private String securityCookiePath = "/";
    @Element(required = false)
    private String securityCookieDomain = null;
    @Element(required = false)
    private int securityCookieVersion = 1;
    @Element(required = false)
    private String securityCookieComment = null;
    @Element(required = false)
    private int securityCookieMaxAge = 3600;
    @Element(required = false)
    private boolean securityCookieSecure = false;

    private Class dialectClass;
    private Class securityTokenStorageClass;
    private Class authenticationDAOClass;
    private StoredProcedureProxyFactory storedProcedureProxyFactory;
    private MessageDigest messageDigest;

    public String getDataSourceJNDI() {
        return dataSourceJNDI;
    }

    public String getDialectClassName() {
        return dialectClassName;
    }

    public String getPasswordHashAlgorithm() {
        return passwordHashAlgorithm;
    }

    public int getPasswordHashIterations() {
        return passwordHashIterations;
    }

    public int getPasswordSaltLength() {
        return passwordSaltLength;
    }

    public String getSecurityTokenStorageClassName() {
        return securityTokenStorageClassName;
    }

    public int getSecurityTokenLength() {
        return securityTokenLength;
    }

    public String getSecurityCookieName() {
        return securityCookieName;
    }

    public String getSecurityCookiePath() {
        return securityCookiePath;
    }

    public String getSecurityCookieDomain() {
        return securityCookieDomain;
    }

    public int getSecurityCookieVersion() {
        return securityCookieVersion;
    }

    public String getSecurityCookieComment() {
        return securityCookieComment;
    }

    public int getSecurityCookieMaxAge() {
        return securityCookieMaxAge;
    }

    public boolean isSecurityCookieSecure() {
        return securityCookieSecure;
    }

    public Dialect getDialect() throws Exception {
        if (dialectClass == null) {
            dialectClass = Class.forName(dialectClassName);
        }
        return (Dialect) dialectClass.newInstance();
    }

    public SecurityTokenStorage getSecurityTokenStorage() throws Exception {
        if (securityTokenStorageClass == null) {
            securityTokenStorageClass = Class.forName(securityTokenStorageClassName);
        }
        return (SecurityTokenStorage) securityTokenStorageClass.newInstance();
    }

    public AuthenticationDAO getAuthenticationDAO() throws Exception {
        if (authenticationDAOClass == null) {
            authenticationDAOClass = Class.forName(authenticationDAOClassName);
        }
        return (AuthenticationDAO) authenticationDAOClass.newInstance();
    }

    public StoredProcedureProxyFactory getStoredProcedureProxyFactory() throws Exception {
        if (storedProcedureProxyFactory == null) {
            storedProcedureProxyFactory = new StoredProcedureProxyFactory(getDialect());
        }
        return storedProcedureProxyFactory;
    }

    public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance(passwordHashAlgorithm);
        }
        return messageDigest;
    }
}
