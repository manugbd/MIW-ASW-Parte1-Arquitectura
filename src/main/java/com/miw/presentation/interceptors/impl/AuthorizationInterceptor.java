package com.miw.presentation.interceptors.impl;

import com.miw.presentation.configuration.AccessConfigurationManager;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.interceptors.Interceptor;
import com.miw.presentation.interceptors.InvocationContext;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

public class AuthorizationInterceptor implements Interceptor, HttpSessionAware {


    // 2.3 Logger por setter y session por interfaz
    private Logger logger;
    private HttpSession session;

    @Override
    public void setHttpSession(HttpSession session) {
        this.session = session;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean before(InvocationContext context) {
        if (logger == null || session == null)
            throw new IllegalStateException("Faltan dependencias en AuthorizationInterceptor");

        Object value = session.getAttribute("username");
        String username = value instanceof String ? (String) value : null;
        AccessConfigurationManager configuration = AccessConfigurationManager.getInstance();

        if (!configuration.userExists(username)) {
            context.deny("Selecciona un usuario para continuar.");
            return false;
        }

        if (!configuration.isAllowed(username, context.getAction())) {
            logger.warn("Acceso denegado: usuario={}, action={}", username, context.getAction());
            context.deny("El usuario " + username + " no tiene permiso para esta accion.");
            return false;
        }

        logger.debug("Acceso permitido: usuario={}, action={}", username, context.getAction());
        return true;
    }

    @Override
    public void after(InvocationContext context) {
        // Empty method - No execute
    }
}
