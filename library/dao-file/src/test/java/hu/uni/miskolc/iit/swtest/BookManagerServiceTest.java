package hu.uni.miskolc.iit.swtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import hu.uni.miskolc.iit.swtest.dao.DaoFile;
import hu.uni.miskolc.iit.swtest.exceptions.BookAlreadyExistsException;
import hu.uni.miskolc.iit.swtest.exceptions.BookDoesNotExistsException;
import hu.uni.miskolc.iit.swtest.exceptions.DuplicatedBookEntryException;
import hu.uni.miskolc.iit.swtest.model.Book;
import hu.uni.miskolc.iit.swtest.model.Genre;
import hu.uni.miskolc.iit.swtest.service.BookManagerServiceImpl;

public class BookManagerServiceTest {

	private BookManagerServiceImpl bookService;
	private static File DEFAULT_DB_STATE;
	private File temporalDB;

	@BeforeClass
	public static void beforeClass() {
		DEFAULT_DB_STATE = new File("src\\resources\\bookDB.csv");
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
		
		
		
		bookService = new BookManagerServiceImpl(new DaoFile(temporalDB));
	}
	
	@Test
	public void testListBooks() {
		bookService.listBooks();
	}
	
	@Test
	public void testListRentedBooks() {
		bookService.listRentedBooks();
	}
	
	@Test
	public void testRecordBook() throws BookAlreadyExistsException {
		Book newBook = new Book("Számítógép-architektúrák", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK, 2006, false);
		bookService.recordBook(newBook);
	}
	
	@Test(expected = BookAlreadyExistsException.class)
	public void testRecordBookThrowsDuplicatedBookEntryException() throws BookAlreadyExistsException{
		Book newBook = new Book("Számítógép hálózatok", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK,2013,false);
		bookService.recordBook(newBook);
	}
	
	@Test
	public void testUpdateBook() throws BookDoesNotExistsException {
		Book book = new Book("Számítógép hálózatok", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK,2013,false); 
		Book updatedBook = new Book(book);
		updatedBook.setTitle("Számítógép hálózatok - bővített kiadás");
		
		bookService.updateBook(book, updatedBook);
	}
	
	@Test(expected = BookDoesNotExistsException.class)
	public void testUpdateBookThrowsBookDoesNotExistsException() throws BookDoesNotExistsException {
		Book book = new Book("Számítógép-architektúrák", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK, 2006, false);
		Book updatedBook = new Book(book);
		updatedBook.setTitle("Számítógép hálózatok - bővített kiadás");
		
		bookService.updateBook(book, updatedBook);
	}
	
	@Test(expected = BookDoesNotExistsException.class)
	public void testSetBookRentedThrowsBookDoesNotExistsException() throws BookDoesNotExistsException {
		Book book = new Book("Számítógép-architektúrák", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK, 2006, false);

		bookService.setBookRented(book);
	}
	
	@Test(expected = BookDoesNotExistsException.class)
	public void testSetBookUnRentedhrowsBookDoesNotExistsException() throws BookDoesNotExistsException {
		Book book = new Book("Számítógép-architektúrák", "Andrew S. Tanenbaum", Genre.SCHOOLBOOK, 2006, false);

		bookService.setBookUnRented(book);
	}
}
