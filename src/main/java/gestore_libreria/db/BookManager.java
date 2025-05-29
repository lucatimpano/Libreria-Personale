package gestore_libreria.db;

import gestore_libreria.model.Book;

import java.util.List;

// Questa è ora un'INTERFACCIA
public interface BookManager {

    void addBook(Book book);

    List<Book> getAllBook();

    List<Book> findBookByTitle(String title);

    List<Book> filterBookByRating(int rating);

    List<Book> filterBookByReadingState(String readingState);

    List<Book> findBookByAuthor(String author);

    void updateBook(Book oldBook, Book book);

    void deleteBook(Book book);

}