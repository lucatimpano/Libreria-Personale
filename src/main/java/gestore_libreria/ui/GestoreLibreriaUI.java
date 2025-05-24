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

    BookRepositoryImplementor repo = new SQLiteBookRepository();
    BookManager db = new ConcreteBookManager(repo);

    public GestoreLibreriaUI(){
        super("Gestore Libreria");
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
        JButton addBookBtn = new JButton("Aggiungi libro");
        addBookBtn.setBackground(new Color(30, 144, 255)); // Blu
        addBookBtn.setForeground(Color.WHITE);
    }

    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Impossibile caricare FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            GestoreLibreriaUI UI = new GestoreLibreriaUI();
            UI.setVisible(true);
        });
    }

}
