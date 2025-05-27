package gestore_libreria.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import com.formdev.flatlaf.themes.*;
import gestore_libreria.db.BookManager;
import gestore_libreria.db.BookRepositoryImplementor;
import gestore_libreria.db.ConcreteBookManager;
import gestore_libreria.db.SQLiteBookRepository;
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

        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        //sezione sx
        JPanel leftPanel = inizializzaSezioneSX();

        JPanel rightPanel = inizializzaSezioneDX();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        this.booksPanelUI = new BooksPanelUI();
        rightPanel.add(booksPanelUI);

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
