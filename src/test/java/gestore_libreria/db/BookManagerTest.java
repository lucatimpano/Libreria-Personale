package gestore_libreria.db;

import gestore_libreria.model.Book;
import gestore_libreria.model.SortCriteria;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class BookManagerTest {
    private BookManager manager;
    private Book testBook1;
    private Book testBook2;
    private Connection testConnection;

    //classe interna per istanziare un repository di test e un collegamento di test
    private class TestSQLiteBookRepository extends SQLiteBookRepository {
        @Override
        protected Connection getConnection() throws SQLException{
            return testConnection;
        }
    }

    @Before
    public void setUp() throws SQLException{

        testConnection = DriverManager.getConnection("jdbc:sqlite::memory:");
        Statement statement = testConnection.createStatement();
        String createTable = """
                   CREATE TABLE IF NOT EXISTS books(
                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                       title TEXT NOT NULL,
                       author TEXT NOT NULL,
                       isbn TEXT,
                       genre TEXT,
                       rating INTEGER,
                       readingState TEXT,
                       coverPath TEXT
                       );
                   """;
        statement.executeUpdate(createTable);

        SQLiteBookRepository repository = new TestSQLiteBookRepository();
        manager = new ConcreteBookManager(repository);

        clearDatabase();

        testBook1 = new Book.Builder("Il Signore degli Anelli", "J.R.R. Tolkien")
                .isbn("978-0544003415")
                .genre("Fantasy")
                .rating(5)
                .readingState("LETTO")
                .build();

        testBook2 = new Book.Builder("1984", "George Orwell")
                .isbn("978-0451524935")
                .genre("Distopia")
                .rating(4)
                .readingState("DA LEGGERE")
                .build();
    }

    private void clearDatabase() throws SQLException{
        if(testConnection != null || !testConnection.isClosed()){
            Statement statement = testConnection.createStatement();
            statement.execute("DELETE FROM books");
            statement.close();
        }
    }

    @After
    public void closeConnection() throws SQLException{
        if(testConnection != null && !testConnection.isClosed()){
            testConnection.close();
        }
    }

    @Test
    public void testAddBook() throws SQLException{
        manager.addBook(testBook1);

        List<Book> allBooks = manager.getAllBook(SortCriteria.NONE);
        assertEquals(1, allBooks.size());
        assertEquals("Il Signore degli Anelli", allBooks.get(0).getTitle());
        assertEquals("J.R.R. Tolkien", allBooks.get(0).getAuthor());
        assertTrue(allBooks.get(0).getId() > 0);        //verifichiamo che sia stato assegnato correttamente un id
    }

    @Test
    public void testGetAllBook() throws SQLException{
        manager.addBook(testBook1);
        manager.addBook(testBook2);

        List<Book> allBooks = manager.getAllBook(SortCriteria.NONE);
        assertEquals(2, allBooks.size());
    }

    @Test
    public void testUpdateBook() throws SQLException{
        manager.addBook(testBook1);

        List<Book> allBooks = manager.getAllBook(SortCriteria.NONE);
        Book originalBook = allBooks.get(0);

        Book updatedBook = new Book.Builder("Il Signore degli Anelli - Update Test", "J.R.R. Tolkien")
                .id(originalBook.getId())
                .isbn("978-0544003415")
                .genre("Fantasy Epico")
                .rating(5)
                .readingState("LETTO")
                .build();
        manager.updateBook(originalBook, updatedBook);

        List<Book> updatedBooks = manager.getAllBook(SortCriteria.NONE);
        assertEquals(1, updatedBooks.size());
        assertEquals("Il Signore degli Anelli - Update Test", updatedBooks.get(0).getTitle());
        assertEquals("J.R.R. Tolkien", updatedBooks.get(0).getAuthor());
        assertTrue(allBooks.get(0).getId() > 0);
    }

    @Test
    public void testDeleteBook() throws SQLException{
        manager.addBook(testBook1);

        List<Book> allBooks = manager.getAllBook(SortCriteria.NONE);
        manager.deleteBook(allBooks.get(0));
        List<Book> remainingBooks = manager.getAllBook(SortCriteria.NONE);

        assertEquals(0, remainingBooks.size());
    }

    @Test
    public void testFindByTitle() throws SQLException{
        manager.addBook(testBook1);

        List<Book> allBooks = manager.getAllBook(SortCriteria.NONE);
        List<Book> findBook = manager.findBookByTitle("Il Signore degli Anelli",SortCriteria.NONE);
        assertEquals(allBooks.get(0).getTitle(), findBook.get(0).getTitle());
    }

    @Test
    public void testFindByAuthor() throws SQLException{
        manager.addBook(testBook1);
        List<Book> allBooks = manager.getAllBook(SortCriteria.NONE);
        List<Book> findBook = manager.findBookByAuthor("J.R.R. Tolkien",SortCriteria.NONE);
        assertEquals(allBooks.get(0).getAuthor(), findBook.get(0).getAuthor());
    }

    @Test
    public void testFilterByRating() throws SQLException{
        manager.addBook(testBook1);
        List<Book> allBooks = manager.getAllBook(SortCriteria.NONE);
        List<Book> filteredBook = manager.filterBookByRating(5,SortCriteria.NONE);
        assertEquals(allBooks.get(0).getRating(), filteredBook.get(0).getRating());
    }

    @Test
    public void testFilterByReadingState() throws SQLException{
        manager.addBook(testBook1);
        List<Book> allBooks = manager.getAllBook(SortCriteria.NONE);
        List<Book> filteredBook = manager.filterBookByReadingState("LETTO",SortCriteria.NONE);
        assertEquals(allBooks.get(0).getReadingState(), filteredBook.get(0).getReadingState());
    }
}