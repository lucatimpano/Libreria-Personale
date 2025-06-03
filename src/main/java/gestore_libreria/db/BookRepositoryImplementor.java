package gestore_libreria.db;

import java.util.List;

import gestore_libreria.model.Book;
import gestore_libreria.model.SortCriteria;


public interface BookRepositoryImplementor {

    //definisco i metodi che devono essere presenti nei database concreti

    void save(Book book);
    List<Book> loadAll(SortCriteria criteria);
    List<Book> findByTitle(String title, SortCriteria criteria);
    List<Book> findByRating(int rating, SortCriteria criteria);
    List<Book> findByReadingState(String readingState, SortCriteria criteria);
    List<Book> findByAuthor(String author, SortCriteria criteria);
    void delete(Book book);
    void update(Book book);
}
