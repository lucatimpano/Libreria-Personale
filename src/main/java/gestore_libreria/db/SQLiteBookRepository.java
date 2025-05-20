package gestore_libreria.db;

import gestore_libreria.model.Book;

import java.sql.*;
import java.util.List;

//classe che implementa il database
public class SQLiteBookRepository implements BookRepositoryImplementor {

    public SQLiteBookRepository(){
        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            Statement statement = connection.createStatement();
            String createTable = """
                    CREATE TABLE IF NOT EXISTS books(
                        title TEXT NOT NULL,
                        authot TEXT NOT NULL,
                        isbn TEXT,
                        genre TEXT,
                        rating INTEGER,
                        readingState TEXT,
                        coverPath TEXT
                        );
                    """;
            statement.execute(createTable);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    @Override
    public void save(Book book) {
        //Per inserire il libro preparo la stringa sql con gli elementi da aggiungere seguiti da ? per ogni parametro
        String sql = """
                INSERT INTO books (title, author, genre, isbn, rating, readingState, coverPath)
                VALUES (?,?,?,?,?,?,?)
                """;
        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2,book.getAuthor());
            preparedStatement.setString(3,book.getIsbn());
            preparedStatement.setString(4,book.getGenre());
            preparedStatement.setInt(5,book.getRating());
            preparedStatement.setString(6, book.getReadingState());
            preparedStatement.setString(7,book.getCoverPath());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
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



    public static void main(String[] args) {
        SQLiteBookRepository repo = new SQLiteBookRepository();
    }

}
