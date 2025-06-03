package gestore_libreria.memento;

import gestore_libreria.model.Book;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookHistoryManagerTest {

    private BookHistoryManager bookHistoryManager;
    private Book testBook1;
    private Book testBook2;
    private BookMemento testBookMemento1;
    private BookMemento testBookMemento2;


    @Before
    public void setUp() throws Exception {
        bookHistoryManager = new BookHistoryManager();
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
        testBookMemento1 = new BookMemento(testBook1, BookMemento.OperationType.ADD);
        testBookMemento2 = new BookMemento(testBook2, BookMemento.OperationType.REMOVE);
    }

    @Test
    public void testSave() {
        assertFalse(bookHistoryManager.canUndo());
        assertFalse(bookHistoryManager.canRedo());

        bookHistoryManager.save(testBookMemento1);

        assertTrue(bookHistoryManager.canUndo());
        assertFalse(bookHistoryManager.canRedo());

        bookHistoryManager.save(testBookMemento2);
        bookHistoryManager.undo();

        assertTrue(bookHistoryManager.canRedo());
    }

    @Test
    public void testUndo() {
        bookHistoryManager.save(testBookMemento1);
        BookMemento bookMemento = bookHistoryManager.undo();

        assertEquals(testBookMemento1, bookMemento);
        assertFalse(bookHistoryManager.canUndo());
        assertTrue(bookHistoryManager.canRedo());
    }

    @Test
    public void testRedo() {
        bookHistoryManager.save(testBookMemento1);
        bookHistoryManager.undo();

        BookMemento bookMemento = bookHistoryManager.redo();
        assertEquals(testBookMemento1, bookMemento);
        assertTrue(bookHistoryManager.canUndo());
        assertFalse(bookHistoryManager.canRedo());
    }

    @Test
    public void testCanUndo() {
        assertFalse(bookHistoryManager.canUndo());
        bookHistoryManager.save(testBookMemento1);
        assertTrue(bookHistoryManager.canUndo());
        bookHistoryManager.undo();
        assertFalse(bookHistoryManager.canUndo());
    }

    @Test
    public void testCanRedo() {
        assertFalse(bookHistoryManager.canRedo());
        bookHistoryManager.save(testBookMemento1);
        bookHistoryManager.undo();
        assertTrue(bookHistoryManager.canRedo());
        bookHistoryManager.redo();
        assertFalse(bookHistoryManager.canRedo());
    }

    @Test
    public void testCleanAll() {
        bookHistoryManager.save(testBookMemento1);
        bookHistoryManager.save(testBookMemento2);

        assertTrue(bookHistoryManager.canUndo());
        bookHistoryManager.cleanAll();

        assertFalse(bookHistoryManager.canUndo());
        assertFalse(bookHistoryManager.canRedo());
    }
}