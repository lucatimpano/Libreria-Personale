package gestore_libreria.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.*;
import gestore_libreria.db.BookManager;
import gestore_libreria.db.BookRepositoryImplementor;
import gestore_libreria.db.ConcreteBookManager;
import gestore_libreria.db.SQLiteBookRepository;
import gestore_libreria.model.Book;

import javax.swing.*;

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
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titolo programma sopra i bottoni
        JLabel TextLabel = new JLabel("Gestore Libreria");
        TextLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        TextLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(TextLabel, BorderLayout.NORTH);

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

        titlePanel.add(StatoBottoni, BorderLayout.CENTER);

        // Bottone addBook
        JButton addBookBtn = addBookButton();
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeftPanel.add(addBookBtn);
        leftPanel.add(titlePanel, BorderLayout.NORTH);
        leftPanel.add(bottomLeftPanel, BorderLayout.SOUTH);
        mainPanel.add(leftPanel, BorderLayout.WEST);

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

            browseBtn.addActionListener(ev -> {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    imagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            });

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

            int result = JOptionPane.showConfirmDialog(null, panel, "Nuovo libro", JOptionPane.OK_CANCEL_OPTION);
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
