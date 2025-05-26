package gestore_libreria.db;

import java.util.List;

import gestore_libreria.model.Book;


public interface BookRepositoryImplementor {

    //definisco i metodi che devono essere presenti nei database concreti

    void save(Book book);
    List<Book> loadAll();
    List<Book> findByTitle(String title);
    List<Book> findByRating(int rating);
    List<Book> findByReadingState(String readingState);
    void delete(Book book);
    void update(Book book);
}
