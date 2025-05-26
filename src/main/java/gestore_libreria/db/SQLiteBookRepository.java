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
            //aggiungo un id come chiave del libro
            String createTable = """
                    CREATE TABLE IF NOT EXISTS books(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
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
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2,book.getAuthor());
            preparedStatement.setString(3,book.getIsbn());
            preparedStatement.setString(4,book.getGenre());
            preparedStatement.setInt(5,book.getRating());
            preparedStatement.setString(6, book.getReadingState());
            preparedStatement.setString(7,book.getCoverPath());
            preparedStatement.executeUpdate();

            //ricavo il codice del libro e lo inserisco nell'oggetto
            try{
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if(resultSet.next()){
                    book.setId(resultSet.getInt(1));
                }
            } catch (SQLException e) {
                System.err.println("Errore nel salvataggio del libro");
                e.printStackTrace();
            }

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
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?)";
        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + title + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Book book = new Book.Builder(resultSet.getString("title"), resultSet.getString("author"))
                        .id(resultSet.getInt("id"))
                        .isbn(resultSet.getString("isbn"))
                        .genre(resultSet.getString("genre"))
                        .rating(resultSet.getInt("rating"))
                        .readingState(resultSet.getString("readingState"))
                        .coverPath(resultSet.getString("coverPath"))
                        .build();
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Errore nella ricerca del libro dal titolo");
        }
        return books;
    }

    @Override
    public List<Book> findByRating(int rating) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE rating = ?";
        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,rating);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Book book = new Book.Builder(resultSet.getString("title"), resultSet.getString("author"))
                        .id(resultSet.getInt("id"))
                        .isbn(resultSet.getString("isbn"))
                        .genre(resultSet.getString("genre"))
                        .rating(resultSet.getInt("rating"))
                        .readingState(resultSet.getString("readingState"))
                        .coverPath(resultSet.getString("coverPath"))
                        .build();
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Errore nella ricerca del libro dalla valutazione");
        }
        return books;
    }

    @Override
    public List<Book> findByReadingState(String readingState) {
        return List.of();
    }

    @Override
    public void delete(Book book) {
        int id = book.getId();
        String sql = "Delete FROM books WHERE id = ?";
        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            int deleteRow = preparedStatement.executeUpdate();
            if(deleteRow>0){
                System.out.println("Riga eliminata con successo");
            }else {
                System.out.println("Riga NON eliminata con successo");
            }

        } catch (SQLException e) {
            System.err.println("Errore nell'aggiornamento del libro");
            e.printStackTrace();
        }
    }

    @Override
    public void update(Book book) {

    }


//    public static void main(String[] args) throws InterruptedException {
//        SQLiteBookRepository repo = new SQLiteBookRepository();
//        for(int i = 0;i < 10;i++){
//            Book book = new Book.Builder("Prova" + i, "Autore" + i).isbn("12ab32sda1" + i).rating(3).build();
//            repo.save(book);
//        }
//        Thread.sleep(10000);
//        System.out.println(repo.findByTitle("Prova2"));
//        System.out.println(repo.findByRating(3));
//
//    }




}
