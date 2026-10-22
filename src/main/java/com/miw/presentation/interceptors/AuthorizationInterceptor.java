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
    public void intercept(InvocationContext context) throws Exception {
        if (logger == null) throw new IllegalStateException("El framework no ha inyectado el logger");

        context.proceed();
    }
}
