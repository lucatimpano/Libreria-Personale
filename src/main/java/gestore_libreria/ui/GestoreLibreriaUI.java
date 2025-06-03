package gestore_libreria.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.formdev.flatlaf.themes.*;
import gestore_libreria.db.*;
import gestore_libreria.model.Book;
import gestore_libreria.model.SortCriteria;
import gestore_libreria.observer.ConcreteBookObserver;

/**
 * Classe principale dell'interfaccia grafica per la gestione della libreria.
 * Estende {@link JFrame}.
 * Integra e concretizza le operazioni possibili con la libreria, visualizzazione, modifica, aggiunta, rimozione e gestione
 * dello stato dei libri.
 * Include anche undo/redo e import/export del database
 */

public class GestoreLibreriaUI extends JFrame{

    private ConcreteBookManager db;
    private ConcreteBookObserver bookObserver;
    private BooksPanelUI booksPanelUI;

    private JMenuItem undo;
    private JMenuItem redo;

    private SortCriteria currentSortCriteria = SortCriteria.NONE; // Default

    /**
     * Costruttore
     *
     * @param db Istanza di {@link ConcreteBookManager} per la gestione dei dati dei libri
     * @pre {@code db} deve essere instanziato correttamente
     * @post l'interfaccia utente viene visualizzata
     */
    public GestoreLibreriaUI(ConcreteBookManager db){
        super("Gestore Libreria");
        this.db = db;
        inizializzaUI();
    }

    /**
     * Inzializza l'interfaccia utente, configurando layout, meno pannelli e listener
     */
    private void inizializzaUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        setJMenuBar(creaMenuBar());

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        //sezione sx
        JPanel leftPanel = inizializzaSezioneSX();

        JPanel rightPanel = inizializzaSezioneDX();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        this.booksPanelUI = new BooksPanelUI(this);
        rightPanel.add(booksPanelUI, BorderLayout.CENTER);

        booksPanelUI.setOnBookClickListener(this::showBookDetails);
        booksPanelUI.setOnDeleteBookListener(this::PopupMenuAction);

        this.bookObserver = new ConcreteBookObserver(this,this.booksPanelUI,this.db);

        updateUndoRedoMenuState();

        setVisible(true);

        // Discrivo l'observer nel caso in cui decidiamo di chiudere la finestra
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (bookObserver != null) {
                    bookObserver.unsubscribe();
                }
            }
        });

    }

    /**
     * Mostra i dettagli di un libro selezionato, permette la modifica e l'eliminazione di un oggetto {@link Book}
     * @pre {@code book} non deve essere null.
     * @post Se modificato, il libro viene aggiornato nel database e l'interfaccia viene aggiornata.
     * @post Se eliminato, il libro viene rimosso dal database e l'interfaccia viene aggiornata.
     * @param book
     */
    private void showBookDetails(Book book) {
        JTextField titoloField = new JTextField(book.getTitle(), 20);
        JTextField autoreField = new JTextField(book.getAuthor(), 20);
        JTextField isbnField = new JTextField(book.getIsbn() != null ? book.getIsbn() : "", 20);
        JTextField genreField = new JTextField(book.getGenre() != null ? book.getGenre() : "", 20);
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(book.getRating(), 1, 5, 1));
        String[] statiLettura = {"DA LEGGERE", "IN LETTURA", "LETTO"};
        JComboBox<String> statoCombo = new JComboBox<>(statiLettura);
        statoCombo.setSelectedItem(book.getReadingState() != null ? book.getReadingState() : "DA LEGGERE");

        JTextField imagePathField = new JTextField(book.getCoverPath() != null ? book.getCoverPath() : "", 15);
        imagePathField.setEditable(false);
        JButton browseBtn = new JButton("Sfoglia");
        browseBtn.setEnabled(false);

        JLabel imagePreview = new JLabel();
        int width = 120;
        int height = 180;
        imagePreview.setPreferredSize(new Dimension(width, height));

        ImageIcon selectedIcon = loadAndScaleImage(book.getCoverPath(), width, height);
        if (selectedIcon != null) {
            imagePreview.setIcon(selectedIcon);
        } else {
            ImageIcon placeholder = loadPlaceholderImage(width, height);
            if (placeholder != null) {
                imagePreview.setIcon(placeholder);
            }else {
                imagePreview.setText("Immagine non disponibile");
            }
        }

        JPanel imageWrapper = new JPanel();
        imageWrapper.setLayout(new BorderLayout());
        imageWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        imageWrapper.add(imagePreview, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Titolo:"));
        panel.add(titoloField);
        panel.add(new JLabel("Autore:"));
        panel.add(autoreField);
        panel.add(new JLabel("Genere:"));
        panel.add(genreField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Rating (1-5):"));
        panel.add(ratingSpinner);
        panel.add(new JLabel("Stato lettura:"));
        panel.add(statoCombo);
        panel.add(new JLabel("Copertina:"));
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(imagePathField);
        filePanel.add(browseBtn);
        panel.add(filePanel);

        JPanel BookPanel = new JPanel(new BorderLayout());
        BookPanel.add(panel, BorderLayout.CENTER);
        BookPanel.add(imageWrapper, BorderLayout.NORTH);
        imageWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Disabilita tutti i campi per la sola visualizzazione
        setFieldsEditable(false, titoloField, autoreField, isbnField, genreField, ratingSpinner, statoCombo, browseBtn, imagePathField);

        String[] options = {"Modifica", "<html><font color='red'>Elimina</font></html>", "Chiudi"};
        int choice = JOptionPane.showOptionDialog(this, BookPanel, "Dettagli Libro",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[2]);
        if (choice == 0) {
            setFieldsEditable(true, titoloField, autoreField, isbnField, genreField, ratingSpinner, statoCombo, browseBtn, imagePathField);
            browseButtonAction(browseBtn, imagePathField, imagePreview, width, height);

            int result = JOptionPane.showConfirmDialog(this, BookPanel, "Modifica Libro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String newTitolo = titoloField.getText().trim();
                String newAutore = autoreField.getText().trim();
                String newIsbn = isbnField.getText().trim();
                String newGenre = genreField.getText().trim();
                int newRating = (Integer) ratingSpinner.getValue();
                String newStato = (String) statoCombo.getSelectedItem();
                String newPath = imagePathField.getText().trim();

                if (!newTitolo.isEmpty() && !newAutore.isEmpty()) {
                    Book updatedBook = new Book.Builder(newTitolo, newAutore)
                            .id(book.getId())
                            .isbn(newIsbn)
                            .rating(newRating)
                            .readingState(newStato)
                            .coverPath(newPath)
                            .genre(newGenre)
                            .build();

                    db.updateBook(book, updatedBook); // Passo sia il vecchio che il nuovo libro per l'undo/redo
                    JOptionPane.showMessageDialog(this, "Libro modificato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Titolo e autore sono obbligatori.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
            setFieldsEditable(false, titoloField, autoreField, isbnField, genreField, ratingSpinner, statoCombo, browseBtn, imagePathField);
        } else if (choice == 1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Sei sicuro di voler eliminare il libro '" + book.getTitle() + "'?",
                    "Conferma Eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                db.deleteBook(book);
                JOptionPane.showMessageDialog(this, "Libro eliminato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Utilizzato in {@code showBookDetails()} imposta la modificabilità dei campi dei dettagli del libro.
     *
     * @param editable True per rendere i campi modificabili, false per renderli non modificabili.
     * @param titoloField Campo del titolo.
     * @param autoreField Campo dell'autore.
     * @param isbnField Campo dell'ISBN.
     * @param genreField Campo del genere.
     * @param ratingSpinner Spinner del rating.
     * @param statoCombo ComboBox dello stato di lettura.
     * @param browseBtn Pulsante per la selezione dell'immagine.
     * @param imagePathField Campo del percorso dell'immagine.
     * @pre Tutti i parametri devono essere inizializzati.
     * @post I campi sono impostati come modificabili o non modificabili in base al parametro {@code editable}.
     */
    private void setFieldsEditable(boolean editable, JTextField titoloField, JTextField autoreField, JTextField isbnField, JTextField genreField, JSpinner ratingSpinner,
                                   JComboBox<String> statoCombo, JButton browseBtn, JTextField imagePathField) {
        titoloField.setEditable(editable);
        autoreField.setEditable(editable);
        isbnField.setEditable(editable);
        genreField.setEditable(editable);
        ratingSpinner.setEnabled(editable);
        statoCombo.setEnabled(editable);
        browseBtn.setEnabled(editable);
        browseBtn.setVisible(editable);
        if(!editable){
            imagePathField.setColumns(20);
        }else{
            imagePathField.setColumns(15);
        }
    }

    /**
     * Gestisce l'azione di eliminazione di un libro tramite un menu contestuale.
     *
     * @param book Il libro da eliminare.
     * @pre {@code book} non deve essere null.
     * @post Se confermato, il libro viene rimosso dal database e l'interfaccia viene aggiornata.
     */
    private void PopupMenuAction(Book book) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Sei sicuro di voler eliminare il libro '" + book.getTitle() + "'?",
                "Conferma Eliminazione",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteBook(book);
            JOptionPane.showMessageDialog(this,
                    "Libro eliminato con successo!",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Crea la barra dei menu dell'applicazione, includendo le voci per file e modifica.
     *
     * @return La barra dei menu configurata.
     * @post La barra dei menu contiene le voci per esportare/importare il database, uscire, e le operazioni di undo/redo.
     */
    private JMenuBar creaMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");

        //sezione file

        JMenuItem exportDB = new JMenuItem("Esporta Database");
        exportDB.addActionListener(e -> esportaDatabase());

        JMenuItem importDB = new JMenuItem("Importa Database");
        importDB.addActionListener(e -> importaDatabase());

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            DatabaseConnectionSingleton.closeConnection();
            System.exit(0);

        });

        //sezione edit
        undo = new JMenuItem("⮨Undo");
        undo.addActionListener(e -> {
            db.getHistoryManager().undo();
        });

        redo = new JMenuItem("➥Redo");
        redo.addActionListener(e -> {
            db.getHistoryManager().redo();
        });

        //sezione view
        JMenuItem sortByTitleAsc = new JMenuItem("Ordina per Titolo (A-Z)");
        sortByTitleAsc.addActionListener(e -> {
            this.currentSortCriteria = SortCriteria.TITLE_ASC;
            refreshBookListView();
        });
        viewMenu.add(sortByTitleAsc);

        JMenuItem sortByTitleDesc = new JMenuItem("Ordina per Titolo (Z-A)");
        sortByTitleDesc.addActionListener(e -> {
            this.currentSortCriteria = SortCriteria.TITLE_DESC;
            refreshBookListView();
        });
        viewMenu.add(sortByTitleDesc);

        JMenuItem sortByAuthorAsc = new JMenuItem("Ordina per Autore (A-Z)");
        sortByAuthorAsc.addActionListener(e -> {
            this.currentSortCriteria = SortCriteria.AUTHOR_ASC;
            refreshBookListView();
        });
        viewMenu.add(sortByAuthorAsc);

        JMenuItem sortByAuthorDesc = new JMenuItem("Ordina per Autore (Z-A)");
        sortByAuthorAsc.addActionListener(e -> {
            this.currentSortCriteria = SortCriteria.AUTHOR_DESC;
            refreshBookListView();
        });
        viewMenu.add(sortByAuthorDesc);

        JMenuItem sortByRatingAsc = new JMenuItem("⮬Ordina per Rating (Crescente)");
        sortByRatingAsc.addActionListener(e -> {
            this.currentSortCriteria = SortCriteria.RATING_ASC;
            refreshBookListView();
        });
        viewMenu.add(sortByRatingAsc);

        JMenuItem sortByRatingDesc = new JMenuItem("⮮Ordina per Rating (Decrescente)");
        sortByRatingDesc.addActionListener(e -> {
            this.currentSortCriteria = SortCriteria.RATING_DESC;
            refreshBookListView();
        });
        viewMenu.add(sortByRatingDesc);



        fileMenu.add(exportDB);
        fileMenu.add(importDB);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        editMenu.add(undo);
        editMenu.add(redo);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        return menuBar;
    }

    public SortCriteria getCurrentSortCriteria() {
        return currentSortCriteria;
    }

    private void refreshBookListView(){
        if (db != null){
            db.notifyObservers();
        }
    }

    /**
     * Aggiorna lo stato abilitato/disabilitato delle voci di menu undo e redo in base alla possibilità di eseguire tali operazioni.
     *
     * @post Le voci di menu undo e redo sono abilitate solo se le rispettive operazioni sono disponibili.
     */
    public void updateUndoRedoMenuState() {
        if (db != null && db.getHistoryManager() != null) {
            undo.setEnabled(db.getHistoryManager().canUndo());
            redo.setEnabled(db.getHistoryManager().canRedo());
        }
    }

    private void esportaDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Esporta Database");
        fileChooser.setSelectedFile(new File("books_backup.db"));

        int userSelection = fileChooser.showOpenDialog(this);

        if(userSelection == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            try{
                DatabaseConnectionSingleton.closeConnection();
                Files.copy(new File("Books_db.db").toPath(), selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "Database esportato con successo.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Errore nell'esportazione del database.");
            }finally {
                try {
                    DatabaseConnectionSingleton.getInstance();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                booksPanelUI.displayBooks(db.getAllBook(this.currentSortCriteria));
            }
        }
    }

    private void importaDatabase(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importa Database");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQLite Database Files (*.db)", "db"));

        int userSelection = fileChooser.showOpenDialog(this);

        if(userSelection == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();

            int confirm = JOptionPane.showConfirmDialog(this, "L'importazione sovrascriverà il database esistente, sei sicuro di voler continuare?","Conferma Importazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if(confirm == JOptionPane.YES_OPTION){
                try{
                    DatabaseConnectionSingleton.closeConnection();
                    Files.copy(selectedFile.toPath(), new File("Books_db.db").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "Database importato con successo.");

                    DatabaseConnectionSingleton.getInstance();

                    db = new ConcreteBookManager(new SQLiteBookRepository());
                    bookObserver.unsubscribe();
                    bookObserver = new ConcreteBookObserver(this,this.booksPanelUI,this.db);

                    booksPanelUI.displayBooks(db.getAllBook(this.currentSortCriteria));
                }catch (IOException | SQLException e){
                    JOptionPane.showMessageDialog(this, "Errore durante l'importazione del database: " + e.getMessage(), "Errore Importazione", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    private JPanel inizializzaSezioneDX(){
        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel searchBarPanel = new JPanel(new BorderLayout());
        // Barra di ricerca con FlatLaf arrotondata
        JTextField searchField = new JTextField("Search");
        searchField.putClientProperty("JTextField.roundRect", true);
        searchField.setBackground(new Color(53, 53, 53));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setPreferredSize(new Dimension(120, 30));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        String[] searchCriteria = {"Titolo", "Autore"};
        JComboBox<String> searchCriteriaCombo = new JComboBox<>(searchCriteria);
        searchCriteriaCombo.setPreferredSize(new Dimension(100, 30));
        searchCriteriaCombo.putClientProperty("JComboBox.is=roundReact",true);
        searchCriteriaCombo.setBackground(new Color(53, 53, 53));
        searchCriteriaCombo.setForeground(Color.WHITE);

        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchCriteriaCombo, BorderLayout.EAST);

        rightPanel.add(searchBarPanel, BorderLayout.NORTH);

        // Listener per la ricerca
        searchField.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            String criterion = searchCriteriaCombo.getSelectedItem().toString();
            if(criterion.equals("Titolo")){
                if (!searchText.isEmpty() && !searchText.equals("Search")) {
                    List<Book> searchResults = db.findBookByTitle(searchText, this.currentSortCriteria);
                    booksPanelUI.displayBooks(searchResults);
                } else {
                    booksPanelUI.displayBooks(db.getAllBook(this.currentSortCriteria));
                }
            }else {
                if (!searchText.isEmpty() && !searchText.equals("Search")) {
                    List<Book> searchResults = db.findBookByAuthor(searchText, this.currentSortCriteria);
                    booksPanelUI.displayBooks(searchResults);
                } else {
                    booksPanelUI.displayBooks(db.getAllBook(this.currentSortCriteria));
                }
            }
        });
        return rightPanel;
    }

    private JPanel inizializzaSezioneSX(){
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titolo programma sopra i bottoni
        JLabel TextLabel = new JLabel("Gestore Libreria");
        TextLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        TextLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Proprietà dei bottoni
        JPanel StatoBottoni = new JPanel();
        StatoBottoni.setLayout(new BoxLayout(StatoBottoni, BoxLayout.Y_AXIS));
        StatoBottoni.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton AllBtn = new JButton("All");
        JButton lettiBtn = new JButton("Letti");
        JButton inLetturaBtn = new JButton("In lettura");
        JButton daLeggereBtn = new JButton("Da leggere");

        Dimension buttonSize = new Dimension(160, 35);
        Color selectedColor = new Color(53, 53, 53);
        Color defaultColor = new Color(30, 30, 30);

        //JButton[] stateButtons = {AllBtn,lettiBtn, inLetturaBtn, daLeggereBtn};

        List<JButton> stateButtons = new ArrayList<>();
        stateButtons.add(AllBtn);
        stateButtons.add(lettiBtn);
        stateButtons.add(inLetturaBtn);
        stateButtons.add(daLeggereBtn);

        for (JButton btn : stateButtons) {
            btn.setPreferredSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setBackground(defaultColor);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMargin(new Insets(0, 10, 0, 0));
            StatoBottoni.add(btn);
        }

        AllBtn.addActionListener(e -> {
            booksPanelUI.displayBooks(db.getAllBook(this.currentSortCriteria)); // Mostra tutti i libri
            highlightButton(AllBtn, stateButtons, selectedColor, defaultColor); // Evidenzia il bottone
        });

        lettiBtn.addActionListener(e -> {
            booksPanelUI.displayBooks(db.filterBookByReadingState("LETTO", this.currentSortCriteria)); // Filtra per "LETTO"
            highlightButton(lettiBtn, stateButtons, selectedColor, defaultColor);
        });

        inLetturaBtn.addActionListener(e -> {
            booksPanelUI.displayBooks(db.filterBookByReadingState("IN LETTURA", this.currentSortCriteria)); // Filtra per "IN LETTURA"
            highlightButton(inLetturaBtn, stateButtons, selectedColor, defaultColor);
        });

        daLeggereBtn.addActionListener(e -> {
            booksPanelUI.displayBooks(db.filterBookByReadingState("DA LEGGERE", this.currentSortCriteria)); // Filtra per "DA LEGGERE"
            highlightButton(daLeggereBtn, stateButtons, selectedColor, defaultColor);
        });

        // Imposta il bottone "Tutti i Libri" come selezionato di default all'avvio
        highlightButton(AllBtn, stateButtons, selectedColor, defaultColor);

//        JLabel ratingLabel = new JLabel();
//        StatoBottoni.add(ratingLabel);

        JPanel ratingPanel = new JPanel(); // Pannello per le "stelline"
        ratingPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Allineamento a sinistra

        for (int i = 1; i <= 5; i++) {
            JButton starButton = new JButton("★");
            starButton.setFont(starButton.getFont().deriveFont(16f));
            starButton.setForeground(Color.ORANGE);
            starButton.setBackground(new Color(30, 30, 30));
            starButton.setFocusPainted(false);
            starButton.setBorderPainted(false);
            starButton.setOpaque(true);
            starButton.setMargin(new Insets(0, 0, 0, 0));

            final int currentRating = i; // Per l'uso nella lambda
            starButton.addActionListener(e -> {
                booksPanelUI.displayBooks(db.filterBookByRating(currentRating, this.currentSortCriteria));
                highlightButton(starButton, stateButtons, selectedColor, defaultColor); // Evidenzia la stella
            });
            ratingPanel.add(starButton);
            stateButtons.add(starButton); // Aggiungi il bottone alla lista
        }
        leftPanel.add(ratingPanel);
        //titolo applicazione
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(TextLabel, BorderLayout.NORTH);
        titlePanel.add(StatoBottoni, BorderLayout.CENTER);

        //Bottone addBook
        JButton addBookBtn = addBookButton();

        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeftPanel.add(addBookBtn);
        leftPanel.add(titlePanel, BorderLayout.NORTH);
        leftPanel.add(bottomLeftPanel, BorderLayout.SOUTH);

        return leftPanel;

    }

    private void highlightButton(JButton selectedButton, List<JButton> buttons, Color selectedColor, Color defaultColor) {
        for (JButton btn : buttons) {
            btn.setBackground(defaultColor);
        }
        selectedButton.setBackground(selectedColor);
    }

    private JButton addBookButton(){
        JButton addBookBtn = new JButton("Aggiungi libro");
        addBookBtn.setBackground(new Color(30, 144, 255)); // Blu
        addBookBtn.setForeground(Color.WHITE);

        //logica per il pannello per aggiungere un libro
        addBookBtn.addActionListener(e -> {
            JTextField titoloField = new JTextField(20);
            JTextField autoreField = new JTextField(20);
            JTextField isbnField = new JTextField(20);
            JTextField genreField = new JTextField(20);
            JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
            JComboBox<String> statoCombo = new JComboBox<>(new String[]{"letto", "in lettura", "da leggere"});
            JTextField imagePathField = new JTextField(15);
            imagePathField.setEditable(false);
            JButton browseBtn = new JButton("Sfoglia");

            //Immagine di copertina
            JLabel imagePreview = new JLabel();
            int width = 120;
            int height = 180;
            imagePreview.setPreferredSize(new Dimension(width, height));

            ImageIcon placeholder = loadPlaceholderImage(width, height);
            if (placeholder != null) {
                imagePreview.setIcon(placeholder);
            } else {
                imagePreview.setText("Immagine non disponibile");
            }


            JPanel imageWrapper = new JPanel();
            imageWrapper.setLayout(new BorderLayout());
            imageWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
            imageWrapper.add(imagePreview, BorderLayout.CENTER);


            //comportamento tasto sfoglia
            browseButtonAction(browseBtn, imagePathField, imagePreview, width, height);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Titolo*:"));
            panel.add(titoloField);
            panel.add(new JLabel("Autore*:"));
            panel.add(autoreField);
            panel.add(new JLabel("Genere:"));
            panel.add(genreField);
            panel.add(new JLabel("ISBN:"));
            panel.add(isbnField);
            panel.add(new JLabel("Rating (1-5):"));
            panel.add(ratingSpinner);
            panel.add(new JLabel("Stato lettura:"));
            panel.add(statoCombo);
            panel.add(new JLabel("Copertina:"));
            JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            filePanel.add(imagePathField);
            filePanel.add(browseBtn);
            panel.add(filePanel);

            // Layout generale
            JPanel BookPanel = new JPanel(new BorderLayout());
            BookPanel.add(panel, BorderLayout.CENTER);
            BookPanel.add(imageWrapper, BorderLayout.NORTH);
            imageWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));


            int result = JOptionPane.showConfirmDialog(null, BookPanel, "Nuovo libro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String titolo = titoloField.getText().trim();
                String autore = autoreField.getText().trim();
                String isbn = isbnField.getText().trim();
                String genre = genreField.getText().trim();
                int rating = (Integer) ratingSpinner.getValue();
                String stato = (String) statoCombo.getSelectedItem();
                String path = "/images/image_placeholder.png";
                if(!imagePathField.getText().trim().isEmpty()){
                    path = imagePathField.getText().trim();
                }

                if (!titolo.isEmpty() && !autore.isEmpty()) {
                    Book nuovoLibro = new Book.Builder(titolo, autore).isbn(isbn)
                            .rating(rating)
                            .readingState(stato)
                            .coverPath(path)
                            .genre(genre)
                            .build();
                    System.out.println("Creato libro: " + nuovoLibro.toString());
                    db.addBook(nuovoLibro);
                } else {
                    JOptionPane.showMessageDialog(null, "Titolo e autore sono obbligatori.");
                }
            }
        });
        return addBookBtn;
    }

    public static ImageIcon loadPlaceholderImage(int width, int height) {
        try {
            java.io.InputStream imageStream = GestoreLibreriaUI.class.getResourceAsStream("/images/image_placeholder.png");
            if (imageStream != null) {
                ImageIcon icon = new ImageIcon(javax.imageio.ImageIO.read(imageStream));
                Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            } else {
                System.err.println("Immagine placeholder non trovata nel JAR");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'immagine placeholder: " + e.getMessage());
            return null;
        }
    }

    private void browseButtonAction(JButton browseBtn, JTextField imagePathField, JLabel imagePreview, int width, int height){
        browseBtn.addActionListener(ev -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());

                ImageIcon selectedIcon = loadAndScaleImage(selectedFile.getAbsolutePath(), width, height);
                if (selectedIcon != null) {
                    imagePreview.setIcon(selectedIcon); // sostituisce il placeholder
                } else {
                    JOptionPane.showMessageDialog(null, "Impossibile caricare l'immagine.");
                }
            }
        });
    }

    public static ImageIcon loadAndScaleImage(String path, int width, int height) {
        try {
            // Se è un percorso delle risorse, carica dal JAR
            if (path != null && (path.startsWith("/images/") || path.equals("/images/image_placeholder.png"))) {
                java.io.InputStream imageStream = GestoreLibreriaUI.class.getResourceAsStream(path);
                if (imageStream != null) {
                    ImageIcon icon = new ImageIcon(javax.imageio.ImageIO.read(imageStream));
                    Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
            }

            // Altrimenti, carica come file esterno
            if (path != null && !path.isEmpty()) {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
            }

            return null;
        } catch (Exception e) {
            System.err.println("Errore nel caricamento immagine da: " + path + ". " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Impossibile caricare FlatLaf");
        }
        BookRepositoryImplementor repo = new SQLiteBookRepository();
        ConcreteBookManager db = new ConcreteBookManager(repo);
        SwingUtilities.invokeLater(() -> {
            GestoreLibreriaUI UI = new GestoreLibreriaUI(db);
            UI.setVisible(true);
        });
    }

}
