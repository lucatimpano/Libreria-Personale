package gestore_libreria.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import com.formdev.flatlaf.themes.*;
import gestore_libreria.db.BookManager;
import gestore_libreria.db.BookRepositoryImplementor;
import gestore_libreria.db.ConcreteBookManager;
import gestore_libreria.db.SQLiteBookRepository;
import gestore_libreria.model.Book;

public class GestoreLibreriaUI extends JFrame{

    BookManager db;

    public GestoreLibreriaUI(BookManager db){
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

        mainPanel.add(leftPanel, BorderLayout.WEST);

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

        JButton lettoBtn = new JButton("All");
        JButton inLetturaBtn = new JButton("Reading");
        JButton daLeggereBtn = new JButton("Unread");

        Dimension buttonSize = new Dimension(160, 35);
        Color selectedColor = new Color(53, 53, 53);
        Color defaultColor = new Color(30, 30, 30);

        JButton[] stateButtons = {lettoBtn, inLetturaBtn, daLeggereBtn};

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
        for (JButton btn : stateButtons) {
            btn.addActionListener(e -> {
                for (JButton b : stateButtons) {
                    b.setBackground(defaultColor);
                }
                btn.setBackground(selectedColor);
            });
        }

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
                String path = imagePathField.getText().trim();

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
        BookManager db = new ConcreteBookManager(repo);
        SwingUtilities.invokeLater(() -> {
            GestoreLibreriaUI UI = new GestoreLibreriaUI(db);
            UI.setVisible(true);
        });
    }

}
