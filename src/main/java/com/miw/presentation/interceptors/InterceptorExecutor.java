package com.miw.presentation.interceptors;

import com.miw.presentation.commands.Command;

import java.util.ArrayList;
import java.util.List;

public final class InterceptorExecutor {

    public void execute(Command command, InvocationContext context, List<Interceptor> interceptors) throws Exception {
        List<Interceptor> entered = new ArrayList<>();

        try {
            for (Interceptor interceptor : interceptors) {
                entered.add(interceptor);
                boolean allowed = interceptor.before(context);
                if (!allowed || context.isDenied()) {
                    if (!context.isDenied())
                        context.deny("La ejecucion ha sido bloqueada por un interceptor");
                    break;
                }
            }

            if (!context.isDenied()) {
                context.startCommand();
                command.execute();
                context.completeCommand();
            }
        } catch (Exception | Error e) {
            context.recordFailure(e);
        }

        for (int i = entered.size() - 1; i >= 0; i--) {
            try {
                entered.get(i).after(context);
            } catch (Exception | Error e) {
                context.recordFailure(e);
            }
        }

        Throwable failure = context.getFailure();
        if (failure instanceof Exception exception)
            throw exception;
        if (failure instanceof Error error)
            throw error;
    }
}