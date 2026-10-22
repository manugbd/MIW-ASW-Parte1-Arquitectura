package com.miw.presentation.interceptors;

import java.util.Objects;

public final class InvocationContext {
    private final String action;
    private final String commandName;
    private boolean commandStarted;
    private boolean commandExecuted;
    private boolean denied;
    private String denialMessage;
    private Throwable failure;

    public InvocationContext(String action, String commandName) {
        this.action = Objects.requireNonNull(action);
        this.commandName = Objects.requireNonNull(commandName);
    }

    public String getAction() {
        return action;
    }

    public String getCommandName() {
        return commandName;
    }

    public boolean wasCommandExecuted() {
        return commandExecuted;
    }

    public boolean isDenied() {
        return denied;
    }

    public String getDenialMessage() {
        return denialMessage;
    }

    public Throwable getFailure() {
        return failure;
    }

    public void deny(String message) {
        if (commandStarted)
            throw new IllegalStateException("No se puede denegar un comando ya iniciado");
        denialMessage = Objects.requireNonNull(message);
        denied = true;
    }

    void startCommand() {
        if (commandStarted)
            throw new IllegalStateException("El comando ya se ha iniciado");
        commandStarted = true;
    }

    void completeCommand() {
        commandExecuted = true;
    }

    void recordFailure(Throwable error) {
        if (failure == null)
            failure = error;
        else if (failure != error)
            failure.addSuppressed(error);
    }
}