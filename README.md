# Gestore Libreria


[![Java](https://img.shields.io/badge/Java-11%2B-blue.svg?logo=openjdk)](https://www.java.com/)
[![FlatLaf](https://img.shields.io/badge/UI-FlatLaf-orange.svg?logo=java)](https://www.formdev.com/flatlaf/)
[![SQLite](https://img.shields.io/badge/Database-SQLite-green.svg?logo=sqlite)](https://www.sqlite.org/index.html)

Gestore libreria è un'applicazione desktop per la gestione della propria libreria personale, sviluppata in Java Swing. Questo progetto è stato realizzato per esplorare e dimostrare l'implementazione pratica di diversi design pattern.


<img src="https://github.com/user-attachments/assets/36f670ff-41b7-4519-aa5e-d1714385fb76" alt="Image" width="450"/>
## Funzionalità Principali

* **Gestione Completa dei Libri:** Operazioni CRUD (Creazione, Lettura, Aggiornamento, Eliminazione) per la gestione dei libri.
* **Ricerca e Filtraggio Avanzati:** Capacità di ricercare e filtrare i libri per titolo, autore, stato di lettura e rating.
* **Ordinamento Dinamico:** Ordinamento dei libri basato su criteri personalizzabili.
* **Aggiornamenti Real-time:** Aggiornamento automatico dell'interfaccia utente in risposta alle modifiche del database.
* **Funzionalità Undo/Redo:** Supporto per l'annullamento e il ripristino delle operazioni sui libri.
* **Persistenza Dati:** I dati dei libri sono persistiti tramite un database SQLite.

## Design Pattern Implementati

Il progetto sfrutta i seguenti design pattern per ottimizzare l'architettura e la flessibilità del codice:

* **Bridge Pattern:** Utilizzato per disaccoppiare l'astrazione della gestione dei libri dall'implementazione concreta della persistenza dati (es. database).
* **Singleton Pattern:** Adottato per garantire l'esistenza di un'unica istanza della connessione al database, ottimizzando le risorse.
* **Observer Pattern:** Impiegato per implementare un meccanismo di notifica che permette agli elementi dell'interfaccia utente di reagire automaticamente ai cambiamenti nello stato dei dati.
* **Builder Pattern:** Utilizzato per la costruzione passo-passo di oggetti `Book` complessi.
* **Memento Pattern:** Implementato per abilitare le funzionalità di undo/redo, permettendo il salvataggio e il ripristino dello stato degli oggetti `Book`.

## Struttura del Progetto

Il codice è organizzato in pacchetti per favorire la modularità e la separazione delle responsabilità:

* `gestore_libreria.db`: Contiene le interfacce e le implementazioni per la persistenza dei dati.
* `gestore_libreria.memento`: Contiene le classi relative all'implementazione del pattern Memento.
* `gestore_libreria.model`: Contiene le classi del modello di dominio dell'applicazione.
* `gestore_libreria.observer`: Contiene le interfacce e le classi per il pattern Observer.
* `gestore_libreria.ui`: Contiene le classi che definiscono l'interfaccia utente grafica.

## Guida all'Avvio del Progetto

### Requisiti

- Java Development Kit (JDK) 11 o superiore
- Maven (solo se si vuole ricompilare il progetto)

## Avvio del Progetto

### Opzione 1 — Esegui il file `.jar` della release (consigliato)

1. Scarica l'ultima release da qui:  
   [Pagina delle release](https://github.com/lucatimpano/Libreria-Personale/releases/tag/1.0.0)

2. Naviga nella cartella dove si è scaricata la release e avvia l'applicazione con:

```bash
java -jar Gestore_Libreria-1.0-SNAPSHOT.jar
```

### Opzione 2 — Compila manualmente il progetto
1. Clona il repository
```bash
git clone https://github.com/lucatimpano/Libreria-Personale.git
cd Gestore_Libreria
```
2. Compila il progetto
```bash
mvn clean package
```

esegui il `.jar` generato:
```bash
java -jar target/Gestore_Libreria-1.0-SNAPSHOT.jar
```


### Dipendenze Esterne

Il progetto utilizza le seguenti librerie:
* **FlatLaf**: UI.
* **SQLite JDBC**: Database.



## Contribuzione e sviluppi futuri

Il progetto non verrà mantenuto in futuro, ma sentiti libero di scaricare e modificare il codice.

## Licenza
[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)


## Authors

- [@lucatimpano](https://github.com/lucatimpano)


## Screenshots

![Image](https://github.com/user-attachments/assets/36f670ff-41b7-4519-aa5e-d1714385fb76)
![Image](https://github.com/user-attachments/assets/d232a937-edcd-4575-a2c6-7f24b33d723d)
![Image](https://github.com/user-attachments/assets/f78677b6-4eda-4cbe-bbf9-09f8b30fc634)
![Image](https://github.com/user-attachments/assets/2a38194e-c006-4b5b-8f71-78f7497a8344)
![Image](https://github.com/user-attachments/assets/d1028149-508c-48a8-bef9-7761e559fee0)
![Image](https://github.com/user-attachments/assets/6e3449cf-ca60-4d23-ad72-fce3813a73db)
![Image](https://github.com/user-attachments/assets/d135d445-d317-4629-8e7e-c95ada84ee4f)
![Image](https://github.com/user-attachments/assets/41a9a030-6f6d-4a87-a323-cec35faa0007)
![Image](https://github.com/user-attachments/assets/1987f584-fe25-45c7-8868-bbd21c7dff06)
![Image](https://github.com/user-attachments/assets/10841303-354b-46bd-8cc7-f31a883c230b)
![Image](https://github.com/user-attachments/assets/bdb35db4-e047-46aa-9a79-04a4b13de858)
![Image](https://github.com/user-attachments/assets/ee426557-728e-4cc9-868f-15c4d4f6edc5)
![Image](https://github.com/user-attachments/assets/4db73b07-febe-4409-ac2a-05b3473c05cb)
![Image](https://github.com/user-attachments/assets/19e5e6d3-1893-4031-9352-88a165f66018)
![Image](https://github.com/user-attachments/assets/971d6cd6-ae86-432f-89ba-4233778cfbd3)
![Image](https://github.com/user-attachments/assets/43bb74ad-0ca1-46f8-a551-7244743441c1)
![Image](https://github.com/user-attachments/assets/009e4691-6767-4b0e-abda-8b882e2d9ff2)
