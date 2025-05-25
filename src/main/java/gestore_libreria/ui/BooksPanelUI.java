package gestore_libreria.ui;

import gestore_libreria.model.Book;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;


public class BooksPanelUI extends JPanel {
    private JPanel bookListPanel;
    private Consumer<Book> onBookClickListener;

    public BooksPanelUI() {
        setLayout(new BorderLayout());
        //setBorder(BorderFactory.createTitledBorder("Lista Libri"));

        bookListPanel = new JPanel();
        bookListPanel.setLayout(new BoxLayout(bookListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(bookListPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setOnBookClickListener(Consumer<Book> onBookClickListener) {
        this.onBookClickListener = onBookClickListener;
    }

    //metodo che verrà richiamato per aggiornare la lista dei libri con l'observer
    public void displayBooks(List<Book> books) {
        bookListPanel.removeAll();
        if (books.isEmpty()) {
            JLabel noBooksLabel = new JLabel("Nessun libro presente nella libreria.");
            noBooksLabel.setForeground(Color.WHITE);
            noBooksLabel.setHorizontalAlignment(SwingConstants.CENTER);
            bookListPanel.add(noBooksLabel);
        } else {
            for (Book book : books) {
                JPanel bookPanel = new JPanel(new BorderLayout());
                bookPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
                bookPanel.setForeground(Color.WHITE); // Colore del testo per i pannelli dei libri

                // Copertina
                JLabel coverLabel = new JLabel();
                coverLabel.setPreferredSize(new Dimension(70, 100));
                coverLabel.setOpaque(true);
                coverLabel.setBackground(Color.LIGHT_GRAY); // Colore di sfondo per l'area della copertina

                //caricamento immagine
                try {
                    ImageIcon icon = new ImageIcon(book.getCoverPath());
                    // Verifica se l'immagine è stata caricata correttamente (larghezza > -1)
                    if (icon.getIconWidth() == -1) {
                        System.err.println("Impossibile caricare l'immagine da: " + book.getCoverPath() + ". File non valido o corrotto.");
                        coverLabel.setText("Err Img"); // Testo placeholder se l'immagine non è valida
                        coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        coverLabel.setForeground(Color.RED); // Per evidenziare l'errore
                        coverLabel.setBackground(Color.DARK_GRAY); // Sfondo per il testo di errore
                    } else {
                        Image img = icon.getImage().getScaledInstance(70, 100, Image.SCALE_SMOOTH);
                        coverLabel.setIcon(new ImageIcon(img));
                        coverLabel.setBackground(null); // Rimuovi il background grigio se c'è un'immagine valida
                    }
                } catch (Exception e) {
                    System.err.println("Errore durante il caricamento immagine: " + book.getCoverPath() + " - " + e.getMessage());
                    coverLabel.setText("Err Img"); // Testo placeholder in caso di errore
                    coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    coverLabel.setForeground(Color.RED);
                    coverLabel.setBackground(Color.DARK_GRAY);
                }
                bookPanel.add(coverLabel, BorderLayout.WEST);
                JLabel infoLabel = new JLabel("<html><b>" + book.getTitle() + "</b><br/><i>" + book.getAuthor() + "</i></html>");
                infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                infoLabel.setForeground(Color.WHITE); // Colore del testo
                bookPanel.add(infoLabel, BorderLayout.CENTER);

                bookListPanel.add(bookPanel);
                bookListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            }
        }
        bookListPanel.revalidate();
        bookListPanel.repaint();
    }
}
