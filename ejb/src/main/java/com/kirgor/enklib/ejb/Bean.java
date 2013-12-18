package com.kirgor.enklib.ejb;

import com.kirgor.enklib.common.EncodingUtils;
import com.kirgor.enklib.ejb.annotation.InjectStoredProcedureProxy;
import com.kirgor.enklib.ejb.annotation.SkipSecurityCheck;
import com.kirgor.enklib.ejb.exception.APIException;
import com.kirgor.enklib.ejb.exception.InjectStoredProcedureProxyException;
import com.kirgor.enklib.sql.Session;
import org.apache.commons.codec.DecoderException;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * Base class for beans, which is designed for simple use of stored procedures proxy injection,
 * around-invoke interceptor and exception handlers.
 * <p/>
 * Specific applications are supposed to inherit their base bean class from this class and
 * in most cases implementation will be short.
 * <p/>
 * Be accurate, since this class contains static fields, you shouldn't inherit from this class directly
 * more than once, otherwise unexpected behaviour with data proxies may occur.
 */
@Interceptors(Bean.Interceptor.class)
public class Bean {
    @EJB
    private ConfigBean configBean;
    private Session session;
    @Context
    private HttpServletRequest httpServletRequest;
    private String currentSecurityToken;
    private Object currentUserPrincipal;

    /**
     * Gets {@link Session}, which is available for current invocation. It is the same session
     * as one which is shared among bean stored procedure proxies.
     */
    protected Session getSession() {
        return session;
    }

    /**
     * Gets instance of {@link HttpServletRequest} which is associated with current method invocation
     * in case if current method represents RESTful API request.
     */
    protected HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Gets security token, associated with current API request.
     * Returns null if there is none.
     */
    protected String getCurrentSecurityToken() {
        return currentSecurityToken;
    }

    /**
     * Gets principal of current logged in user (e.g. ID or email).
     * Returns null if there is none.
     */
    protected Object getCurrentUserPrincipal() {
        return currentUserPrincipal;
    }


    // Base method invocation wrappers.


    /**
     * Called by interceptor before bean method has been invoked.
     *
     * @param method Method, which is about to be invoked.
     * @param params Method params.
     * @throws Exception
     */
    protected void beforeInvoke(Method method, Object[] params) throws Exception {
        Config config = configBean.getConfig();

        // Create session for proxies
        session = createSession();
        session.setAutoCommit(true);

        // Instantiate proxies and inject them
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getAnnotation(InjectStoredProcedureProxy.class) != null) {
                try {
                    Object proxy = createProxy(field.getType(), session);
                    field.setAccessible(true);
                    field.set(this, proxy);
                } catch (Exception ex) {
                    throw new InjectStoredProcedureProxyException(ex, field.getType());
                }
            }
        }

        currentSecurityToken = null;
        currentUserPrincipal = null;

        if (httpServletRequest != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals(configBean.getConfig().getSecurityCookieName())) {
                    currentSecurityToken = cookie.getValue();
                    currentUserPrincipal = config.getSecurityTokenStorage().get(currentSecurityToken);
                }
            }

            if (currentUserPrincipal == null && method.getAnnotation(SkipSecurityCheck.class) == null) {
                throw new APIException(401);
            }
        }
    }


    /**
     * Called by interceptor after bean method has been invoked.
     *
     * @param method Method, which has just been invoked.
     * @param params Method params.
     * @throws Exception
     */
    protected void afterInvoke(Method method, Object[] params) throws Exception {
        // Close the session
        if (session != null) {
            session.close();
        }
    }

    /**
     * Called by interceptor in case of invocation exception.
     * If inherited bean class doesn't override this method, it just rethrows the exception.
     *
     * @param ex Catched exception instance.
     * @return Method can return something on behalf of invoked method.
     * @throws Exception
     */
    protected Object handleException(Exception ex, Method method, Object[] params) throws Exception {
        throw ex;
    }


    // Database related.


    /**
     * Creates {@link Session} instance, connected to the database.
     *
     * @throws NamingException
     * @throws SQLException
     */
    protected Session createSession() throws Exception {
        Config config = configBean.getConfig();
        DataSource dataSource = InitialContext.doLookup(config.getDataSourceJNDI());
        return new Session(dataSource, config.getDialect());
    }

    /**
     * Creates data proxy of specified class for specified session.
     *
     * @param interfaceClass Class of the proxy interface.
     * @param session        Session instance for the proxy.
     * @param <T>            Type of the proxy interface.
     * @throws Exception
     */
    protected <T> T createProxy(Class<T> interfaceClass, Session session) throws Exception {
        return configBean.getConfig().getStoredProcedureProxyFactory().getProxy(interfaceClass, session);
    }

    /**
     * Creates data proxy of specified class for current invocation session.
     *
     * @param interfaceClass Class of proxy interface.
     * @param <T>            Type of the proxy interface.
     * @throws Exception
     */
    protected <T> T createProxy(Class<T> interfaceClass) throws Exception {
        return createProxy(interfaceClass, getSession());
    }


    // Base API related methods.


    protected void beforeReturnEntity(Object entity) {
    }

    protected Response buildResponse(Response.ResponseBuilder responseBuilder) {
        return responseBuilder.build();
    }

    protected Response ok() {
        return buildResponse(Response.ok());
    }

    protected Response ok(Object entity) {
        if (entity != null) {
            beforeReturnEntity(entity);
        }
        return buildResponse(Response.ok(entity));
    }

    protected Response ok(List<?> entityList) {
        for (Object entity : entityList) {
            if (entity != null) {
                beforeReturnEntity(entity);
            }
        }
        return buildResponse(Response.ok(entityList));
    }

    protected Response handleAPIException(APIException ex, Method method, Object[] params) throws Exception {
        return buildResponse(Response.status(ex.getHttpStatus()));
    }


    // Authentication logic.


    protected Response handleUserNotFound(Object principal) {
        return Response.status(404).build();
    }

    protected Response handleWrongPassword(Object principal) {
        return Response.status(401).build();
    }

    protected Response handleUserAlreadyExists(Object principal) {
        return Response.status(403).build();
    }

    protected Response login(Object principal, String password) throws Exception {
        Config config = configBean.getConfig();

        Object user = config.getAuthenticationDAO().getUserByPrincipal(session, config.getStoredProcedureProxyFactory(), principal);
        if (user != null) {
            String expectedHash = config.getAuthenticationDAO().getUserPasswordHash(user);
            String actualHash = calculateHash(config, password, config.getAuthenticationDAO().getUserPasswordSalt(user));
            if (expectedHash.equals(actualHash)) {
                String securityToken = generateRandomCode(config.getSecurityTokenLength());
                config.getSecurityTokenStorage().add(securityToken, principal);
                NewCookie newCookie = new NewCookie(
                        config.getSecurityCookieName(), securityToken,
                        config.getSecurityCookiePath(),
                        config.getSecurityCookieDomain(),
                        config.getSecurityCookieVersion(),
                        config.getSecurityCookieComment(),
                        config.getSecurityCookieMaxAge(),
                        config.isSecurityCookieSecure());

                return buildResponse(Response.ok().cookie(newCookie));
            } else {
                return handleWrongPassword(principal);
            }
        } else {
            return handleUserNotFound(principal);
        }
    }

    protected Response logout() throws Exception {
        configBean.getConfig().getSecurityTokenStorage().remove(getCurrentSecurityToken());
        return ok();
    }

    protected Response register(Object principal, String password, Object extraData) throws Exception {
        Config config = configBean.getConfig();

        Object user = config.getAuthenticationDAO().getUserByPrincipal(session, config.getStoredProcedureProxyFactory(), principal);
        if (user == null) {
            String salt = generateRandomCode(config.getPasswordSaltLength());
            String hash = calculateHash(config, password, salt);
            config.getAuthenticationDAO().addNewUser(session, config.getStoredProcedureProxyFactory(), principal, hash, salt, extraData);
            return ok();
        } else {
            return handleUserAlreadyExists(principal);
        }
    }

    private static String generateRandomCode(int length) {
        byte[] bytes = new byte[length];
        Random random = new Random();
        random.nextBytes(bytes);
        return EncodingUtils.bytesToHex(bytes);
    }

    private static String calculateHash(Config config, String password, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException, DecoderException {
        byte[] bytes = password.getBytes("UTF-8");
        MessageDigest messageDigest = config.getMessageDigest();
        messageDigest.update(EncodingUtils.hexToBytes(salt));
        for (int i = 0; i < config.getPasswordHashIterations(); i++) {
            bytes = messageDigest.digest(bytes);
            messageDigest.reset();
        }
        return EncodingUtils.bytesToHex(bytes);
    }

    public static class Interceptor {
        @AroundInvoke
        protected Object intercept(InvocationContext ic) throws Exception {
            Bean bean = (Bean) ic.getTarget();
            try {
                bean.beforeInvoke(ic.getMethod(), ic.getParameters());
                return ic.proceed();
            } catch (APIException ex) {
                if (ic.getMethod().getReturnType().isAssignableFrom(Response.class)) {
                    return bean.handleAPIException(ex, ic.getMethod(), ic.getParameters());
                } else {
                    return bean.handleException(ex, ic.getMethod(), ic.getParameters());
                }
            } catch (Exception ex) {
                return bean.handleException(ex, ic.getMethod(), ic.getParameters());
            } finally {
                bean.afterInvoke(ic.getMethod(), ic.getParameters());
            }
        }
    }
}