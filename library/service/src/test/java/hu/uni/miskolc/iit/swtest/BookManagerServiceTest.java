package hu.uni.miskolc.iit.swtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.BeforeClass;

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
		
		//bookService = new BookManagerServiceImpl(new DaoFile(temporalDB));
	}
}
