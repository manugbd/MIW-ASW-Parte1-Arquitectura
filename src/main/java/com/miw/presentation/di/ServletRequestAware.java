package com.miw.presentation.di;

import jakarta.servlet.http.HttpServletRequest;

public interface ServletRequestAware {
	void setServletRequest(HttpServletRequest request);
}
