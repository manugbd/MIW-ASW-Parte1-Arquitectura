package com.miw.presentation.di;

import jakarta.servlet.ServletContext;

public interface ServletContextAware {
	void setServletContext(ServletContext context);
}
