package hu.uni.miskolc.iit.swtest.service;

import java.util.Collection;

import hu.uni.miskolc.iit.swtest.exceptions.BookAlreadyExistsException;
import hu.uni.miskolc.iit.swtest.exceptions.BookDoesNotExistsException;
import hu.uni.miskolc.iit.swtest.model.Book;

public interface BookManagerService {
	void recordBook(Book book) throws BookAlreadyExistsException;
	void updateBook(Book bookToBeUpdated, Book updatedBook) throws BookDoesNotExistsException;
	
	void setBookRented(Book book) throws BookDoesNotExistsException;
	void setBookUnRented(Book book) throws BookDoesNotExistsException;
	
	Collection<Book> listBooks();
	Collection<Book> listRentedBooks();
}
