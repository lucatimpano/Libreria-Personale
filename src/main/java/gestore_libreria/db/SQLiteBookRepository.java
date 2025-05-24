package gestore_libreria.db;

import gestore_libreria.model.Book;

import java.sql.*;
import java.util.ArrayList;
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
                        author TEXT NOT NULL,
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
                INSERT INTO books (title, author, isbn, genre, rating, readingState, coverPath)
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
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Book> loadAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()){
                Book book = new Book.Builder(resultSet.getString("title"), resultSet.getString("author"))
                        .isbn(resultSet.getString("isbn"))
                        .genre(resultSet.getString("genre"))
                        .rating(resultSet.getInt("rating"))
                        .readingState(resultSet.getString("readingState"))
                        .coverPath(resultSet.getString("coverPath"))
                        .build();
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return books;
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



//    public static void main(String[] args) {
//        SQLiteBookRepository repo = new SQLiteBookRepository();
//        Book book = new Book.Builder("Prova", "Libro").isbn("12ab32sda1").build();
//        repo.save(book);
//    }

}
