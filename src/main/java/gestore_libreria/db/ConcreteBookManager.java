package gestore_libreria.db;

import gestore_libreria.model.Book;

public class ConcreteBookManager extends BookManager{

    //classe concreta per utilizzare il bookManager

    public ConcreteBookManager(BookRepositoryImplementor repository) {
        super(repository);
    }

    public static void main(String[] args) {
        BookRepositoryImplementor repo = new SQLiteBookRepository();
        Book book = new Book.Builder("Prova2", "Libro2").isbn("15b32sda1").build();
        BookManager db = new ConcreteBookManager(repo);
        db.addBook(book);
    }
}
