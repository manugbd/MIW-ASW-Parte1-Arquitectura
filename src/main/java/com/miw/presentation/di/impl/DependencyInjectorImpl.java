package com.miw.presentation.di.impl;

import com.miw.presentation.di.DependencyInjector;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.ServletContextAware;
import com.miw.presentation.di.ServletRequestAware;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DependencyInjectorImpl implements DependencyInjector {

    private final Logger logger = LogManager.getLogger(getClass());
    private static DependencyInjector instance;

    public static DependencyInjector getInstance() {
        if (instance == null)
            instance = new DependencyInjectorImpl();
        return instance;
    }


    private DependencyInjectorImpl() {
    }

    @Override
    public void inject(Object target, HttpServletRequest req) throws IllegalAccessException, InvocationTargetException {
        String action = req.getParameter("action");
        // 2.3 Inyeccion por las interfaces existentes.
        if (target instanceof ServletRequestAware) {
            logger.debug("Injecting request in command {}", action);
            ((ServletRequestAware) target).setServletRequest(req);
        }
        if (target instanceof HttpSessionAware) {
            logger.debug("Injecting session in command {}", action);
            ((HttpSessionAware) target).setHttpSession(req.getSession());
        }
        if (target instanceof ServletContextAware) {
            logger.debug("Injecting session in command {}", action);
            ((ServletContextAware) target).setServletContext(req.getServletContext());
        }

        injectLogger(target);
    }

    private void injectLogger(Object target) throws IllegalAccessException, InvocationTargetException {
        // 2.3 Inyeccion del logger por setter, descubierto mediante reflexion.
        Method setter;
        try {
            setter = target.getClass().getMethod("setLogger", Logger.class);
        } catch (NoSuchMethodException e) {
            return;
        }
        setter.invoke(target, LogManager.getLogger(target.getClass()));
    }
}
