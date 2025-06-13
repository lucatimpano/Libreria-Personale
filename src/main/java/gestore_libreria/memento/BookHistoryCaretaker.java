package gestore_libreria.memento;

import gestore_libreria.model.Book;

import java.util.Stack;

/**
 * Gestisce la cronologia degli stati di un oggetto {@link Book} attraverso il pattern memento.
 * Permette le operazioni di undo/redo notificando gli observer registrati
 */

public class BookHistoryCaretaker {

    private final Stack<BookMemento> undoStack = new Stack<>();
    private final Stack<BookMemento> redoStack = new Stack<>();

    private OnMementoListener restoreListener;

    /**
     * Indicano le possibili direzioni dell'operazione di ripristino: UNDO e REDO
     */
    public enum ActionDirection{
        UNDO, REDO;
    }

    /**
     * Interfaccia per gestire il restore di un {@link BookMemento}.
     * Deve essere implementata da chi ha la possibilità di modificare lo stato del database: {@link gestore_libreria.db.BookManager}
     * nel nostro caso.
     */
    public interface OnMementoListener {
        /**
         * Viene richiamato quando si desidera ripristinare uno stato precedente.
         *
         * @param memento rappresenta lo stato da ripristinare
         * @param direction direzione dell'operazione UNDO/REDO
         * @pre {@code memento} non deve essere null
         * @pre {@code direction} non deve essere null
         * @post lo stato viene ripristinato secondo la direzione indicata
         */
        void restore(BookMemento memento, ActionDirection direction);
    }

    /**
     * Salva un nuovo stato e svuota lo stack redo
     *
     * @param memento lo stato corrente da salvare
     * @pre {@code memento} non deve essere null
     * @post memento si posziona in cima allo stack undo
     * @post lo stack redo viene svuotato
     */
    public void save(BookMemento memento) {
        undoStack.push(memento);
        redoStack.clear();      // una volta che faccio una nuova operazione ripulisco lo stack redo
    }

    /**
     * Imposta un listner per le notifiche di ripristino
     *
     * @param listener listener da notificare
     * @post il listener è stato aggiornato
     */
    public void setOnMementoRestoreListener(OnMementoListener listener) {
        this.restoreListener = listener;
    }

    /**
     * Esegue l'operazione di undo
     * @return il memento ripristinato o {@code null} se non disponibile
     * @pre lo stack undo può essere vuoto ma restituisce null
     * @post se disponibile il memento viene rimosso dallo stack undo e spostato nello stack redo.
     * @post se presente un listener, viene notificato con {@code ActionDirection.UNDO}
     */
    public BookMemento undo() {
        if (!undoStack.isEmpty()) {
            BookMemento memento = undoStack.pop();
            redoStack.push(memento);        //prendo il memento dalla pila undo e lo sposto nella pila redo
            if (restoreListener != null) {
                restoreListener.restore(memento, ActionDirection.UNDO);
            }
            return memento;
        }
        return null;
    }

    /**
     * Esegue l'operazione di redo
     * @return il memento ripristinato o {@code null} se non disponibile
     * @pre lo stack redo può essere vuoto ma restituisce null
     * @post se disponibile il memento viene rimosso dallo stack redo e spostato nello stack undo.
     * @post se presente un listener, viene notificato con {@code ActionDirection.REDO}
     */
    public BookMemento redo() {
        if (!redoStack.isEmpty()) {
            BookMemento memento = redoStack.pop();
            undoStack.push(memento);
            if (restoreListener != null) {
                restoreListener.restore(memento, ActionDirection.REDO);
            }
            return memento;
        }
        return null;
    }

    //metodi accessori

    /**
     * Verifica se è possibile effettuare un'operazione di undo.
     *
     * @return {@code true} se lo stack undo non è vuoto
     * @post il risultato riflette lo stato dello stack undo
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Verifica se è possibile effettuare un'operazione di redo.
     *
     * @return {@code true} se lo stack redo non è vuoto
     * @post il risultato riflette lo stato dello stack redo
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Pulisce completamente la cronologia di undo e redo.
     *
     * @post entrambi gli stack sono vuoti
     */
    public void cleanAll(){
        undoStack.clear();
        redoStack.clear();
    }


}
