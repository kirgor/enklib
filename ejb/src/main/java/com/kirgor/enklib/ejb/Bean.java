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
 * Base class for beans, which is designed for simple use of RESTful API (via JAX-RS),
 * stored procedures proxy injection, around-invoke interceptor, exception handlers,
 * password based authentication and more.
 * <p/>
 * Specific applications are supposed to inherit their base bean class from this class and
 * in most cases implementation will be short.
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
     * <p/>
     * It's important to call base method in override-method, because base method is responsible for
     * cookie authentication, SQL session creation and injection of stored procedure proxies.
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

        // Extract security token and current user principal
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
     * <p/>
     * It's important to call base method in override-method, since it closes SQL session.
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
     * <p/>
     * Default implementation just rethrows the exception.
     *
     * @param ex     Caught exception instance.
     * @param method Invoked method.
     * @param params Invoked method params.
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
     * Creates data proxy of specified class for current invocation SQL session.
     *
     * @param interfaceClass Class of proxy interface.
     * @param <T>            Type of the proxy interface.
     * @throws Exception
     */
    protected <T> T createProxy(Class<T> interfaceClass) throws Exception {
        return createProxy(interfaceClass, getSession());
    }


    // Base API related methods.


    /**
     * Maps response entity to other object. If entity is null, it's also passed here.
     * <p/>
     * Default implementation of this method simply maps response entity to itself.
     *
     * @param entity Response entity (may be null). Please note, that if whole response entity is a list,
     *               this parameter will be actually list item rather than the entire list.
     * @return Response entity, which will be actually returned instead of entity param.
     */
    protected Object mapResponseEntity(Object entity) {
        return entity;
    }

    /**
     * This method is recommended to be called instead of {@link Response.ResponseBuilder} build(),
     * because override-method can apply several transformations to the {@link Response.ResponseBuilder}
     * like adding custom headers, cookies, etc. for all API responses.
     *
     * @param responseBuilder {@link Response.ResponseBuilder}, which should be converted to {@link Response}.
     * @return {@link Response} built from responseBuilder param.
     */
    protected Response buildResponse(Response.ResponseBuilder responseBuilder) {
        return responseBuilder.build();
    }

    /**
     * This is shorthand method for simple response 200 OK.
     */
    protected Response ok() {
        return buildResponse(Response.ok());
    }

    /**
     * This is shorthand method for simple response 200 OK with entity.
     */
    protected Response ok(Object entity) {
        entity = mapResponseEntity(entity);
        return buildResponse(Response.ok(entity));
    }

    /**
     * This is shorthand method for simple response 200 OK with list of entities.
     */
    protected Response ok(List<Object> entityList) {
        for (int i = 0; i < entityList.size(); i++) {
            entityList.set(i, mapResponseEntity(entityList.get(i)));
        }
        return buildResponse(Response.ok(entityList));
    }

    /**
     * Called by interceptor in case of API invocation exception.
     * <p/>
     * Default implementation simply returns {@link Response} with HTTP code taken from ex param.
     *
     * @param ex     Caught exception instance.
     * @param method Invoked method.
     * @param params Invoked method params.
     * @return Method must return {@link Response} on behalf of invoked API method.
     * @throws Exception
     */
    protected Response handleAPIException(APIException ex, Method method, Object[] params) throws Exception {
        return buildResponse(Response.status(ex.getHttpStatus()));
    }


    // Authentication logic.


    /**
     * Handles situations when user is not found during login process.
     * <p/>
     * Default implementation simply returns 404 HTTP error response.
     *
     * @param principal Principal, which no user could be found for.
     * @return {@link Response} which should be returned to API user.
     */
    protected Response handleUserNotFound(Object principal) {
        return buildResponse(Response.status(404));
    }

    /**
     * Handles situations during login process when user is found, but the password is wrong.
     * <p/>
     * Default implementation simply returns 401 HTTP error response.
     *
     * @param principal Principal of user, which attempted to login.
     * @return {@link Response} which should be returned to API user.
     */
    protected Response handleWrongPassword(Object principal) {
        return buildResponse(Response.status(401));
    }

    /**
     * Handles situations during registration process when user with specified principal already exists.
     * <p/>
     * Default implementation simply returns 403 HTTP error response.
     *
     * @param principal Principal of user, which tried to register.
     * @return {@link Response} which should be returned to API user.
     */
    protected Response handleUserAlreadyExists(Object principal) {
        return buildResponse(Response.status(403));
    }

    /**
     * Base method for the login process. Should be called in JAX-RS method, which is API endpoint for login.
     * <p/>
     * Handles situations with not found user or wrong password, generates security tokens, stores them in
     * configured {@link SecurityTokenStorage} and passes them back to user via response cookies.
     *
     * @param principal Principal of user, which tries to login.
     * @param password  Specified password.
     * @return {@link Response} which should be passed to API user. Returns simple 200 OK response with security token
     *         cookie if login succeeded, otherwise calls handleUserNotFound() or handleWrongPassword() method.
     * @throws Exception
     */
    protected Response login(Object principal, String password) throws Exception {
        Config config = configBean.getConfig();

        AuthenticationDAO.User user = config.getAuthenticationDAO().getUserByPrincipal(session, config.getStoredProcedureProxyFactory(), principal);
        if (user != null) {
            String expectedHash = user.getPasswordHash();
            String actualHash = calculateHash(config, password, user.getPasswordSalt());
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

    /**
     * Base method for the logout process. Should be called in JAX-RS method, which is API endpoint for logout.
     *
     * @return {@link Response} which should be passed to API user. It's always simple 200 OK.
     * @throws Exception
     */
    protected Response logout() throws Exception {
        configBean.getConfig().getSecurityTokenStorage().remove(getCurrentSecurityToken());
        return ok();
    }

    /**
     * Base method for the register process. Should be called in JAX-RS method, which is API endpoint for register.
     * <p/>
     * Handles situations when user already exists, generates password salt, hash, writes new user
     * into the storage using configured {@link AuthenticationDAO} implementation.
     *
     * @param principal Principal of user, which tries to register.
     * @param password  Specified password.
     * @param extraData Any object, which contains extra data for new registrant (e.g. name, phone number).
     *                  This object will be consumed by {@link AuthenticationDAO} implementation.
     * @return {@link Response} which should be passed to API user. Returns simple 200 OK response if
     *         registration succeeded, otherwise calls handleUserAlreadyExists() method.
     * @throws Exception
     */
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