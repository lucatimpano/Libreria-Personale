package gestore_libreria.memento;

import gestore_libreria.model.Book;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookHistoryCaretakerTest {

    private BookHistoryCaretaker bookHistoryCaretaker;
    private Book testBook1;
    private Book testBook2;
    private BookMemento testBookMemento1;
    private BookMemento testBookMemento2;


    @Before
    public void setUp() throws Exception {
        bookHistoryCaretaker = new BookHistoryCaretaker();
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
        assertFalse(bookHistoryCaretaker.canUndo());
        assertFalse(bookHistoryCaretaker.canRedo());

        bookHistoryCaretaker.save(testBookMemento1);

        assertTrue(bookHistoryCaretaker.canUndo());
        assertFalse(bookHistoryCaretaker.canRedo());

        bookHistoryCaretaker.save(testBookMemento2);
        bookHistoryCaretaker.undo();

        assertTrue(bookHistoryCaretaker.canRedo());
    }

    @Test
    public void testUndo() {
        bookHistoryCaretaker.save(testBookMemento1);
        BookMemento bookMemento = bookHistoryCaretaker.undo();

        assertEquals(testBookMemento1, bookMemento);
        assertFalse(bookHistoryCaretaker.canUndo());
        assertTrue(bookHistoryCaretaker.canRedo());
    }

    @Test
    public void testRedo() {
        bookHistoryCaretaker.save(testBookMemento1);
        bookHistoryCaretaker.undo();

        BookMemento bookMemento = bookHistoryCaretaker.redo();
        assertEquals(testBookMemento1, bookMemento);
        assertTrue(bookHistoryCaretaker.canUndo());
        assertFalse(bookHistoryCaretaker.canRedo());
    }

    @Test
    public void testCanUndo() {
        assertFalse(bookHistoryCaretaker.canUndo());
        bookHistoryCaretaker.save(testBookMemento1);
        assertTrue(bookHistoryCaretaker.canUndo());
        bookHistoryCaretaker.undo();
        assertFalse(bookHistoryCaretaker.canUndo());
    }

    @Test
    public void testCanRedo() {
        assertFalse(bookHistoryCaretaker.canRedo());
        bookHistoryCaretaker.save(testBookMemento1);
        bookHistoryCaretaker.undo();
        assertTrue(bookHistoryCaretaker.canRedo());
        bookHistoryCaretaker.redo();
        assertFalse(bookHistoryCaretaker.canRedo());
    }

    @Test
    public void testCleanAll() {
        bookHistoryCaretaker.save(testBookMemento1);
        bookHistoryCaretaker.save(testBookMemento2);

        assertTrue(bookHistoryCaretaker.canUndo());
        bookHistoryCaretaker.cleanAll();

        assertFalse(bookHistoryCaretaker.canUndo());
        assertFalse(bookHistoryCaretaker.canRedo());
    }
}