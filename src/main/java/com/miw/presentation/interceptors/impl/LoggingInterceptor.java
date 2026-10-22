package com.miw.presentation.interceptors.impl;

import com.miw.presentation.interceptors.Interceptor;
import com.miw.presentation.interceptors.InvocationContext;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Objects;

public class LoggingInterceptor implements Interceptor {

    private long startTime;
    private boolean started;

    // 2.3 Logger por setter
    private Logger logger;

    public void setLogger(Logger logger) {
        this.logger = Objects.requireNonNull(logger);
    }

    @Override
    public boolean before(InvocationContext context) {
        if (logger == null)
            throw new IllegalStateException("El framework no ha inyectado el logger");

        startTime = System.nanoTime();
        started = true;
        logger.info("Inicio: action={}, command={}, fecha={}",
                context.getAction(), context.getCommandName(), Instant.now());
        return true;
    }

    @Override
    public void after(InvocationContext context) {
        if (!started)
            return;

        double milliseconds = (System.nanoTime() - startTime) / 1_000_000.0;

        try {
            if (context.getFailure() != null)
                logger.error("Finalizacion con excepcion: command={}", context.getCommandName(), context.getFailure());
            else if (context.isDenied())
                logger.warn("Ejecucion denegada: action={}, motivo={}", context.getAction(), context.getDenialMessage());
            else if (context.wasCommandExecuted())
                logger.info("Finalizacion sin excepcion propagada: command={}", context.getCommandName());
            else
                logger.warn("Comando no ejecutado: action={}", context.getAction());
        } finally {
            logger.info("Duracion: action={}, command={}, milisegundos={}", context.getAction(), context.getCommandName(), milliseconds);
        }
    }
}