package gestore_libreria.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.formdev.flatlaf.themes.*;
import gestore_libreria.db.*;
import gestore_libreria.model.Book;
import gestore_libreria.observer.BookObserver;
import gestore_libreria.observer.ConcreteBookObserver;

public class GestoreLibreriaUI extends JFrame{

    private ConcreteBookManager db;
    private ConcreteBookObserver bookObserver;
    private BooksPanelUI booksPanelUI;

    public GestoreLibreriaUI(ConcreteBookManager db){
        super("Gestore Libreria");
        this.db = db;
        inizializzaUI();
    }

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

        this.booksPanelUI = new BooksPanelUI();
        rightPanel.add(booksPanelUI, BorderLayout.CENTER);

        booksPanelUI.setOnBookClickListener(this::showBookDetails);
        booksPanelUI.setOnDeleteBookListener(this::showPopupMenu);

        this.bookObserver = new ConcreteBookObserver(this.booksPanelUI,this.db);

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
            ImageIcon placeholder = new ImageIcon("src/main/resources/images/image_placeholder.png");
            imagePreview.setIcon(new ImageIcon(placeholder.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
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
        titoloField.setEditable(false);
        autoreField.setEditable(false);
        isbnField.setEditable(false);
        genreField.setEditable(false);
        ratingSpinner.setEnabled(false);
        statoCombo.setEnabled(false);

        JOptionPane.showConfirmDialog(this, BookPanel, "Dettagli Libro", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void showPopupMenu(Book book) {
        //TODO
        System.out.println("Richiesta di eliminazione per il libro: " + book.getTitle());
    }

    private JMenuBar creaMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");

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
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem redo = new JMenuItem("Redo");

        fileMenu.add(exportDB);
        fileMenu.add(importDB);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        editMenu.add(undo);
        editMenu.add(redo);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
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
                booksPanelUI.displayBooks(db.getAllBook());
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
                    bookObserver = new ConcreteBookObserver(this.booksPanelUI,this.db);

                    booksPanelUI.displayBooks(db.getAllBook());
                }catch (IOException | SQLException e){
                    JOptionPane.showMessageDialog(this, "Errore durante l'importazione del database: " + e.getMessage(), "Errore Importazione", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    private JPanel inizializzaSezioneDX(){
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Barra di ricerca con FlatLaf arrotondata
        JTextField searchField = new JTextField("Search");
        searchField.putClientProperty("JTextField.roundRect", true);
        searchField.setBackground(new Color(53, 53, 53));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setPreferredSize(new Dimension(150, 30));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rightPanel.add(searchField, BorderLayout.NORTH);

        // Listener per la ricerca
        searchField.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty() && !searchText.equals("Search")) {
                List<Book> searchResults = db.findBookByTitle(searchText);
                booksPanelUI.displayBooks(searchResults);
            } else {
                booksPanelUI.displayBooks(db.getAllBook());
            }
        });

        // Lista dei libri dal database
//        JPanel bookListPanel = new JPanel();
//        bookListPanel.setLayout(new BoxLayout(bookListPanel, BoxLayout.Y_AXIS));
//
//        getAllBook(bookListPanel,rightPanel);

        return rightPanel;
    }

    //!Metodo non usato, da rimuovere
    private void getAllBook(JPanel bookListPanel, JPanel rightPanel){

        List<Book> books = db.getAllBook();
        for (Book b : books) {
            JPanel bookPanel = new JPanel(new BorderLayout());
            bookPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

            // Copertina
            JLabel coverLabel = new JLabel();
            coverLabel.setPreferredSize(new Dimension(60, 90));
            coverLabel.setOpaque(true);
            coverLabel.setBackground(Color.LIGHT_GRAY);
            // carica immagine se presente
            if (!b.getCoverPath().isBlank()) {
                ImageIcon icon = new ImageIcon(b.getCoverPath());
                Image img = icon.getImage().getScaledInstance(60, 90, Image.SCALE_SMOOTH);
                coverLabel.setIcon(new ImageIcon(img));
                coverLabel.setBackground(null);
            }
            bookPanel.add(coverLabel, BorderLayout.WEST);

            // Info titolo e autore
            JLabel infoLabel = new JLabel(b.getTitle() + " - " + b.getAuthor());
            infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            bookPanel.add(infoLabel, BorderLayout.CENTER);

            bookListPanel.add(bookPanel);
        }
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

        JButton[] stateButtons = {AllBtn,lettiBtn, inLetturaBtn, daLeggereBtn};

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

        // Gestione selezione
//        for (JButton btn : stateButtons) {
//            btn.addActionListener(e -> {
//                for (JButton b : stateButtons) {
//                    b.setBackground(defaultColor);
//                }
//                btn.setBackground(selectedColor);
//            });
//        }

        //gestione selezione2
        AllBtn.addActionListener(e -> {
            booksPanelUI.displayBooks(db.getAllBook()); // Mostra tutti i libri
            highlightButton(AllBtn, stateButtons, selectedColor, defaultColor); // Evidenzia il bottone
        });

        lettiBtn.addActionListener(e -> {
            booksPanelUI.displayBooks(db.filterBookByReadingState("LETTO")); // Filtra per "LETTO"
            highlightButton(lettiBtn, stateButtons, selectedColor, defaultColor);
        });

        inLetturaBtn.addActionListener(e -> {
            booksPanelUI.displayBooks(db.filterBookByReadingState("IN LETTURA")); // Filtra per "IN LETTURA"
            highlightButton(inLetturaBtn, stateButtons, selectedColor, defaultColor);
        });

        daLeggereBtn.addActionListener(e -> {
            booksPanelUI.displayBooks(db.filterBookByReadingState("DA LEGGERE")); // Filtra per "DA LEGGERE"
            highlightButton(daLeggereBtn, stateButtons, selectedColor, defaultColor);
        });

        // Imposta il bottone "Tutti i Libri" come selezionato di default all'avvio
        highlightButton(AllBtn, stateButtons, selectedColor, defaultColor);

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

    private void highlightButton(JButton selectedButton, JButton[] buttons, Color selectedColor, Color defaultColor) {
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

            ImageIcon placeholder = new ImageIcon("src/main/resources/images/image_placeholder.png");
            imagePreview.setIcon(new ImageIcon(placeholder.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));


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
                String path = "src/main/resources/images/image_placeholder.png";
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

    private ImageIcon loadAndScaleImage(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento immagine: " + e.getMessage());
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
