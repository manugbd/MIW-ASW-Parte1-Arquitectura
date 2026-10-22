package com.miw.presentation.interceptors;

public interface Interceptor {
    boolean before(InvocationContext context) throws Exception;

    void after(InvocationContext context) throws Exception;
}