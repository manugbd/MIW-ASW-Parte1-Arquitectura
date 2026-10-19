package com.miw.presentation.di;

import jakarta.servlet.http.HttpSession;

public interface HttpSessionAware {
	void setHttpSession(HttpSession session);
}
