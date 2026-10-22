package com.miw.presentation.interceptors;

public interface Interceptor {

    void intercept(InvocationContext context) throws Exception;

}