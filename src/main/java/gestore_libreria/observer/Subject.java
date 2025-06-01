package gestore_libreria.observer;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Subject {
    private List<BookObserver> observers;

    public Subject() {
        observers = new CopyOnWriteArrayList<>();
    }

    /**
     * Ci permette di iscrivere il nostro ConcreteSubject alla lista degli observers per
     * essere notificato
     *
     * @param observer contiene la logica {@code update()} e {@code unsubscribe()}, è l'elemento che verrà inserito nella lista
     * @pre {@code observer} non deve essere null
     * @pre {@code observer} non deve essere contenuto nella lista
     * @post l'observer è stato correttamente inserito nella lista
     */
    public void attach(BookObserver observer) {
        if(!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Ci permette di disiscrivere il nostro ConcreteSubject dalla lista degli observer
     * @param observer ccontiene la logica {@code update()} e {@code unsubscribe()}, è l'elemento che verrà inserito
     * nella lista
     * @pre {@code observer} non deve essere null
     * @pre {@code observer} deve essere contenuto nella lista
     * @post l'observer è stato correttamente rimosso dalla lista degli observer
     * @post l'observer non verrà più notificato
     */
    public void detach(BookObserver observer) {
        if(observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    /**
     * Notifica tutti gli observer iscritti invocando il loro metodo {@code update()}
     * @post tutti gli observer contenuti nella lista sono stati notificati
     */
    public void notifyObservers() {
        for (BookObserver observer : observers) {
            observer.update();
        }
    }
}
