package com.miw.business;

import com.miw.business.bookmanager.BookManagerService;

public interface ServicesFactory {
	BookManagerService getBookManagerService();
	
}
