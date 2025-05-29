package gestore_libreria.db;

import gestore_libreria.memento.BookHistoryManager;
import gestore_libreria.memento.BookMemento;
import gestore_libreria.model.Book;
import gestore_libreria.observer.BookObserver;
import gestore_libreria.observer.Subject;

import java.util.List;

// Questa classe ora implementa l'interfaccia BookManager.
public class ConcreteBookManager extends Subject implements BookManager, BookHistoryManager.OnMementoListener  {

    private final BookRepositoryImplementor repository;
    private final BookHistoryManager historyManager;

    /**
     * Costruisce una nuova istanza di ConcreteBookManager.
     *
     * @param repository L'implementazione del repository per la gestione dei dati dei libri.
     * @pre repository non deve essere null.
     * @post L'oggetto ConcreteBookManager è stato inizializzato.
     * @post Il repository interno è stato impostato con l'istanza fornita.
     * @post Viene creata una nuova istanza di BookHistoryManager.
     * @post L'istanza corrente di ConcreteBookManager è registrata come listener per il ripristino dei memento nella cronologia.
     * @throws IllegalArgumentException se il parametro 'repository' è null.
     */
    public ConcreteBookManager(BookRepositoryImplementor repository) {
        this.repository = repository;
        this.historyManager = new BookHistoryManager();
        this.historyManager.setOnMementoRestoreListener(this);
    }

    /**
     * Restituisce l'istanza del gestore della cronologia (BookHistoryManager) associata a questo manager.
     *
     * @pre Nessuna pre-condizione specifica. Il manager deve essere stato inizializzato (il costruttore deve essere stato chiamato).
     * @post Restituisce un'istanza non null di BookHistoryManager.
     * @return L'istanza di {@code BookHistoryManager}.
     */
    public BookHistoryManager getHistoryManager() {
        return historyManager;
    }

    /**
     * Consente l'inserimento di un libro nel database
     *
     * @param book Il libro da aggiungere.
     * @pre book non deve essere null.
     * @pre book.getTitle() non deve essere null o vuoto.
     * @pre book.getAuthor() non deve essere null o vuoto.
     * @post Il libro è stato correttamente inserito nel database con codice ID univoco.
     * @post Dopo l'inserimento vengono notificati gli Observer.
     * @post Dopo l'inserimento si salva il libro nello stack hystoryManager.
     */
    @Override
    public void addBook(Book book) {
        repository.save(book);
        historyManager.save(new BookMemento(book, BookMemento.OperationType.ADD));
        super.notifyObservers();
    }

    /**
     * @pre il database deve essere in uno stato consistente.
     * @post Restituisce una lista non null di oggetti Book contenuti nel database.
     * @post Restituisce una lista vuota se il database non contiene nessun oggetto Book.
     * @return Una {@code List<Book>} contenente i libri contenuti nel database.
     */
    @Override
    public List<Book> getAllBook() {
        return repository.loadAll();
    }

    /**
     * Permette di trovare i libri che all'interno del titolo contengono la stringa specificata
     *
     * @param title Stringa da cercare nel titolo dei libri.
     * @pre title non deve essere null.
     * @post Restituisce una lista non null di oggetti Book che corrispondono al criterio di ricerca.
     * @post Se nessun libro rispetto il criterio viene restituita una lista vuota.
     * @return Una {@code List<Book>} contenente i libri trovati.
     */
    @Override
    public List<Book> findBookByTitle(String title) {
        return repository.findByTitle(title);
    }

    /**
     * Permette di trovare i libri in base alla valutazione specificata
     *
     * @param rating La valutazione come parametro di ricerca (int)
     * @pre rating deve essere un valore intero compreso da 1 a 5
     * @post restituisce una lista non null di oggetti Book che corrispondono al criterio di ricerca
     * @post Se nessun libro rispetto il criterio viene restituita una lista vuota
     * @return Una {@code List<Book>} contenente i libri trovati
     * @throws IllegalArgumentException se rating non è compreso tra 1 e 5.
     */
    @Override
    public List<Book> filterBookByRating(int rating) {
        return repository.findByRating(rating);
    }

    /**
     *Filtra i libri in base allo stato di lettura specificato.
     *
     * @param readingState lo stato di lettura da filtrare (es. "LETTO", "IN LETTURA", "DA LEGGERE")
     * @pre readingState non deve essere null o vuoto
     * @pre readingState deve corrispondere obbligatoriamente a uno degli stati di lettura predefiniti ("LETTO", "IN LETTURA", "DA LEGGERE")
     * @post restituisce una lista non null di oggetti Book che hanno lo stato di lettura specificato
     * @post Se nessun libro rispetto il criterio viene restituita una lista vuota
     * @return Una {@code List<Book>} contenente i libri con lo stato di lettura specificato.
     * @throws IllegalArgumentException se readingState non è uno stato valido.
     */
    @Override
    public List<Book> filterBookByReadingState(String readingState) {
        return repository.findByReadingState(readingState);
    }

    /**
     * Trova i libri il cui autore contiene la stringa specificata (case-insensitive).
     *
     * @param author La stringa da cercare nel nome dell'autore dei libri.
     * @pre author non deve essere null.
     * @post Restituisce una lista non null di oggetti Book che corrispondono al criterio di ricerca.
     * @post Se nessun libro corrisponde al criterio, la lista restituita è vuota.
     * @return Una {@code List<Book>} contenente i libri trovati.
     */
    @Override
    public List<Book> findBookByAuthor(String author) {
        return repository.findByAuthor(author);
    }

    /**
     * Aggiorna un libro esistente nel database e ne salva lo stato per le operazioni di undo/redo.
     *
     * @param oldBook Lo stato precedente del libro prima dell'aggiornamento. Usato per l'undo.
     * @param book Il libro con i dati aggiornati.
     * @pre oldBook non deve essere null.
     * @pre newBook non deve essere null.
     * @pre newBook.getId() deve corrispondere all'ID di un libro esistente nel database.
     * @pre newBook.getTitle() non deve essere null o vuoto.
     * @pre newBook.getAuthor() non deve essere null o vuoto.
     * @post Lo stato del libro nel database è aggiornato con i dati di {@code newBook}.
     * @post Un memento di tipo UPDATE è salvato nella cronologia, contenente sia {@code newBook} che {@code oldBook}.
     * @post Tutti gli osservatori sono notificati del cambiamento.
     * @throws IllegalArgumentException se {@code oldBook} o {@code newBook} sono null, o se {@code newBook} non ha un ID valido/campi obbligatori.
     *
     */
    @Override
    public void updateBook(Book oldBook, Book book) {
        historyManager.save(new BookMemento(book, BookMemento.OperationType.UPDATE, oldBook));
        repository.update(book);
        super.notifyObservers();
    }

    /**
     * Elimina un libro dal database e ne salva lo stato per le operazioni di undo/redo.
     *
     * @param book Il libro da eliminare.
     * @pre book non deve essere null.
     * @pre book.getId() deve corrispondere all'ID di un libro esistente nel database.
     * @post Il libro è rimosso dal database.
     * @post Un memento di tipo REMOVE è salvato nella cronologia, contenente lo stato del libro prima della rimozione.
     * @post Tutti gli osservatori sono notificati del cambiamento.
     * @throws IllegalArgumentException se book è null o non ha un ID valido.
     */
    @Override
    public void deleteBook(Book book) {
        historyManager.save(new BookMemento(book, BookMemento.OperationType.REMOVE));
        repository.delete(book);
        super.notifyObservers();
    }

/**
 * Ripristina lo stato di un'operazione del libro in base al memento e alla direzione specificata.
 * Questo metodo gestisce le operazioni di Undo e Redo per aggiunte, rimozioni e aggiornamenti.
 *
 * @param memento Il memento che contiene lo stato del libro e il tipo di operazione.
 * @param direction La direzione dell'azione (UNDO o REDO).
 * @pre memento non deve essere null.
 * @pre memento.getBookState() non deve essere null per le operazioni ADD e REMOVE.
 * @pre memento.getPreviousBookState() non deve essere null per l'operazione UPDATE in caso di UNDO.
 * @post Il database viene modificato per riflettere lo stato del libro come specificato dal memento e dalla direzione.
 * @post Se l'operazione è un UNDO di ADD, il libro viene rimosso.
 * @post Se l'operazione è un REDO di ADD, il libro viene riaggiunto.
 * @post Se l'operazione è un UNDO di REMOVE, il libro viene riaggiunto.
 * @post Se l'operazione è un REDO di REMOVE, il libro viene rimosso.
 * @post Se l'operazione è un UNDO di UPDATE, il libro viene ripristinato allo stato precedente.
 * @post Se l'operazione è un REDO di UPDATE, il libro viene ripristinato allo stato successivo.
 * @post Tutti gli osservatori sono notificati del cambiamento dopo il ripristino.
 * @throws IllegalArgumentException se il memento o il suo contenuto non sono validi per l'operazione specificata.
 */
    @Override
    public void restore(BookMemento memento, BookHistoryManager.ActionDirection direction) {
        switch (memento.getOperationType()) {
            case ADD:
                if (direction == BookHistoryManager.ActionDirection.UNDO) {
                    repository.delete(memento.getBookState());
                    System.out.println("Undo ADD: Rimosso libro " + memento.getBookState().getTitle());
                } else {
                    repository.save(memento.getBookState());
                    System.out.println("Redo ADD: Riaggiunto libro " + memento.getBookState().getTitle());
                }
                break;
            case REMOVE:
                if (direction == BookHistoryManager.ActionDirection.UNDO) {
                    repository.save(memento.getBookState());
                    System.out.println("Undo DELETE: Riaggiunto libro " + memento.getBookState().getTitle());
                } else {
                    repository.delete(memento.getBookState());
                    System.out.println("Redo DELETE: Rimosso libro " + memento.getBookState().getTitle());
                }
                break;
            case UPDATE:
                if (direction == BookHistoryManager.ActionDirection.UNDO) {
                    repository.update(memento.getPreviousBookState());
                    System.out.println("Undo UPDATE: Ripristinato libro " + memento.getPreviousBookState().getTitle() + " allo stato precedente.");
                } else {
                    repository.update(memento.getBookState());
                    System.out.println("Redo UPDATE: Ripristinato libro " + memento.getBookState().getTitle() + " allo stato successivo.");
                }
                break;
        }
        super.notifyObservers(); // Notifica la UI dopo il ripristino
    }
}