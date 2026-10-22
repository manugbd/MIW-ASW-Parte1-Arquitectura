package com.miw.presentation.interceptors;

import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Objects;

public class LoggingInterceptor implements Interceptor {

    // 2.3 Logger por setter
    private Logger logger;

    public void setLogger(Logger logger) {
        this.logger = Objects.requireNonNull(logger);
    }

    @Override
    public void intercept(InvocationContext context) throws Exception {
        if (logger == null) throw new IllegalStateException("El framework no ha inyectado el logger");

        Instant startDate = Instant.now();
        long startTime = System.nanoTime();

        logger.info("Inicio: action={}, command={}, fecha={}",
                context.getAction(),
                context.getCommandName(),
                startDate);

        try {
            context.proceed();

            if (context.isDenied()) {
                logger.warn("Ejecucion denegada: action={}, motivo={}", context.getAction(), context.getDenialMessage());

            } else if (context.wasCommandExecuted()) {
                logger.info("Finalizacion correcta: command={}", context.getCommandName());

            } else logger.warn("Cadena interrumpida sin ejecutar el comando: action={}", context.getAction());

        } catch (Exception | Error e) {
            logger.error("Finalizacion con excepcion: command= {}", context.getCommandName(), e);
            throw e;

        } finally {
            double elapsedMilliseconds = (System.nanoTime() - startTime) / 1_000_000.0;

            logger.info("Duracion: action={}, command={}, milisegundos={}",
                    context.getAction(),
                    context.getCommandName(),
                    elapsedMilliseconds);
        }
    }
}