package com.miw.business.bookmanager;

import java.util.List;

import com.miw.model.Book;

public interface BookManagerService {
	List<Book> getBooks() throws Exception;
	Book getSpecialOffer() throws Exception;

}