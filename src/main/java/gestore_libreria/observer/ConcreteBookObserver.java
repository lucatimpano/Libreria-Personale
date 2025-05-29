package gestore_libreria.observer;

import gestore_libreria.db.BookManager;
import gestore_libreria.db.BookRepositoryImplementor;
import gestore_libreria.db.ConcreteBookManager;
import gestore_libreria.db.SQLiteBookRepository;
import gestore_libreria.model.Book;
import gestore_libreria.ui.BooksPanelUI;
import gestore_libreria.ui.GestoreLibreriaUI;

import java.util.List;

/**
 * Implementazione concreta dell'interfaccia BookObserver.
 * Questo osservatore è responsabile dell'aggiornamento dell'interfaccia utente (UI)
 * quando vengono apportate modifiche allo stato del database.
 */
public class ConcreteBookObserver implements BookObserver{

    //qui ho un'istanza del ConcreteBookManager
    private BooksPanelUI bookPanel;
    private GestoreLibreriaUI gestoreLibreriaUI;
    private ConcreteBookManager db;

    /**
     * Costruisce una nuova istanza di ConcreteBookObserver.
     *
     * @param gestoreLibreriaUI l'interfaccia utente principale dove vengono visualizzati i criteri di ricerca e il {@code bookPanel}
     * @param bookPanel Il pannello dell'interfaccia utente che questo osservatore deve aggiornare.
     * @param db Istanza del database utilizzato
     * @pre gestoreLibreriaUI non deve essere null
     * @pre booksPanel non deve essere null.
     * @post L'osservatore è inizializzato con un riferimento al pannello UI.
     * @throws IllegalArgumentException se booksPanelUI è null.
     */
    public ConcreteBookObserver(GestoreLibreriaUI gestoreLibreriaUI, BooksPanelUI bookPanel, ConcreteBookManager db) {
        this.bookPanel = bookPanel;
        this.db = db;
        this.gestoreLibreriaUI = gestoreLibreriaUI;
        this.db.attach(this);
        update();
    }

    /**
     * Aggiorna la vista dell'interfaccia utente recuperando tutti i libri dal database
     * e visualizzandoli, quindi aggiorna lo stato dei menu Undo/Redo.
     * Questo metodo è tipicamente invocato quando l'oggetto osservabile (es. il manager dei libri)
     * notifica un cambiamento.
     *
     * @pre Il manager dei libri (db) e il pannello dei libri (bookPanel) devono essere stati inizializzati correttamente.
     * @post La lista completa dei libri è stata recuperata dal database.
     * @post Il metodo displayBooks del bookPanel è stato invocato con la lista aggiornata dei libri,
     * causando il refresh dell'interfaccia utente.
     * @post Lo stato dei menu Undo/Redo è stato aggiornato per riflettere la disponibilità di operazioni.
     */
    @Override
    public void update() {
        System.out.println("Aggiorno la bookView");
        List<Book> books = db.getAllBook();
        bookPanel.displayBooks(books);
        gestoreLibreriaUI.updateUndoRedoMenuState();
    }

    /**
     * Rimuove questo osservatore dalla lista degli osservatori dell'oggetto osservabile (db).
     *
     * @pre L'oggetto osservabile (db) non deve essere null.
     * @pre Questo osservatore deve essere precedentemente stato "sottoscritto" (attached) al db.
     * @post Se db non è null, questo osservatore è stato rimosso dalla sua lista di osservatori.
     * @post L'osservatore non riceverà più notifiche da db.
     */
    public void unsubscribe() {
        if(this.db != null) {
            this.db.detach(this);
            System.out.println("Unsubscribed");
        }
    }
}
