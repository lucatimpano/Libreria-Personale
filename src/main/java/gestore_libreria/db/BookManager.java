package gestore_libreria.db;

import gestore_libreria.model.Book;
import gestore_libreria.model.SortCriteria;

import java.util.List;


public interface BookManager {

    void addBook(Book book);

    List<Book> getAllBook(SortCriteria criteria);

    List<Book> findBookByTitle(String title, SortCriteria criteria);

    List<Book> filterBookByRating(int rating, SortCriteria criteria);

    List<Book> filterBookByReadingState(String readingState, SortCriteria criteria);

    List<Book> findBookByAuthor(String author, SortCriteria criteria);

    void updateBook(Book oldBook, Book book);

    void deleteBook(Book book);

}