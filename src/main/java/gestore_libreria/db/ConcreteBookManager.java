package gestore_libreria.db;

import gestore_libreria.model.Book;
import gestore_libreria.observer.Subject;

import java.util.List;

// Questa classe ora implementa l'interfaccia BookManager.
public class ConcreteBookManager extends Subject implements BookManager  {

    private final BookRepositoryImplementor repository;

    // Il costruttore riceve l'implementazione del repository.
    public ConcreteBookManager(BookRepositoryImplementor repository) {
        this.repository = repository;
    }

    @Override
    public void addBook(Book book) {
        repository.save(book);
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
}