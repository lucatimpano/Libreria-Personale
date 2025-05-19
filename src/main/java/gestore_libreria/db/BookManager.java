package gestore_libreria.db;

import gestore_libreria.model.Book;

import java.util.List;

public abstract class BookManager {

    //classe che definisce l'astrazione del database, il client interagirà con la seguente classe

    private final BookRepositoryImplementor repository;

    public BookManager(BookRepositoryImplementor repository){
        this.repository = repository;
    }

    public void addBook(Book book){
        repository.save(book);
    }

    public void getAllBook(){
        repository.loadAll();
    }

    public List<Book> findBookByTitle(String title){
        return repository.findByTitle(title);
    }

    public List<Book> filterBookByRating(int rating){
        return repository.findByRating(rating);
    }

    public List<Book> filterBookByReadingState(String readingState){
        return repository.findByReadingState(readingState);
    }


}
