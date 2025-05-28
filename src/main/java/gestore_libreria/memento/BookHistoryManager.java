package gestore_libreria.memento;

import gestore_libreria.model.Book;

import java.util.Stack;

public class BookHistoryManager {

    private final Stack<BookMemento> undoStack = new Stack<>();
    private final Stack<BookMemento> redoStack = new Stack<>();

    private OnMementoListener restoreListener;

    //definisco le operazioni

    public enum ActionDirection{
        UNDO, REDO;
    }

    public interface OnMementoListener {
        //interfaccia che verrà implementata dal BookManager per "obbligare" l'implementazione dell'operazione restore nel db
        void restore(BookMemento memento, ActionDirection direction);
    }

    // metodo per salvare lo stato attuale
    public void save(BookMemento memento) {
        undoStack.push(memento);
        redoStack.clear();      // una volta che faccio una nuova operazione ripulisco lo stack redo
    }

    public void setOnMementoRestoreListener(OnMementoListener listener) {
        this.restoreListener = listener;
    }

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

    //la redo sara il complementare di redo
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
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void cleanAll(){
        undoStack.clear();
        redoStack.clear();
    }


}
