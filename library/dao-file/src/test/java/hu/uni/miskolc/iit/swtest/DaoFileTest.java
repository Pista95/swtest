package hu.uni.miskolc.iit.swtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import hu.uni.miskolc.iit.swtest.dao.BookDAO;
import hu.uni.miskolc.iit.swtest.dao.DaoFile;
import hu.uni.miskolc.iit.swtest.exceptions.DuplicatedBookEntryException;
import hu.uni.miskolc.iit.swtest.exceptions.EntryNotFoundException;
import hu.uni.miskolc.iit.swtest.model.Book;
import hu.uni.miskolc.iit.swtest.model.Genre;

public class DaoFileTest {

	private static File DEFAULT_DB_STATE;
	private File temporalDB;
	private BookDAO daoFile;
	
	@BeforeClass
	public static void beforeClass() {
		DEFAULT_DB_STATE = new File("src/resources/bookDB.csv");
	}
	
	@Before
	public void setUp() {
		InputStream is = null;
		OutputStream os = null;
		try {
			temporalDB = File.createTempFile("bookDB", "csv");
			is = new FileInputStream(DEFAULT_DB_STATE);
			os = new FileOutputStream(temporalDB);
			byte buffer[] = new byte[1024];
			int length;
			while( (length = is.read(buffer)) > 0 ) {
				os.write(buffer, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		daoFile = new DaoFile(temporalDB);
	}
	
	@Test
	public void testRead() {
		List<Book> expected = new ArrayList<Book>();
		Book book1 = new Book("Számítógép hálózatok", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK,2013,false);
		book1.setId(0);
		Book book2 = new Book("Operációs rendszerek","Andrew S. Tanenbaum",Genre.SCHOOLBOOK,2007,false);
		book2.setId(1);
		Book book3 = new Book("A Gyűrűk Ura","J. R. R. Tolkien",Genre.FANTASY,2018,true);
		book3.setId(2);
		expected.add(book1);
		expected.add(book2);
		expected.add(book3);
		Collection<Book> actual = daoFile.readBooks();
		assertEquals(expected.size(), actual.size());
		for(Book book: expected) {
			assertTrue(actual.contains(book));
		}
	}
	
	@Test
	public void testCreateBook() throws DuplicatedBookEntryException, EntryNotFoundException {
		Book newBook = new Book("Számítógép-architektúrák", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK, 2006, false);
		daoFile.createBook(newBook);
		Collection<Book> bookList = daoFile.readBooks();
		assertTrue(bookList.contains(newBook));
		daoFile.deleteBook(newBook);
	}
	
	@Test
	public void testUpdateBook() throws EntryNotFoundException {
		Book book = new Book("Számítógép hálózatok", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK,2013,false);
		Book updatedBook = new Book(book);
		updatedBook.setTitle("Számítógép hálózatok - bővített kiadás");
		daoFile.updeateBook(book, updatedBook);
		Collection<Book> bookList = daoFile.readBooks();
		assertTrue(bookList.contains(updatedBook));
		daoFile.updeateBook(updatedBook, book);
	}
	
	@Test
	public void testSetBookRented() throws EntryNotFoundException {
		Book book = new Book("Számítógép hálózatok", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK,2013,false);
		daoFile.setBookRented(book, true);
		book.setRented(true);
		Collection<Book> bookList = daoFile.readBooks();
		assertTrue(bookList.contains(book));
		daoFile.setBookRented(book, false);
	}
	
	@Test(expected = DuplicatedBookEntryException.class)
	public void testCreateBookThrowsDuplicatedBookEntryException() throws DuplicatedBookEntryException {
		Book book = new Book("Számítógép hálózatok", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK,2013,false);
		daoFile.createBook(book);
	}
	
	@Test(expected = EntryNotFoundException.class)
	public void testUpdateBookThrowsEntryNotFoundException() throws EntryNotFoundException {
		Book book = new Book("A Hobbit", "J. R. R. Tolkien", Genre.FANTASY,2018,false);
		Book updatedBook = new Book(book);
		updatedBook.setRented(true);
		daoFile.updeateBook(book, updatedBook);
	}
	
	@Test(expected = EntryNotFoundException.class)
	public void testDeleteBookThrowsEntryNotFoundException() throws EntryNotFoundException {
		Book book = new Book("A Hobbit", "J. R. R. Tolkien", Genre.FANTASY,2018,false);
		daoFile.deleteBook(book);
	}
}
