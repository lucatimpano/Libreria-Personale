package gestore_libreria.observer;

public interface BookObserver {
    /**
     * Questo metodo viene chiamato dall'oggetto osservato per notificare gli osservatori
     * che si è verificato un cambiamento nello stato dei dati dei libri.
     *
     * @pre L'oggetto osservabile (es. ConcreteBookManager) ha subito una modifica nel suo stato
     * relativo ai libri e ha chiamato questo metodo.
     * @post L'osservatore ha ricevuto la notifica e può agire di conseguenza (aggiornare la UI in questo caso specifico).
     */
    void update();
}
