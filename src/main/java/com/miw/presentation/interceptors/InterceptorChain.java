package com.miw.presentation.interceptors;

import com.miw.presentation.commands.Command;

import java.util.List;
import java.util.Objects;

public final class InterceptorChain implements InvocationContext {

    private final String action;
    private final Command command;
    private final List<Interceptor> interceptors;

    private int position;

    private boolean commandStarted;
    private boolean commandExecuted;

    private boolean denied;
    private String denialMessage;

    public InterceptorChain(String action, Command command, List<Interceptor> interceptors) {
        this.action = Objects.requireNonNull(action);
        this.command = Objects.requireNonNull(command);
        this.interceptors = List.copyOf(interceptors);
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public String getCommandName() {
        return command.getClass().getName();
    }

    @Override
    public void proceed() throws Exception {
        if (denied)
            throw new IllegalStateException("No se puede continuar una ejecución denegada");

        if (commandStarted)
            throw new IllegalStateException("El comando ya se ha intentado ejecutar");

        if (position < interceptors.size()) {
            Interceptor interceptor = interceptors.get(position++);
            interceptor.intercept(this);
            return;
        }

        commandStarted = true;
        command.execute();
        commandExecuted = true;
    }

    @Override
    public void deny(String message) {
        if (commandStarted)
            throw new IllegalStateException("No se puede denegar después de iniciar el comando");

        denied = true;
        denialMessage = Objects.requireNonNull(message);
    }

    @Override
    public boolean isDenied() {
        return denied;
    }

    @Override
    public String getDenialMessage() {
        return denialMessage;
    }

    @Override
    public boolean wasCommandExecuted() {
        return commandExecuted;
    }
}