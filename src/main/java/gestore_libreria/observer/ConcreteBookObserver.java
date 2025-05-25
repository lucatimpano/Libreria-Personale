package gestore_libreria.observer;

import gestore_libreria.db.BookManager;
import gestore_libreria.db.BookRepositoryImplementor;
import gestore_libreria.db.ConcreteBookManager;
import gestore_libreria.db.SQLiteBookRepository;
import gestore_libreria.ui.BooksPanelUI;

public class ConcreteBookObserver implements BookObserver{

    //qui ho un'istanza del ConcreteBookManager
    private BooksPanelUI bookPanel;
    private ConcreteBookManager db;

    public ConcreteBookObserver(BooksPanelUI bookPanel, ConcreteBookManager db) {
        this.bookPanel = bookPanel;
        this.db = db;
        this.db.attach(this);
    }

    @Override
    public void update() {
        //TODO
    }
}
