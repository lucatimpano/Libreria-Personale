package gestore_libreria.observer;

import gestore_libreria.db.BookManager;
import gestore_libreria.db.BookRepositoryImplementor;
import gestore_libreria.db.ConcreteBookManager;
import gestore_libreria.db.SQLiteBookRepository;

public class ConcreteBookObserver implements BookObserver{

    //qui ho un'istanza del ConcreteBookManager
    BookRepositoryImplementor repo = new SQLiteBookRepository();
    BookManager db = new ConcreteBookManager(repo);


    @Override
    public void update() {
        //TODO
    }
}
