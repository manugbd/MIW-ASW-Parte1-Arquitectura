package com.miw.presentation.interceptors;

public interface InvocationContext {

    String getAction();

    String getCommandName();

    void proceed() throws Exception;

    void deny(String message);

    boolean isDenied();

    String getDenialMessage();

    boolean wasCommandExecuted();
}