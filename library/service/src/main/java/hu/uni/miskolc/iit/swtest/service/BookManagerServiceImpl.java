package hu.uni.miskolc.iit.swtest.service;

import java.util.ArrayList;
import java.util.Collection;

import hu.uni.miskolc.iit.swtest.dao.BookDAO;
import hu.uni.miskolc.iit.swtest.exceptions.BookAlreadyExistsException;
import hu.uni.miskolc.iit.swtest.exceptions.BookDoesNotExistsException;
import hu.uni.miskolc.iit.swtest.exceptions.DuplicatedBookEntryException;
import hu.uni.miskolc.iit.swtest.exceptions.EntryNotFoundException;
import hu.uni.miskolc.iit.swtest.model.Book;

public class BookManagerServiceImpl implements BookManagerService{

	private BookDAO bookDAO;
	
	public BookManagerServiceImpl(BookDAO bookDAO) {
		super();
		this.bookDAO = bookDAO;
	}
	
	public void recordBook(Book book) throws BookAlreadyExistsException {
		try {
			bookDAO.createBook(book);
		} catch (DuplicatedBookEntryException e) {
			throw new BookAlreadyExistsException(e);
		}		
	}

	public void updateBook(Book bookToBeUpdated, Book updatedBook) throws BookDoesNotExistsException {
		try {
			bookDAO.updeateBook(bookToBeUpdated, updatedBook);
		} catch (EntryNotFoundException e) {
			throw new BookDoesNotExistsException(e);
		}	
	}

	public void setBookRented(Book book) throws BookDoesNotExistsException {
		try {
			bookDAO.setBookRented(book, true);
		} catch (EntryNotFoundException e) {
			throw new BookDoesNotExistsException(e);
		}
	}

	public void setBookUnRented(Book book) throws BookDoesNotExistsException {
		try {
			bookDAO.setBookRented(book, false);
		} catch (EntryNotFoundException e) {
			throw new BookDoesNotExistsException(e);
		}	
	}

	public Collection<Book> listBooks() {
		return bookDAO.readBooks();
	}

	public Collection<Book> listRentedBooks() {
		Collection<Book> rentedBookList = new ArrayList<Book>();
		Collection<Book> bookList = bookDAO.readBooks();
		
		for(Book book: bookList)
			if(book.isRented())
				rentedBookList.add(book);
		
		return rentedBookList;
	}

}
