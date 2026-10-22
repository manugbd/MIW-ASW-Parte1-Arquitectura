package com.miw.presentation.di;

import jakarta.servlet.http.HttpServletRequest;

public interface DependencyInjector {

    void inject(Object target, HttpServletRequest req) throws Exception;
}
