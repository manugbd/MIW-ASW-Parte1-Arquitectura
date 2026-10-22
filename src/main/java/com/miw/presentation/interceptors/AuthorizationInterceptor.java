package com.miw.presentation.interceptors;

import com.miw.presentation.di.HttpSessionAware;
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

        logger.warn("Autorizacion pendiente de implementar: action={}", context.getAction());
        context.deny("El interceptor de autorizacion esta pendiente de implementar");
        return false;
    }

    @Override
    public void after(InvocationContext context) {
        // Empty method - No execute
    }
}
