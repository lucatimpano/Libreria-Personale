package gestore_libreria.db;

import gestore_libreria.model.Book;

import java.util.List;
//classe che implementa il database
public class SQLiteBookRepository implements BookRepositoryImplementor {
    @Override
    public void save(Book book) {

    }

    @Override
    public List<Book> loadAll() {
        return List.of();
    }

    @Override
    public List<Book> findByTitle(String title) {
        return List.of();
    }

    @Override
    public List<Book> findByRating(int rating) {
        return List.of();
    }

    @Override
    public List<Book> findByReadingState(String readingState) {
        return List.of();
    }

}
