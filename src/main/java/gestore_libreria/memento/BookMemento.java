package gestore_libreria.memento;

import gestore_libreria.model.Book;

/**
 * Rappresenta un memento contenente lo stato di un oggetto {@link Book}
 * e il tipo di operazione eseguita (ADD, REMOVE e UPDATE)
 */

public class BookMemento {

    /**
     * definiamo i tipi principali di operazioni che possiamo eseguire
     */

    public enum OperationType {
        ADD, REMOVE, UPDATE;
    }

    private final Book bookState;       // lo stato dopo l'operazione
    private final OperationType operationType;      //il tipo di operazione
    private final Book previousBookState;       // lo stato del libro prima dell'operazione

    // costruttore

    /**
     * Costruttore per operazioni di tipo ADD o REMOVE.
     *
     * @param bookState stato del libro dopo l'operazione
     * @param operationType tipo di operazione (solo ADD o REMOVE)
     * @pre {@code bookState} non deve essere null
     * @pre {@code operationType == ADD || operationType == REMOVE}
     * @pre {@code operationType != UPDATE}
     * @post il memento rappresenta l'operazione passata con stato precedente nullo
     * @throws IllegalArgumentException se {@code operationType == UPDATE}
     */
    public BookMemento(Book bookState, OperationType operationType) {
        //costruttore per le operazioni di delete e add, NO UPDATE
        if(operationType == OperationType.UPDATE){
            throw new IllegalArgumentException("Update operation not supported");
        }
        this.bookState = bookState;
        this.operationType = operationType;
        this.previousBookState = null;
    }

    /**
     * Costruttore per operazioni di tipo UPDATE.
     *
     * @param bookState stato del libro dopo l'operazione
     * @param operationType deve essere {@code UPDATE}
     * @param previousBookState stato del libro prima dell'update
     * @pre {@code bookState} non deve essere null
     * @pre {@code previousBookState} non deve essere null
     * @pre {@code operationType == UPDATE}
     * @pre {@code operationType != ADD || operationType != REMOVE}
     * @post il memento rappresenta correttamente l'update con stato precedente e successivo
     * @throws IllegalArgumentException se {@code operationType == ADD || operationType == REMOVE}
     */
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
