package gestore_libreria.db;

import gestore_libreria.memento.BookHistoryManager;
import gestore_libreria.memento.BookMemento;
import gestore_libreria.model.Book;
import gestore_libreria.observer.BookObserver;
import gestore_libreria.observer.Subject;

import java.util.List;

// Questa classe ora implementa l'interfaccia BookManager.
public class ConcreteBookManager extends Subject implements BookManager, BookHistoryManager.OnMementoListener  {

    private final BookRepositoryImplementor repository;
    private final BookHistoryManager historyManager;

    // Il costruttore riceve l'implementazione del repository.
    public ConcreteBookManager(BookRepositoryImplementor repository) {
        this.repository = repository;
        this.historyManager = new BookHistoryManager();
        this.historyManager.setOnMementoRestoreListener(this);
    }

    public BookHistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void addBook(Book book) {
        repository.save(book);
        historyManager.save(new BookMemento(book, BookMemento.OperationType.ADD));
        super.notifyObservers();
    }

    @Override
    public List<Book> getAllBook() {
        return repository.loadAll();
    }

    @Override
    public List<Book> findBookByTitle(String title) {
        return repository.findByTitle(title);
    }

    @Override
    public List<Book> filterBookByRating(int rating) {
        return repository.findByRating(rating);
    }

    @Override
    public List<Book> filterBookByReadingState(String readingState) {
        return repository.findByReadingState(readingState);
    }

    @Override
    public void updateBook(Book odlBook, Book book) {
        historyManager.save(new BookMemento(book, BookMemento.OperationType.UPDATE, odlBook));
        repository.update(book);
        super.notifyObservers();
    }

    @Override
    public void deleteBook(Book book) {
        historyManager.save(new BookMemento(book, BookMemento.OperationType.REMOVE));
        repository.delete(book);
        super.notifyObservers();
    }

    @Override
    public void restore(BookMemento memento, BookHistoryManager.ActionDirection direction) {
        switch (memento.getOperationType()) {
            case ADD:
                if (direction == BookHistoryManager.ActionDirection.UNDO) {
                    repository.delete(memento.getBookState());
                    System.out.println("Undo ADD: Rimosso libro " + memento.getBookState().getTitle());
                } else {
                    repository.save(memento.getBookState());
                    System.out.println("Redo ADD: Riaggiunto libro " + memento.getBookState().getTitle());
                }
                break;
            case REMOVE:
                if (direction == BookHistoryManager.ActionDirection.UNDO) {
                    repository.save(memento.getBookState());
                    System.out.println("Undo DELETE: Riaggiunto libro " + memento.getBookState().getTitle());
                } else {
                    repository.delete(memento.getBookState());
                    System.out.println("Redo DELETE: Rimosso libro " + memento.getBookState().getTitle());
                }
                break;
            case UPDATE:
                if (direction == BookHistoryManager.ActionDirection.UNDO) {
                    repository.update(memento.getPreviousBookState());
                    System.out.println("Undo UPDATE: Ripristinato libro " + memento.getPreviousBookState().getTitle() + " allo stato precedente.");
                } else {
                    repository.update(memento.getBookState());
                    System.out.println("Redo UPDATE: Ripristinato libro " + memento.getBookState().getTitle() + " allo stato successivo.");
                }
                break;
        }
        super.notifyObservers(); // Notifica la UI dopo il ripristino
    }
}