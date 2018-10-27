package hu.uni.miskolc.iit.swtest.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import hu.uni.miskolc.iit.swtest.exceptions.DuplicatedBookEntryException;
import hu.uni.miskolc.iit.swtest.exceptions.EntryNotFoundException;
import hu.uni.miskolc.iit.swtest.model.Book;
import hu.uni.miskolc.iit.swtest.model.Genre;

public class DaoFile implements BookDAO{
	
	private File database;
	public static final String FIELD_SEPARATOR = ";";
	
	public DaoFile(File database) {
		this.database = database;
	}
	
	public void createBook(Book book) throws DuplicatedBookEntryException {
		ArrayList<Book> bookList = readBooks();
		if(bookList.contains(book)) {
			throw new DuplicatedBookEntryException(book + " is exists!");
		}
		
		BufferedWriter bf = null;
		try{
			bf = new BufferedWriter(new FileWriter(database, true));
			String record = getNextID() + FIELD_SEPARATOR + marshalToRecord(book);
			bf.write(record);
			bf.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally {
			try {
				bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public ArrayList<Book> readBooks() {
		ArrayList<Book> bookList = new ArrayList<Book>();
		BufferedReader bf = null;
		try{
			bf = new BufferedReader(new FileReader(database));
			String record;
			while( (record = bf.readLine()) != null ) {
				Book book = recordToBook(record);
				if(book != null)
					bookList.add(book);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bookList;
	}

	public void updeateBook(Book bookToBeUpdated, Book updatedBook) throws EntryNotFoundException {
		ArrayList<Book> bookList = readBooks();
		int bookIndex = bookList.indexOf(bookToBeUpdated);
		if(bookIndex == -1)
			throw new EntryNotFoundException("The requested book does not exists: " + bookToBeUpdated);
		updatedBook.setId(bookToBeUpdated.getId());
		bookList.set(bookIndex, updatedBook);
		overrideDatabase(bookList);		
	}

	public void deleteBook(Book book) throws EntryNotFoundException {
		ArrayList<Book> bookList = readBooks();
		if(!bookList.remove(book))
			throw new EntryNotFoundException("The requested book does not exists: " + book);
		
	}

	public void setBookRented(Book book, boolean rented) throws EntryNotFoundException {
		Book rentedBook = new Book(book);
		rentedBook.setRented(rented);
		updeateBook(book, rentedBook);
	}
	
	private String marshalToRecord(Book book) {
		return book.getTitle() + FIELD_SEPARATOR + book.getAuthor() + FIELD_SEPARATOR +
				book.getGenre() + FIELD_SEPARATOR + book.getReleasedYear() + FIELD_SEPARATOR + book.isRented();
	}
	
	private Book recordToBook(String record) {
		Book book = new Book();
		StringTokenizer strt = new StringTokenizer(record, FIELD_SEPARATOR);
		try {
			book.setId(Integer.parseInt(strt.nextElement() + ""));
			book.setTitle(strt.nextElement() + "");
			book.setAuthor(strt.nextElement() + "");
			String genre = (strt.nextElement() + "").toUpperCase();
			if(genre.equals("SCIFI")) {
				book.setGenre(Genre.SCIFI);
			}else if(genre.equals("FANTASY")) {
				book.setGenre(Genre.FANTASY);
			}else if(genre.equals("ADVENTURE")) {
				book.setGenre(Genre.ADVENTURE);
			}else if(genre.equals("HOBBY")) {
				book.setGenre(Genre.HOBBY);
			}else if(genre.equals("SCHOOLBOOK")) {
				book.setGenre(Genre.SCHOOLBOOK);
			}
			book.setReleasedYear(Integer.valueOf(strt.nextElement() + ""));
			book.setRented(Boolean.valueOf(strt.nextElement() + ""));
		}catch(Exception e) {
			book = null;
		}
		return book;
	}
	
	private int getNextID() {
		int nextID = 0;
		BufferedReader bf = null;
		try{
			bf = new BufferedReader(new FileReader(database));
			ArrayList<Integer> ids = new ArrayList();
			String line;
			while((line = bf.readLine()) != null) {
				StringTokenizer strt = new StringTokenizer(line, ";");
				try {
					int id = Integer.parseInt( strt.nextElement() + "" );
					ids.add(id);					
				}catch(NumberFormatException e) {
					
				}
			}
			nextID = Collections.max(ids) + 1;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(NoSuchElementException e){
			nextID = 0;
		}finally {
			try {
				bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return nextID;
	}
	
	private void overrideDatabase(Collection<Book> bookList) {
		BufferedWriter bfw = null;
		try {
			bfw = new BufferedWriter(new FileWriter(database, false));
			Iterator<Book> bookIt = bookList.iterator();
			while(bookIt.hasNext()) {
				Book book = bookIt.next();
				bfw.write( book.getId() + FIELD_SEPARATOR + marshalToRecord(book) );
				bfw.newLine();
			}
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				bfw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	

}
