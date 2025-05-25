package gestore_libreria.observer;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Subject {
    private List<BookObserver> observers;

    public Subject() {
        observers = new CopyOnWriteArrayList<>();
    }

    public void attach(BookObserver observer) {
        if(!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void detach(BookObserver observer) {
        if(observers.contains(observer)) {
            observers.remove(observer);
        }

    }

    public void notifyObservers() {
        for (BookObserver observer : observers) {
            observer.update();
        }
    }

}
