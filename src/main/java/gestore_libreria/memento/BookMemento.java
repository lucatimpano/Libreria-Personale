package gestore_libreria.memento;

import gestore_libreria.model.Book;

public class BookMemento {

    //definiamo i tipi principali di operazioni che possiamo eseguire
    public enum OperationType {
        ADD, REMOVE, UPDATE;
    }

    private final Book bookState;       // lo stato dopo l'operazione
    private final OperationType operationType;      //il tipo di operazione
    private final Book previousBookState;       // lo stato del libro prima dell'operazione

    // costruttore

    public BookMemento(Book bookState, OperationType operationType) {
        //costruttore per le operazioni di delete e add, NO UPDATE
        if(operationType == OperationType.UPDATE){
            throw new IllegalArgumentException("Update operation not supported");
        }
        this.bookState = bookState;
        this.operationType = operationType;
        this.previousBookState = null;
    }

    public BookMemento(Book bookState, OperationType operationType, Book previousBookState) {
        //costruttore per l'operazione di UPDATE
        if(operationType == OperationType.ADD || operationType == OperationType.REMOVE){
            throw new IllegalArgumentException("Add operation and Remove operation not supported");
        }
        this.bookState = bookState;
        this.previousBookState = previousBookState;
        this.operationType = operationType;
    }

    public Book getBookState() {
        return bookState;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Book getPreviousBookState() {
        return previousBookState;
    }

    @Override
    public String toString() {
        return "BookMemento{" +
                "bookState=" + bookState +
                ", operationType=" + operationType +
                ", previousBookState=" + previousBookState +
                '}';
    }
}
