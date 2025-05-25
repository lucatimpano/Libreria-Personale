package gestore_libreria.observer;

import gestore_libreria.db.BookManager;
import gestore_libreria.db.BookRepositoryImplementor;
import gestore_libreria.db.ConcreteBookManager;
import gestore_libreria.db.SQLiteBookRepository;
import gestore_libreria.model.Book;
import gestore_libreria.ui.BooksPanelUI;

import java.util.List;

public class ConcreteBookObserver implements BookObserver{

    //qui ho un'istanza del ConcreteBookManager
    private BooksPanelUI bookPanel;
    private ConcreteBookManager db;

    public ConcreteBookObserver(BooksPanelUI bookPanel, ConcreteBookManager db) {
        this.bookPanel = bookPanel;
        this.db = db;
        this.db.attach(this);
        update();
    }

    @Override
    public void update() {
        System.out.println("Aggiorno la bookView");
        List<Book> books = db.getAllBook();
        bookPanel.displayBooks(books);
    }

    public void unsubscribe() {
        if(this.db != null) {
            this.db.detach(this);
            System.out.println("Unsubscribed");
        }
    }
}
