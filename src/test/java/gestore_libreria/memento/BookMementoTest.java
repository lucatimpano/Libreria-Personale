package gestore_libreria.memento;

import gestore_libreria.model.Book;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookMementoTest {
    private Book testBook1;
    private Book testBookNew;
    private Book testBookOld;
    private BookMemento memento1;
    private BookMemento memento2;


    @Before
    public void setUp() {
        testBook1 = new Book.Builder("Il Signore degli Anelli", "J.R.R. Tolkien")
                .isbn("978-0544003415")
                .genre("Fantasy")
                .rating(5)
                .readingState("LETTO")
                .build();

        testBookNew = new Book.Builder("1984", "George Orwell")
                .isbn("978-0451524935")
                .genre("Distopia")
                .rating(4)
                .readingState("DA LEGGERE")
                .build();
        testBookOld = new Book.Builder("1984 - old", "George Orwell")
                .isbn("978-0451524935")
                .genre("Distopia")
                .rating(4)
                .readingState("DA LEGGERE")
                .build();
        memento1 = new BookMemento(testBook1, BookMemento.OperationType.ADD);
        memento2 = new BookMemento(testBookNew, BookMemento.OperationType.UPDATE, testBookOld);


    }
    @Test(expected = IllegalArgumentException.class)
    public void testNoUpdateConstructor(){
        new BookMemento(testBook1, BookMemento.OperationType.UPDATE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoAddNoDeleteConstructor(){
        new BookMemento(testBookNew, BookMemento.OperationType.ADD, testBookOld);
        new BookMemento(testBookNew, BookMemento.OperationType.REMOVE, testBookOld);
    }

    @Test
    public void testGetBookState() {
        assertEquals(testBook1, memento1.getBookState());
    }

    @Test
    public void testGetOperationType() {
        assertEquals(BookMemento.OperationType.ADD, memento1.getOperationType());
    }

    @Test
    public void getPreviousBookState() {
        assertEquals(testBookOld, memento2.getPreviousBookState());
    }
}