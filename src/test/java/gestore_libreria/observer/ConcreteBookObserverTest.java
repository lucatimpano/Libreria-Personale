package gestore_libreria.observer;

import gestore_libreria.db.ConcreteBookManager;
import gestore_libreria.model.Book;
import gestore_libreria.ui.BooksPanelUI;
import gestore_libreria.ui.GestoreLibreriaUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteBookObserverTest {

    static class TestGestoreLibreriaUI extends GestoreLibreriaUI {
        public int updateUndoRedoCallCount = 0;

        public TestGestoreLibreriaUI(ConcreteBookManager db) {
            super(db);
        }

        @Override
        public void updateUndoRedoMenuState() {
            updateUndoRedoCallCount++;
        }
    }

    static class TestBooksPanelUI extends BooksPanelUI {
        public int displayBooksCallCount = 0;
        public List<Book> displayedBooks = new ArrayList<>();

        public TestBooksPanelUI(GestoreLibreriaUI gestoreLibreriaUI) {
            super(gestoreLibreriaUI);
        }

        @Override
        public void displayBooks(List<Book> books) {
            displayBooksCallCount++;
            displayedBooks.clear();
            displayedBooks.addAll(books);
        }
    }

    static class TestBookManager extends ConcreteBookManager {
        private final List<BookObserver> observers = new ArrayList<>();
        private List<Book> books = new ArrayList<>();

        public TestBookManager() {
            super(null);
        }

        public void setBooks(List<Book> books) {
            this.books = books;
        }

        @Override
        public List<Book> getAllBook() {
            return new ArrayList<>(books);
        }

        @Override
        public void attach(BookObserver observer) {
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        }

        @Override
        public void detach(BookObserver observer) {
            observers.remove(observer);
        }

        public boolean isObserverAttached(BookObserver observer) {
            return observers.contains(observer);
        }

        public void notifyObservers() {
            List<BookObserver> observerList = new ArrayList<>(observers);
            for (BookObserver o : observerList) {
                o.update();
            }
        }
    }

    private TestGestoreLibreriaUI testUI;
    private TestBooksPanelUI testBookPanel;
    private TestBookManager testManager;
    private ConcreteBookObserver observer;

    @BeforeEach
    void setUp() {
        testManager = new TestBookManager();
        testUI = new TestGestoreLibreriaUI(testManager);
        testBookPanel = new TestBooksPanelUI(testUI);

        observer = new ConcreteBookObserver(testUI, testBookPanel, testManager);

    }

    @Test
    void testConstructor() {

        // Verifica che l'osservatore sia registrato
        assertTrue(testManager.isObserverAttached(observer));

        // Verifica che l'update iniziale sia stato chiamato
        assertEquals(1, testBookPanel.displayBooksCallCount);
        assertEquals(1, testUI.updateUndoRedoCallCount);
    }

    @Test
    void testUpdate() {

        testBookPanel.displayBooksCallCount = 0;
        testUI.updateUndoRedoCallCount = 0;

        List<Book> testBooks = new ArrayList<>();
        Book testBook1 = new Book.Builder("Il Signore degli Anelli", "J.R.R. Tolkien")
                .isbn("978-0544003415")
                .genre("Fantasy")
                .rating(5)
                .readingState("LETTO")
                .build();
        testBooks.add(testBook1);
        testManager.setBooks(testBooks);

        observer.update();

        assertEquals(1, testBookPanel.displayBooksCallCount);
        assertEquals(1, testBooks.size(), testBookPanel.displayedBooks.size());
        assertEquals("Il Signore degli Anelli", testBookPanel.displayedBooks.get(0).getTitle());

        assertEquals(1, testUI.updateUndoRedoCallCount);
    }


    @Test
    void testUnsubscribe() {
        assertTrue(testManager.isObserverAttached(observer));

        observer.unsubscribe();

        assertFalse(testManager.isObserverAttached(observer));
    }

    @Test
    void testExternalNotifyObserver() {

        testBookPanel.displayBooksCallCount = 0;
        testUI.updateUndoRedoCallCount = 0;

        List<Book> newBooks = new ArrayList<>();
        Book testBook1 = new Book.Builder("Il Signore degli Anelli", "J.R.R. Tolkien")
                .isbn("978-0544003415")
                .genre("Fantasy")
                .rating(5)
                .readingState("LETTO")
                .build();
        newBooks.add(testBook1);
        testManager.setBooks(newBooks);

        // Simuliamo una notifica esterna
        testManager.notifyObservers();

        assertEquals(1, testBookPanel.displayBooksCallCount);
        assertEquals(1, testBookPanel.displayedBooks.size());
        assertEquals("Il Signore degli Anelli", testBookPanel.displayedBooks.get(0).getTitle());
    }

    @Test
    void testMultipleUpdates() {

        // Esegui tre aggiornamenti
        observer.update();
        observer.update();
        observer.update();

        // Il numero di update che ci si aspetta è 4, uno del costruttore e tre invocati dal seguente test
        assertEquals(4, testBookPanel.displayBooksCallCount);
        assertEquals(4, testUI.updateUndoRedoCallCount);
    }
}