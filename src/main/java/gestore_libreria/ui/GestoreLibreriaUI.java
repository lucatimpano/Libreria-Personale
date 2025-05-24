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
    }

}
