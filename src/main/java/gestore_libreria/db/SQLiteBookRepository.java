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
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(readingState) LIKE LOWER(?)";
        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + readingState + "%");
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
            System.err.println("Errore nella ricerca del libro dallo stato di lettura");
        }
        return books;
    }

    @Override
    public List<Book> findByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(author) LIKE LOWER(?)";
        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + author + "%");
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
            System.err.println("Errore nella ricerca del libro dall'autore");
        }
        return books;
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
        String sql = """
                UPDATE books SET
                title=?,
                author=?,
                isbn=?,
                genre=?,
                rating=?,
                readingState=?,
                coverPath=?
                WHERE id=?
                """;
        try{
            Connection connection = DatabaseConnectionSingleton.getInstance();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getIsbn());
            preparedStatement.setString(4, book.getGenre());
            preparedStatement.setInt(5, book.getRating());
            preparedStatement.setString(6, book.getReadingState());
            preparedStatement.setString(7, book.getCoverPath());
            preparedStatement.setInt(8, book.getId());      //il libro aggiornato deve avere lo stesso id del libro da modificare

            int affectedRows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore nell'aggiornamento del libro");
            e.printStackTrace();
        }

    }


//    public static void main(String[] args) {
//        SQLiteBookRepository repo = new SQLiteBookRepository();
//
//        // Pulizia iniziale del database
//        try (Connection conn = DatabaseConnectionSingleton.getInstance();
//             Statement stmt = conn.createStatement()) {
//            stmt.execute("DELETE FROM books");
//            System.out.println("DB pulito.");
//        } catch (SQLException e) {
//            System.err.println("Errore nella pulizia: " + e.getMessage());
//        }
//
//        // Inserisco un libro
//        Book libro = new Book.Builder("Il Signore degli Anelli", "J.R.R. Tolkien")
//                .isbn("978-0544003415")
//                .genre("Fantasy")
//                .rating(5)
//                .readingState("letto")
//                .build();
//
//        repo.save(libro);
//        System.out.println("Libro inserito: " + libro);
//
//        // Modifico lo stesso libro
//        Book modificato = new Book.Builder("Il Signore degli Anelli - Edizione Estesa", libro.getAuthor())
//                .id(libro.getId())
//                .isbn("978-0544003415")
//                .genre("Fantasy Epico")
//                .rating(5)
//                .readingState("letto")
//                .build();
//
//        repo.update(modificato);
//        System.out.println("Libro aggiornato.");
//
//        // Carico tutti i libri e li stampo
//        List<Book> libri = repo.loadAll();
//        libri.forEach(System.out::println);
//    }


}
